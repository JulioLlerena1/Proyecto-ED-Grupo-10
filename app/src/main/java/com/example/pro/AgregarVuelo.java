package com.example.pro;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import modelo.Aeropuerto;
import modelo.Conexion;
import modelo.Vuelo;

public class AgregarVuelo extends AppCompatActivity {

    private TextInputLayout layoutHoraInicio, layoutHoraFin, layoutNumPasajeros, layoutNumAsientos;
    private TextInputEditText editTextHoraInicio, editTextHoraFin, editTextNumPasajeros, editTextNumAsientos;
    private Spinner spinnerOrigen, spinnerDestino;
    private Button buttonGuardarVuelo;

    private ArrayList<Aeropuerto> aeropuertos;
    private ArrayList<Conexion> conexiones;
    private ArrayList<Vuelo> vuelos;



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


        editTextHoraInicio = findViewById(R.id.edit_text_hora_salida);
        editTextHoraFin = findViewById(R.id.edit_text_hora_llegada);
        layoutNumPasajeros = findViewById(R.id.layout_num_pasajeros);
        editTextNumPasajeros = findViewById(R.id.edit_text_num_pasajeros);
        layoutNumAsientos = findViewById(R.id.layout_num_asientos);
        editTextNumAsientos = findViewById(R.id.edit_text_num_asientos);

        spinnerOrigen = findViewById(R.id.spinner_aeropuerto_origen);
        spinnerDestino = findViewById(R.id.spinner_aeropuerto_destino);
        buttonGuardarVuelo = findViewById(R.id.button_guardar_vuelo);

        aeropuertos = new ArrayList<>();
        Intent intent = getIntent();
        aeropuertos = intent.getParcelableArrayListExtra("LISTA_AEROPUERTOS");
        conexiones = intent.getParcelableArrayListExtra("LISTA_CONEXIONES");
        vuelos = intent.getParcelableArrayListExtra("LISTA_VUELOS");


        if (aeropuertos.isEmpty()) {
            Toast.makeText(this, "No hay aeropuertos disponibles para seleccionar.", Toast.LENGTH_LONG).show();
        }

        configurarSpinnersAeropuerto();

        editTextHoraInicio.setOnClickListener(v -> mostrarTimePickerDialog(editTextHoraInicio));
        editTextHoraFin.setOnClickListener(v -> mostrarTimePickerDialog(editTextHoraFin));

