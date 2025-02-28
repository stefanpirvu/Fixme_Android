package com.example.fixme_android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String API_URL = "https://mi-api-php-stefans-projects-6bba87b0.vercel.app/login";

    private EditText editText_recogerCorreo;
    private EditText editText_recogerContrasenia;
    private Button button_IniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_recogerCorreo = findViewById(R.id.editText_recogerCorreo);
        editText_recogerContrasenia = findViewById(R.id.editText_recogerContrasenia);
        button_IniciarSesion = findViewById(R.id.button_IniciarSesion);

        button_IniciarSesion.setOnClickListener(v -> {
            String email = editText_recogerCorreo.getText().toString().trim();
            String password = editText_recogerContrasenia.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                try {
                    URL url = new URL(API_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    String jsonInput = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
                    OutputStream os = conn.getOutputStream();
                    os.write(jsonInput.getBytes());
                    os.flush();
                    os.close();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            response.append(line);
                        }
                        in.close();

                        String jsonResponse = response.toString();
                        Log.d(TAG, "Respuesta de la API: " + jsonResponse);

                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        if (jsonObject.getBoolean("success")) {
                            JSONObject usuario = jsonObject.getJSONObject("usuario");
                            int idUsuario = usuario.getInt("idusuario");

                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                                // Redirigir a MainActivity_Home
                                Intent intent = new Intent(MainActivity.this, MainActivity_Home.class);
                                intent.putExtra("idUsuario", idUsuario);
                                startActivity(intent);
                                finish(); // Cierra MainActivity para que no se pueda volver atrás
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Log.e(TAG, "Error en la solicitud: " + responseCode);
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error en el servidor: " + responseCode, Toast.LENGTH_SHORT).show());
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    Log.e(TAG, "Excepción: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}