package com.example.pro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import modelo.Aeropuerto;

public class AgregarAeropuerto extends AppCompatActivity {

    private TextInputEditText editTextCodigo, editTextNombre, editTextLatitud, editTextLongitud;
    private Button btnGuardarAeropuerto;

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

    }

    public void guardarAeropuerto(View view) {

        String codigo = editTextCodigo.getText().toString();
        String nombre = editTextNombre.getText().toString();
        String latitud = editTextLatitud.getText().toString();
        String longitud = editTextLongitud.getText().toString();

        Aeropuerto nuevoAeropuerto = new Aeropuerto(codigo, nombre, Double.parseDouble(latitud), Double.parseDouble(longitud));

        Intent intent = new Intent(this, ConfiguracionAeropuertos.class);
        intent.putExtra("aeropuertoNuevo", nuevoAeropuerto);
        startActivity(intent);


    }

    public void regresar(View view){
        Intent intent=new Intent(this,ConfiguracionAeropuertos.class);
        startActivity(intent);
    }
}