        buttonGuardarVuelo.setOnClickListener(v -> guardarVuelo());

    }

    private void mostrarTimePickerDialog(TextInputEditText editTextTime) {
        Calendar calendario = Calendar.getInstance();
        int horaActual = calendario.get(Calendar.HOUR_OF_DAY);
        int minutoActual = calendario.get(Calendar.MINUTE);


        Calendar calExistente = Calendar.getInstance();
        horaActual = calExistente.get(Calendar.HOUR_OF_DAY);
        minutoActual = calExistente.get(Calendar.MINUTE);



        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    Calendar calSeleccionado = Calendar.getInstance();
                    calSeleccionado.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calSeleccionado.set(Calendar.MINUTE, minute);
                    editTextTime.setText(sdfInputHora.format(calSeleccionado.getTime()));
                }, horaActual, minutoActual, true);
        timePickerDialog.show();
    }

    private void configurarSpinnersAeropuerto() {
        if (aeropuertos == null || aeropuertos.isEmpty()) {
            List<String> listaVacia = new ArrayList<>();
            listaVacia.add("No hay aeropuertos disponibles");
            ArrayAdapter<String> adapterVacio = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listaVacia);
            adapterVacio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerOrigen.setAdapter(adapterVacio);
            spinnerDestino.setAdapter(adapterVacio);
            spinnerOrigen.setEnabled(false);
            spinnerDestino.setEnabled(false);
            return;
        }

        List<String> nombresAeropuertos = new ArrayList<>();
        for (Aeropuerto a : aeropuertos) {
            nombresAeropuertos.add(a.getNombreCompleto() + " (" + a.getCodigo() + ")");
        }

        ArrayAdapter<String> adapterAeropuertos = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                nombresAeropuertos
        );

        adapterAeropuertos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrigen.setAdapter(adapterAeropuertos);
        spinnerDestino.setAdapter(adapterAeropuertos);

        spinnerOrigen.setEnabled(true);
        spinnerDestino.setEnabled(true);
    }

    private void guardarVuelo() {
        String strHoraInicio = editTextHoraInicio.getText().toString().trim();
        String strHoraFin = editTextHoraFin.getText().toString().trim();
        String strNumPasajeros = editTextNumPasajeros.getText().toString().trim();
        String strNumAsientos = editTextNumAsientos.getText().toString().trim();

        if (aeropuertos.isEmpty() || spinnerOrigen.getSelectedItem() == null || spinnerDestino.getSelectedItem() == null) {
            Toast.makeText(this, "No hay aeropuertos disponibles para crear un vuelo.", Toast.LENGTH_LONG).show();
            return;
        }

        if (strHoraInicio.isEmpty() || strHoraFin.isEmpty() || strNumPasajeros.isEmpty() || strNumAsientos.isEmpty()||
                spinnerOrigen.getSelectedItemPosition() == Spinner.INVALID_POSITION ||
                spinnerDestino.getSelectedItemPosition() == Spinner.INVALID_POSITION ) {

            Toast.makeText(this, "Por favor, complete todos los campos y seleccione aeropuertos.", Toast.LENGTH_SHORT).show();
            return;
        }

        Date horaInicioDate, horaFinDate;
        try {
            horaInicioDate = sdfInputHora.parse(strHoraInicio);
            horaFinDate = sdfInputHora.parse(strHoraFin);
        } catch (ParseException e) {
            Toast.makeText(this, "Formato de hora inválido. Use HH:mm", Toast.LENGTH_SHORT).show();
            return;
        }

        if (horaInicioDate == null || horaFinDate == null) {
            Toast.makeText(this, "Error al procesar las horas.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (horaFinDate.before(horaInicioDate)) { // Esta validación puede ser compleja si el vuelo cruza la medianoche
                Toast.makeText(this, "La hora de fin no puede ser anterior a la hora de inicio en el mismo día.", Toast.LENGTH_SHORT).show();
                return;


        }

        int numPasajeros, numAsientos;
        try {
            numPasajeros = Integer.parseInt(strNumPasajeros);
            numAsientos = Integer.parseInt(strNumAsientos);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Número de pasajeros o asientos inválido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (numPasajeros < 0 || numAsientos <= 0) {
            Toast.makeText(this, "Pasajeros no puede ser negativo y asientos debe ser mayor que cero.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (numPasajeros > numAsientos) {
            Toast.makeText(this, "El número de pasajeros no puede exceder el número de asientos.", Toast.LENGTH_SHORT).show();
            return;
        }

        Aeropuerto aeropuertoPartidaSeleccionado = aeropuertos.get(spinnerOrigen.getSelectedItemPosition());
        Aeropuerto aeropuertoDestinoSeleccionado = aeropuertos.get(spinnerDestino.getSelectedItemPosition());

        if (aeropuertoPartidaSeleccionado.getCodigo().equals(aeropuertoDestinoSeleccionado.getCodigo())) {
            Toast.makeText(this, "El aeropuerto de partida y destino no pueden ser el mismo.", Toast.LENGTH_SHORT).show();
            return;
        }


        Vuelo nuevoVuelo = new Vuelo(horaInicioDate, horaFinDate, numPasajeros, numAsientos, aeropuertoDestinoSeleccionado, aeropuertoPartidaSeleccionado);
        vuelos.add(nuevoVuelo);
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra("LISTA_VUELOS", vuelos);
        resultIntent.putParcelableArrayListExtra("LISTA_CONEXIONES", conexiones);
        resultIntent.putParcelableArrayListExtra("LISTA_AEROPUERTOS", aeropuertos);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}