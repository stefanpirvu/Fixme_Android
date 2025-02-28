package com.example.fixme_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity_Home extends AppCompatActivity {
    private int idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        // Obtener el idUsuario del Intent recibido desde MainActivity
        idUsuario = getIntent().getIntExtra("idUsuario", -1);
        if (idUsuario == -1) {
            // Si no se recibe idUsuario, algo falló en el login
            finish(); // Cierra la actividad para evitar problemas
            return;
        }

        Button button_ofrecerServicio = findViewById(R.id.button_ofrecerServicio);
        Button button_pedirServicio = findViewById(R.id.button_pedirServicio);

        button_ofrecerServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAbrir_MainActivity_OfrecerServicio_Desde_MainActivity_Home =
                        new Intent(MainActivity_Home.this, MainActivity_OfrecerServicio.class);
                intentAbrir_MainActivity_OfrecerServicio_Desde_MainActivity_Home.putExtra("idUsuario", idUsuario);
                startActivity(intentAbrir_MainActivity_OfrecerServicio_Desde_MainActivity_Home);
            }
        });

        button_pedirServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAbrir_MainActivity_PedirServicio_Desde_MainActivity_Home =
                        new Intent(MainActivity_Home.this, MainActivity_PedirServicio.class);
                intentAbrir_MainActivity_PedirServicio_Desde_MainActivity_Home.putExtra("idUsuario", idUsuario); // Opcional
                startActivity(intentAbrir_MainActivity_PedirServicio_Desde_MainActivity_Home);
            }
        });
    }
}