package com.example.pro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import modelo.Aeropuerto;
import modelo.Conexion;

public class AgregarAeropuerto extends AppCompatActivity {

    private TextInputEditText editTextCodigo, editTextNombre, editTextLatitud, editTextLongitud;
    private Button btnGuardarAeropuerto;
    private MultiSelectSpinner spinnerConexiones;

    private ArrayList<Aeropuerto> aeropuertos;
    private ArrayList<Conexion> conexiones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_aeropuerto);

        editTextCodigo = findViewById(R.id.editTextCodigoAeropuerto);
        editTextNombre = findViewById(R.id.editTextNombreAeropuerto);
        editTextLatitud = findViewById(R.id.editTextLatitudAeropuerto);
        editTextLongitud = findViewById(R.id.editTextLongitudAeropuerto);
        btnGuardarAeropuerto = findViewById(R.id.btnGuardarAeropuerto);
        spinnerConexiones = findViewById(R.id.spinnerConexiones);

        aeropuertos = (ArrayList<Aeropuerto>) getIntent().getSerializableExtra("LISTA_AEROPUERTOS");
        conexiones = getIntent().getParcelableArrayListExtra("LISTA_CONEXIONES");

        List<String> nombresAeropuertos = new ArrayList<>();
        for (Aeropuerto a : aeropuertos) {
            nombresAeropuertos.add(a.getNombreCompleto() + " (" + a.getCodigo() + ")");
        }
        spinnerConexiones.setItems(nombresAeropuertos);
        spinnerConexiones.setSelection(new int[]{}); // inicialmente vacío

    }

    public void guardarAeropuerto(View view) {

        String codigo = editTextCodigo.getText().toString();
        String nombre = editTextNombre.getText().toString();
        String latitud = editTextLatitud.getText().toString();
        String longitud = editTextLongitud.getText().toString();

        Aeropuerto nuevoAeropuerto = new Aeropuerto(codigo, nombre, Double.parseDouble(latitud), Double.parseDouble(longitud));
        aeropuertos.add(nuevoAeropuerto);

        int[] seleccionados = spinnerConexiones.getSelectedIndices();
        Random random = new Random();
        for (int index : seleccionados) {
            Aeropuerto destino = aeropuertos.get(index);
            if(destino != null){
                int colorAleatorio = 0xFF000000
                        | (random.nextInt(256) << 16)
                        | (random.nextInt(256) << 8)
                        | random.nextInt(256);
                conexiones.add(new Conexion(nuevoAeropuerto, destino, colorAleatorio));
            }
        }

        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra("LISTA_AEROPUERTOS", aeropuertos);
        resultIntent.putParcelableArrayListExtra("LISTA_CONEXIONES", conexiones);
        setResult(RESULT_OK, resultIntent);
        finish(); // vuelve a ConfiguracionAeropuertos
    }


    public void regresar(View view){
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra("LISTA_AEROPUERTOS", aeropuertos);
        setResult(RESULT_OK, resultIntent);
        finish(); // vuelve a ConfiguracionAeropuertos
    }
}