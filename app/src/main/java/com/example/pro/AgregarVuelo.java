package com.example.pro;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import modelo.Aeropuerto;

public class AgregarVuelo extends AppCompatActivity {

    private TextInputLayout layoutHoraInicio, layoutHoraFin, layoutNumPasajeros, layoutNumAsientos;
    private TextInputEditText editTextHoraInicio, editTextHoraFin, editTextNumPasajeros, editTextNumAsientos;
    private Spinner spinnerOrigen, spinnerDestino;
    private Button buttonGuardarVuelo;

    private ArrayList<Aeropuerto> aeropuertos;
    private static final SimpleDateFormat sdfInputHora = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_vuelo);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_agregar_vuelo);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });


        layoutNumPasajeros = findViewById(R.id.layout_num_pasajeros);
        editTextNumPasajeros = findViewById(R.id.edit_text_num_pasajeros);
        layoutNumAsientos = findViewById(R.id.layout_num_asientos);
        editTextNumAsientos = findViewById(R.id.edit_text_num_asientos);

    }
}