package com.example.pro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Estadisticas extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estadisticas);


    }



    public void btnVolver(View view){
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}