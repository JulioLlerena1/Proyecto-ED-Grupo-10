package com.example.pro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import modelo.Aeropuerto;
import modelo.Vuelo;

public class ConfiguracionVuelos extends AppCompatActivity {

    private TableLayout table;
    private Button agregarVuelo;
    private ArrayList<Aeropuerto> aeropuertos;
    private ArrayList<Vuelo> vuelos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuracion_vuelos);

        table = findViewById(R.id.tableLayoutConfVuelos);
        agregarVuelo = findViewById(R.id.btn_agregarVuelo);

        aeropuertos = new ArrayList<>();
        vuelos = new ArrayList<>();

        vuelos = (ArrayList<Vuelo>) getIntent().getSerializableExtra("VUELO_AGREGADO");

        if (vuelos == null) {

            aeropuertos = (ArrayList<Aeropuerto>) getIntent().getSerializableExtra("LISTA_AEROPUERTOS");
            vuelos = (ArrayList<Vuelo>) getIntent().getSerializableExtra("LISTA_VUELOS");

        }

        if (aeropuertos != null) {
        }

    }
    public void regresar(View view){

        Intent intent=new Intent(this,MainActivity.class);
        intent.putParcelableArrayListExtra("LISTA_AEROPUERTOS",aeropuertos);
        intent.putParcelableArrayListExtra("LISTA_VUELOS",vuelos);

        startActivity(intent);
    }
}