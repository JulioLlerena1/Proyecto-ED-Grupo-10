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
import modelo.Vuelo;

public class EditarVuelo extends AppCompatActivity {

    private TextInputLayout layoutHoraInicio, layoutHoraFin, layoutNumPasajeros, layoutNumAsientos;
    private TextInputEditText editTextHoraInicio, editTextHoraFin, editTextNumPasajeros, editTextNumAsientos;
    private Spinner spinnerOrigen, spinnerDestino;
    private Button buttonGuardarVuelo;

    private ArrayList<Aeropuerto> aeropuertos;
    private Vuelo vueloAEditar;
    private static final SimpleDateFormat sdfInputHora = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_vuelo);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_editar_vuelo);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            setResult(Activity.RESULT_CANCELED);
            finish();
        });


        editTextHoraInicio = findViewById(R.id.edit_text_hora_inicio_editar);
        editTextHoraFin = findViewById(R.id.edit_text_hora_fin_editar);
        layoutNumPasajeros = findViewById(R.id.layout_num_pasajeros_editar);
        editTextNumPasajeros = findViewById(R.id.edit_text_num_pasajeros_editar);
        layoutNumAsientos = findViewById(R.id.layout_num_asientos_editar);
        editTextNumAsientos = findViewById(R.id.edit_text_num_asientos_editar);

        spinnerOrigen = findViewById(R.id.spinner_aeropuerto_origen_editar);
        spinnerDestino = findViewById(R.id.spinner_aeropuerto_destino_editar);
        buttonGuardarVuelo = findViewById(R.id.button_actualizar_vuelo);

        aeropuertos = new ArrayList<>();
        Intent intent = getIntent();
        aeropuertos = intent.getParcelableArrayListExtra("LISTA_AEROPUERTOS");
        vueloAEditar = intent.getParcelableExtra("VUELO_EDITADO");

        if (aeropuertos.isEmpty()) {
            Toast.makeText(this, "No hay aeropuertos disponibles para seleccionar.", Toast.LENGTH_LONG).show();
        }

        configurarSpinnersAeropuerto();
        precargarDatosDelVuelo();

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

    private void precargarDatosDelVuelo() {
        if (vueloAEditar == null) return;

        if (vueloAEditar.getHoraI() != null) {
            editTextHoraInicio.setText(sdfInputHora.format(vueloAEditar.getHoraI()));
        }
        if (vueloAEditar.getHoraF() != null) {
            editTextHoraFin.setText(sdfInputHora.format(vueloAEditar.getHoraF()));
        }
        editTextNumPasajeros.setText(String.valueOf(vueloAEditar.getNumPasajeros()));
        editTextNumAsientos.setText(String.valueOf(vueloAEditar.getNumPasajeros()));

        if (vueloAEditar.getPartida() != null) {
            int posicionOrigen = encontrarPosicionAeropuertoEnLista(vueloAEditar.getPartida());
            spinnerOrigen.setSelection(posicionOrigen);

        }
        if (vueloAEditar.getDestino() != null) {
            int posicionDestino = encontrarPosicionAeropuertoEnLista(vueloAEditar.getDestino());
            spinnerDestino.setSelection(posicionDestino);

        }
    }

    private int encontrarPosicionAeropuertoEnLista(Aeropuerto aeropuertoBuscado) {
        if (aeropuertoBuscado == null || aeropuertos == null) return -1;
        for (int i = 0; i < aeropuertos.size(); i++) {
            if (aeropuertos.get(i).getCodigo().equals(aeropuertoBuscado.getCodigo())) {
                return i;
            }
        }
        return -1;
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
                android.R.layout.simple_spinner_item, // Layout estándar para el ítem seleccionado
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
            Toast.makeText(this, "No hay aeropuertos disponibles para editar el vuelo.", Toast.LENGTH_LONG).show();
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

        if (horaInicioDate == null || horaFinDate == null) { // Debería ser redundante por el try-catch
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
        ArrayList vuelosEdit = new ArrayList();
        vuelosEdit.add(nuevoVuelo);
        vuelosEdit.add(vueloAEditar);
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra("VUELO_EDITADO", vuelosEdit);
        setResult(RESULT_OK, resultIntent);
        finish();


    }
}