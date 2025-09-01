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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import modelo.Aeropuerto;
import modelo.Conexion;
import modelo.DynamicGraph;
import modelo.Vertex;
import modelo.Vuelo;

public class ConfiguracionVuelos extends AppCompatActivity {

    private TableLayout table;
    private Button agregarVuelo;
    private ArrayList<Aeropuerto> aeropuertos;
    private ArrayList<Vuelo> vuelos;
    private ArrayList<Conexion> conexiones;
    private Vuelo vueloagg;
    private ArrayList<Vuelo> vueloedit;

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    vuelos = result.getData().getParcelableArrayListExtra("LISTA_VUELOS");
                    conexiones = result.getData().getParcelableArrayListExtra("LISTA_CONEXIONES");
                    vueloagg = (Vuelo) result.getData().getSerializableExtra("VUELO_AGREGADO");
                    vueloedit = result.getData().getParcelableArrayListExtra("VUELO_EDITADO");
                    if(vueloagg != null){
                        vuelos.add(vueloagg);

                        Random random = new Random();
                        int colorAleatorio = 0xFF000000
                                | (random.nextInt(256) << 16)
                                | (random.nextInt(256) << 8)
                                | random.nextInt(256);

                        conexiones.add(new Conexion(vueloagg.getPartida(), vueloagg.getDestino(),colorAleatorio));

                    }

                    if(vueloedit != null ){

                        vuelos.removeIf(a -> a.equals(vueloedit.get(1)));

                        vuelos.add(vueloedit.get(0));

                        Random random = new Random();
                        int colorAleatorio = 0xFF000000
                                | (random.nextInt(256) << 16)
                                | (random.nextInt(256) << 8)
                                | random.nextInt(256);

                        conexiones.add(new Conexion(vueloedit.get(0).getPartida(), vueloedit.get(0).getDestino(),colorAleatorio));

                    }
                    mostrarVuelos(vuelos);
                }
            }
    );

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
        aeropuertos = (ArrayList<Aeropuerto>) getIntent().getSerializableExtra("LISTA_AEROPUERTOS");
        Vuelo vueloagg = (Vuelo) getIntent().getSerializableExtra("VUELO_AGREGADO");


        if (vuelos == null) {

            vuelos = (ArrayList<Vuelo>) getIntent().getSerializableExtra("LISTA_VUELOS");

        }

        if (vuelos != null) {

            mostrarVuelos(vuelos);

        }

        if(vueloagg != null){

            vuelos.add(vueloagg);

        }

    }
    public void regresar(View view){

        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra("LISTA_VUELOS", vuelos);
        resultIntent.putParcelableArrayListExtra("LISTA_CONEXIONES", conexiones);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void introAgregarVuelo(View view){
        Intent intent = new Intent(this, AgregarVuelo.class);
        intent.putParcelableArrayListExtra("LISTA_AEROPUERTOS",aeropuertos);
        intent.putParcelableArrayListExtra("LISTA_VUELOS",vuelos);
        launcher.launch(intent);
    }

    private void mostrarVuelos(List<Vuelo> lista) {
        table.removeAllViews();

        TableRow header = new TableRow(this);
        header.addView(crearTextView("Partida", true));
        header.addView(crearTextView("Destino", true));
        header.addView(crearTextView("Hora Salida", true));
        header.addView(crearTextView("Hora Llegada", true));
        header.addView(crearTextView("Accion", true));
        table.addView(header);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        for (Vuelo v : lista) {
            TableRow row = new TableRow(this);
            row.addView(crearTextView(v.getPartida().getNombre(), false));
            row.addView(crearTextView(v.getDestino().getNombre(), false));
            row.addView(crearTextView(sdf.format(v.getHoraI()), false));
            row.addView(crearTextView(sdf.format(v.getHoraF()), false));

            row.setTag(v);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ConfiguracionVuelos.this, EditarVuelo.class);
                    intent.putExtra("VUELO_EDITADO", v);
                    intent.putParcelableArrayListExtra("LISTA_AEROPUERTOS",aeropuertos);
                    startActivity(intent);
                }
            });

            Button botonEliminar = new Button(this);
            botonEliminar.setText("Eliminar");
            botonEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    eliminarVuelo(v);
                }
            });
            row.addView(botonEliminar);

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

    public void eliminarVuelo(Vuelo v){

        if (this.vuelos != null) {

            this.vuelos.removeIf(a -> a.equals(v));

            mostrarVuelos(this.vuelos);
        }

    }

}