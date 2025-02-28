package com.example.fixme_android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity_MostrarServicioSeleccionado extends AppCompatActivity {
    private static final String API_URL_PHONE = "https://mi-api-php-stefans-projects-6bba87b0.vercel.app/get_user_phone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mostrar_servicio_seleccionado);

        TextView textView_mostratTituloServicioSeleccionado = findViewById(R.id.textView_mostratTituloServicioSeleccionado);
        TextView textView_mostrarCategoriaServicioSeleccionado = findViewById(R.id.textView_mostrarCategoriaServicioSeleccionado);
        TextView textView_mostrarPrecioServicioSeleccionado = findViewById(R.id.textView_mostrarPrecioServicioSeleccionado);
        TextView textView_mostrarDescripcionServicioSeleccionado = findViewById(R.id.textView_mostrarDescripcionServicioSeleccionado);
        Button button_llamarA_Usuario = findViewById(R.id.button_llamarA_Usuario);

        Intent intent = getIntent();
        String titulo = intent.getStringExtra("titulo");
        String categoria = intent.getStringExtra("categoria");
        float precio = intent.getFloatExtra("precio", 0.0f);
        String descripcion = intent.getStringExtra("descripcion");
        int idUsuario = intent.getIntExtra("idUsuario", -1);
        Log.d("MostrarServicio", "idUsuario recibido: " + idUsuario);

        textView_mostratTituloServicioSeleccionado.setText(titulo);
        textView_mostrarCategoriaServicioSeleccionado.setText(categoria);
        textView_mostrarPrecioServicioSeleccionado.setText("€" + precio);
        textView_mostrarDescripcionServicioSeleccionado.setText(descripcion);

        button_llamarA_Usuario.setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    URL url = new URL(API_URL_PHONE);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    String jsonInput = "{\"idUsuario\":" + idUsuario + "}";
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

                        JSONObject jsonResponse = new JSONObject(response.toString());
                        if (jsonResponse.getBoolean("success")) {
                            String telefono = jsonResponse.getString("telefono");
                            runOnUiThread(() -> {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + telefono));
                                startActivity(callIntent);
                            });
                        } else {
                            runOnUiThread(() -> {
                                try {
                                    Toast.makeText(this, jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Error en el servidor: " + responseCode, Toast.LENGTH_SHORT).show());
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).start();
        });
    }
}