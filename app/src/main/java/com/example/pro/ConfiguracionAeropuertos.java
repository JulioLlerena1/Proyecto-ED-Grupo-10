package com.example.pro;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import modelo.Aeropuerto;
import modelo.Conexion;
import modelo.Vuelo;

public class ConfiguracionAeropuertos extends AppCompatActivity {

    private TableLayout table;
    private Button agregarAeropuerto;
    private ArrayList<Aeropuerto> aeropuertos;
    private ArrayList<Conexion> conexiones;
    private ArrayList<Vuelo> vuelos;




    private ActivityResultLauncher<Intent> launcherAgregar = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    aeropuertos = result.getData().getParcelableArrayListExtra("LISTA_AEROPUERTOS");
                    conexiones = result.getData().getParcelableArrayListExtra("LISTA_CONEXIONES");
                    vuelos = result.getData().getParcelableArrayListExtra("LISTA_VUELOS");
                    mostrarAeropuertosEnTabla(aeropuertos);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuracion_aeropuertos);

        table = findViewById(R.id.tableLayoutAeropuertos);
        agregarAeropuerto = findViewById(R.id.btn_agregarAeropuerto);


        conexiones = getIntent().getParcelableArrayListExtra("LISTA_CONEXIONES");
        if (conexiones == null) {
            conexiones = new ArrayList<>();
        }

        aeropuertos = new ArrayList<>();

        aeropuertos = getIntent().getParcelableArrayListExtra("AEROPUERTO_AGREGADO");
        vuelos = getIntent().getParcelableArrayListExtra("LISTA_VUELOS");


        if (aeropuertos == null) {

            aeropuertos = getIntent().getParcelableArrayListExtra("LISTA_AEROPUERTOS");

        }

        if (aeropuertos != null) {
            mostrarAeropuertosEnTabla(aeropuertos);
        }

    }

    public void regresar(View view) {
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra("LISTA_AEROPUERTOS", aeropuertos);
        resultIntent.putParcelableArrayListExtra("LISTA_CONEXIONES", conexiones);
        resultIntent.putParcelableArrayListExtra("LISTA_VUELOS", vuelos);
        setResult(RESULT_OK, resultIntent);
        finish(); // vuelve a MainActivity existente
    }

    public void introAgregarAero(View view){
        Intent intent = new Intent(this, AgregarAeropuerto.class);
        intent.putParcelableArrayListExtra("LISTA_AEROPUERTOS", aeropuertos);
        intent.putParcelableArrayListExtra("LISTA_CONEXIONES", conexiones);
        intent.putParcelableArrayListExtra("LISTA_VUELOS", vuelos);
        launcherAgregar.launch(intent);
    }

    private void mostrarAeropuertosEnTabla(ArrayList<Aeropuerto> aeropuertos) {
        table.removeAllViews();

        TableRow headerRow = new TableRow(this);

        TextView headerNombre = new TextView(this);
        headerNombre.setText("Nombre");
        headerNombre.setPadding(8, 8, 8, 8);
        headerRow.addView(headerNombre);

        TextView headerAccion = new TextView(this);
        headerAccion.setText("Acción");
        headerAccion.setPadding(8, 8, 8, 8);
        headerRow.addView(headerAccion);

        table.addView(headerRow);

        for (Aeropuerto aeropuerto : aeropuertos) {
            TableRow dataRow = new TableRow(this);
            TextView nombreTextView = new TextView(this);
            nombreTextView.setText(aeropuerto.getNombre());
            nombreTextView.setPadding(8, 8, 8, 8);
            dataRow.addView(nombreTextView);

            Button botonEliminar = new Button(this);
            botonEliminar.setText("Eliminar");
            botonEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminarAeropuerto(aeropuerto);
                }
            });
            dataRow.addView(botonEliminar);

            table.addView(dataRow);
        }

    }

    private void eliminarAeropuerto(Aeropuerto aeropuerto) {
        if (aeropuertos != null) {
            // Eliminar aeropuerto
            aeropuertos.removeIf(a -> a.getCodigo().equalsIgnoreCase(aeropuerto.getCodigo()));

            // Eliminar todas las conexiones asociadas
            if (conexiones != null) {
                conexiones.removeIf(c ->
                        (c.getOrigen() != null && c.getOrigen().getCodigo().equalsIgnoreCase(aeropuerto.getCodigo()))
                                || (c.getDestino() != null && c.getDestino().getCodigo().equalsIgnoreCase(aeropuerto.getCodigo()))
                );
            }

            // Actualizar tabla
            mostrarAeropuertosEnTabla(aeropuertos);
        }
    }

}