package com.example.fixme_android;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity_OfrecerServicio extends AppCompatActivity {
    private static final String API_URL = "https://mi-api-php-stefans-projects-6bba87b0.vercel.app/add_service";

    private EditText editText_recogerTituloNuevoServicio;
    private EditText editText_recogerCategoriaNuevoServicio;
    private EditText editText_recogerPrecioNuevoServicio;
    private EditText editTextMultiLine_recogerDescripcionNuevoServicio;
    private Button button_IngresarNuevoServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ofrecer_servicio);

        editText_recogerTituloNuevoServicio = findViewById(R.id.editText_recogerTituloNuevoServicio);
        editText_recogerCategoriaNuevoServicio = findViewById(R.id.editText_recogerCategoriaNuevoServicio);
        editText_recogerPrecioNuevoServicio = findViewById(R.id.editText_recogerPrecioNuevoServicio);
        editTextMultiLine_recogerDescripcionNuevoServicio = findViewById(R.id.editTextMultiLine_recogerDescripcionNuevoServicio);
        button_IngresarNuevoServicio = findViewById(R.id.button_IngresarNuevoServicio);

        // Obtener el idUsuario del Intent
        int idUsuario = getIntent().getIntExtra("idUsuario", -1);
        if (idUsuario == -1) {
            Toast.makeText(this, "Error: No se recibió idUsuario", Toast.LENGTH_SHORT).show();
            finish(); // Cierra la actividad si no hay idUsuario
            return;
        }

        button_IngresarNuevoServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo = editText_recogerTituloNuevoServicio.getText().toString().trim();
                String categoria = editText_recogerCategoriaNuevoServicio.getText().toString().trim();
                String precioStr = editText_recogerPrecioNuevoServicio.getText().toString().trim();
                String descripcion = editTextMultiLine_recogerDescripcionNuevoServicio.getText().toString().trim();

                if (titulo.isEmpty() || categoria.isEmpty() || precioStr.isEmpty()) {
                    Toast.makeText(MainActivity_OfrecerServicio.this, "Faltan datos obligatorios", Toast.LENGTH_SHORT).show();
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

                        String jsonInput = "{"
                                + "\"titulo\":\"" + titulo + "\","
                                + "\"categoria\":\"" + categoria + "\","
                                + "\"precio\":" + precioStr + ","
                                + "\"descripcion\":\"" + descripcion + "\","
                                + "\"idUsuario\":" + idUsuario
                                + "}";
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
                            runOnUiThread(() -> {
                                if (jsonResponse.contains("\"success\":true")) {
                                    Toast.makeText(MainActivity_OfrecerServicio.this, "Servicio añadido correctamente", Toast.LENGTH_SHORT).show();
                                    editText_recogerTituloNuevoServicio.setText("");
                                    editText_recogerCategoriaNuevoServicio.setText("");
                                    editText_recogerPrecioNuevoServicio.setText("");
                                    editTextMultiLine_recogerDescripcionNuevoServicio.setText("");
                                } else {
                                    Toast.makeText(MainActivity_OfrecerServicio.this, "Error al añadir el servicio", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(MainActivity_OfrecerServicio.this, "Error en el servidor: " + responseCode, Toast.LENGTH_SHORT).show());
                        }
                        conn.disconnect();
                    } catch (Exception e) {
                        runOnUiThread(() -> Toast.makeText(MainActivity_OfrecerServicio.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });
    }
}