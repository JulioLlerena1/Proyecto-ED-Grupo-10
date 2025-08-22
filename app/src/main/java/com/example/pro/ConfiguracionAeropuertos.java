package com.example.pro;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import modelo.Aeropuerto;

public class ConfiguracionAeropuertos extends AppCompatActivity {

    private TableLayout table;
    private Button regresar;
    private Button agregarAeropuerto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuracion_aeropuertos);

        table = findViewById(R.id.tableLayoutAeropuertos);
        regresar = findViewById(R.id.regresar);
        agregarAeropuerto = findViewById(R.id.agregarAeropuerto);
        cargarAeropuertos();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void cargarAeropuertos() {
        // Cargar todos los aeropuertos
        List<Aeropuerto> aeropuertos = new ArrayList<>();
        try {
            aeropuertos = Aeropuerto.cargarAeropuertos(this); // Método que carga todos los aeropuertos
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }



    }
}