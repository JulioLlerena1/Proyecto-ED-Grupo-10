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

public class PrimerPantalla extends AppCompatActivity {

    private Button btn_Ingresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_primer_pantalla);

        btn_Ingresar= findViewById(R.id.btn_ingresar);

        btn_Ingresar.setOnClickListener(this::ingresar);
    }

    public void ingresar(View view){

        //Mostrar la activity de mapa
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
