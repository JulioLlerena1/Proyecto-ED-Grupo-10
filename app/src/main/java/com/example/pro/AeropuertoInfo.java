package com.example.pro;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import modelo.Aeropuerto;
import modelo.DynamicGraph;
import modelo.Vertex;
import modelo.Vuelo;


public class AeropuertoInfo  extends AppCompatActivity {

    private TextView nombre;
    private Button regresar;
    private Button ordenar;

    private TableLayout table;

    private List<Vuelo> vuelosDelAeropuerto = new ArrayList<>();
    private Aeropuerto aeropuertoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aeropuerto_info);

        nombre = findViewById(R.id.nombre);
        table = findViewById(R.id.tableLayoutVuelos);

        Intent itObjeto = getIntent();
        aeropuertoActual = (Aeropuerto) itObjeto.getSerializableExtra("aeropuerto");
        nombre.setText(aeropuertoActual.getNombre());

        cargarYMostrarVuelos();
    }

    public void regresar(View view){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }


    private void cargarYMostrarVuelos() {
        // Cargar todos los aeropuertos
        List<Aeropuerto> aeropuertos = new ArrayList<>();
        try {
            aeropuertos = Aeropuerto.cargarAeropuertos(this); // Método que carga todos los aeropuertos
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Cargar todos los vuelos
        List<Vuelo> todosVuelos = new ArrayList<>();
        try {
            todosVuelos = Vuelo.cargarVuelos(this, aeropuertos);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Filtrar solo los vuelos del aeropuerto actual
        vuelosDelAeropuerto.clear();
        for (Vuelo v : todosVuelos) {
            if (v.getPartida().getCodigo().equalsIgnoreCase(aeropuertoActual.getCodigo())) {
                vuelosDelAeropuerto.add(v);
            }
        }

        // Mostrar sin ordenar
        mostrarVuelos(vuelosDelAeropuerto);
    }



    private void mostrarVuelos(List<Vuelo> lista) {
        table.removeAllViews();

        // encabezado
        TableRow header = new TableRow(this);
        header.addView(crearTextView("Destino", true));
        header.addView(crearTextView("Hora Salida", true));
        header.addView(crearTextView("Hora Llegada", true));
        table.addView(header);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        // agregar filas
        for (Vuelo v : lista) {
            TableRow row = new TableRow(this);
            row.addView(crearTextView(v.getDestino().getNombre(), false));
            row.addView(crearTextView(sdf.format(v.getHoraI()), false));
            row.addView(crearTextView(sdf.format(v.getHoraF()), false));

            row.setTag(v);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Vuelo vueloSeleccionado = (Vuelo) view.getTag();
                    Aeropuerto origen = vueloSeleccionado.getPartida();
                    Aeropuerto destino = vueloSeleccionado.getDestino();

                    DynamicGraph grafo = MainActivity.graph;
                    List<Vertex<Aeropuerto, Double>> camino = grafo.dijkstra(origen, destino);

                    if (camino.isEmpty()) {
                        Toast.makeText(AeropuertoInfo.this, "No hay ruta disponible + " + grafo.getVertices().size() , Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<String> codigosRuta = new ArrayList<>();
                    for (Vertex<Aeropuerto, Double> v : camino) {
                        codigosRuta.add(v.getValue().getCodigo());
                    }

                    Intent intent = new Intent(AeropuertoInfo.this, RutaCorta.class);
                    intent.putStringArrayListExtra("ruta", codigosRuta);
                    startActivity(intent);
                }
            });
            table.addView(row);
        }
    }

    private TextView crearTextView(String texto, boolean bold) {

        TextView tv = new TextView(this);
        tv.setText(texto);

        if (bold) {
            tv.setTypeface(null, Typeface.BOLD);
        }

        tv.setPadding(16, 16, 16, 16);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        );
        tv.setLayoutParams(params);

        return tv;
    }

    public void ordenarPorHorario(View view) {

        TreeSet<Vuelo> vuelosOrdenados = new TreeSet<>(new Comparator<Vuelo>() {
            @Override
            public int compare(Vuelo v1, Vuelo v2) {
                int cmp = v1.getHoraI().compareTo(v2.getHoraI());
                if (cmp == 0) {
                    // Si las horas son iguales, diferenciar por destino para no perder vuelos
                    return v1.getDestino().getCodigo().compareTo(v2.getDestino().getCodigo());
                }
                return cmp;
            }
        });

        // Agregar todos los vuelos del aeropuerto actual
        vuelosOrdenados.addAll(vuelosDelAeropuerto);

        // Mostrar en TableLayout
        mostrarVuelos(new ArrayList<>(vuelosOrdenados));
    }

}