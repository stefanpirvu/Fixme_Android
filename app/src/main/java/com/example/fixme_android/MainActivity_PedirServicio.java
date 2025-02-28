package com.example.fixme_android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity_PedirServicio extends AppCompatActivity {
    private static final String API_URL = "https://mi-api-php-stefans-projects-6bba87b0.vercel.app/search_services";

    private EditText editText_recogerBusquedaPorTitulo;
    private EditText editText_recogerBusquedaPorCategoria;
    private ServicioAdapter adapter;
    private List<Servicio> serviciosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pedir_servicio);

        editText_recogerBusquedaPorTitulo = findViewById(R.id.editText_recogerBusquedaPorTitulo);
        Button button_buscarPorTitulo = findViewById(R.id.button_buscarPorTitulo);
        editText_recogerBusquedaPorCategoria = findViewById(R.id.editText_recogerBusquedaPorCategoria);
        Button button_buscarPorCategoria = findViewById(R.id.button_buscarPorCategoria);
        RecyclerView recyclerView_pedirServicio = findViewById(R.id.recyclerView_pedirServicio);

        // Configurar RecyclerView
        serviciosList = new ArrayList<>();
        adapter = new ServicioAdapter(serviciosList, this); // Pasar la actividad
        recyclerView_pedirServicio.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_pedirServicio.setAdapter(adapter);

        // Buscar por título
        button_buscarPorTitulo.setOnClickListener(v -> {
            String titulo = editText_recogerBusquedaPorTitulo.getText().toString().trim();
            if (titulo.isEmpty()) {
                Toast.makeText(this, "Ingresa un título para buscar", Toast.LENGTH_SHORT).show();
                return;
            }
            buscarServicios(titulo, null);
        });

        // Buscar por categoría
        button_buscarPorCategoria.setOnClickListener(v -> {
            String categoria = editText_recogerBusquedaPorCategoria.getText().toString().trim();
            if (categoria.isEmpty()) {
                Toast.makeText(this, "Ingresa una categoría para buscar", Toast.LENGTH_SHORT).show();
                return;
            }
            buscarServicios(null, categoria);
        });
    }

    private void buscarServicios(String titulo, String categoria) {
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

                JSONObject jsonInput = new JSONObject();
                if (titulo != null) jsonInput.put("titulo", titulo);
                if (categoria != null) jsonInput.put("categoria", categoria);
                String jsonString = jsonInput.toString();

                OutputStream os = conn.getOutputStream();
                os.write(jsonString.getBytes());
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

                    JSONObject jsonObject = new JSONObject(response.toString());
                    if (jsonObject.getBoolean("success")) {
                        JSONArray serviciosArray = jsonObject.getJSONArray("servicios");
                        List<Servicio> nuevosServicios = new ArrayList<>();
                        for (int i = 0; i < serviciosArray.length(); i++) {
                            JSONObject servicioJson = serviciosArray.getJSONObject(i);
                            Servicio servicio = new Servicio(
                                    servicioJson.getInt("idservicio"),
                                    servicioJson.getString("titulo"),
                                    servicioJson.getString("categoria"),
                                    (float) servicioJson.getDouble("precio"),
                                    servicioJson.optString("descripcion", ""),
                                    servicioJson.getInt("idusuario")
                            );
                            nuevosServicios.add(servicio);
                        }
                        runOnUiThread(() -> {
                            serviciosList.clear();
                            serviciosList.addAll(nuevosServicios);
                            adapter.notifyDataSetChanged();
                        });
                    } else {
                        runOnUiThread(() -> {
                            try {
                                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
    }
}