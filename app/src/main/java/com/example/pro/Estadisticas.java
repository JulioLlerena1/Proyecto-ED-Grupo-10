package com.example.pro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import modelo.Aeropuerto;
import modelo.Vertex;


public class Estadisticas extends AppCompatActivity {

    private TextView tvConexiones, tvMasConectado, tvMenosConectado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estadisticas);


        // Referencias a los TextView
        tvConexiones = findViewById(R.id.textView3);
        tvMasConectado = findViewById(R.id.textView5);
        tvMenosConectado = findViewById(R.id.textView7);

        // Obtener aeropuerto del Intent
        Aeropuerto aeropuerto = (Aeropuerto) getIntent().getSerializableExtra("aeropuerto");

        mostrarNumeroConexiones(aeropuerto);
        mostrarAeropuertoMasConectado();
        mostrarAeropuertoMenosConectado();
    }



    public void mostrarNumeroConexiones(Aeropuerto aeropuerto) {
        Vertex<Aeropuerto, Double> vertice = MainActivity.graph.findVertex(aeropuerto);
        if (vertice != null) {
            tvConexiones.setText(String.valueOf(vertice.getEdges().size()));
        } else {
            tvConexiones.setText("Aeropuerto no encontrado");
        }
    }



    public void mostrarAeropuertoMasConectado() {
        Vertex<Aeropuerto, Double> masConectado = null;
        for (Vertex<Aeropuerto, Double> v : MainActivity.graph.getVertices()) {
            if (masConectado == null || v.getEdges().size() > masConectado.getEdges().size()) {
                masConectado = v;
            }
        }
        if (masConectado != null) {
            tvMasConectado.setText(masConectado.getValue().getNombreCompleto() + " (" + masConectado.getEdges().size() + " conexiones)");
        }
    }


    public void mostrarAeropuertoMenosConectado() {
        Vertex<Aeropuerto, Double> menosConectado = null;
        for (Vertex<Aeropuerto, Double> v : MainActivity.graph.getVertices()) {
            if (menosConectado == null || v.getEdges().size() < menosConectado.getEdges().size()) {
                menosConectado = v;
            }
        }
        if (menosConectado != null) {
            tvMenosConectado.setText(menosConectado.getValue().getNombreCompleto()
                    + " (" + menosConectado.getEdges().size() + " conexiones)");
        }
    }

    public void btnVolver(View view){
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}