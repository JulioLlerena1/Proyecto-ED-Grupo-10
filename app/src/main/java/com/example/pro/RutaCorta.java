package com.example.pro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import modelo.Aeropuerto;
import modelo.Conexion;
import modelo.Vuelo;

public class RutaCorta extends AppCompatActivity {

    private MapView mapViewSecundario;
    private ArrayList<Aeropuerto> aeropuertos;
    private ArrayList<Vuelo> vuelos;
    private ArrayList<Conexion> conexiones;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración de OSMDroid
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().setOsmdroidBasePath(
                new File(getCacheDir().getAbsolutePath(), "osmdroid")
        );
        Configuration.getInstance().setOsmdroidTileCache(
                new File(getCacheDir().getAbsolutePath(), "tile")
        );

        setContentView(R.layout.ruta_corta);

        aeropuertos = getIntent().getParcelableArrayListExtra("LISTA_AEROPUERTOS");
        conexiones = getIntent().getParcelableArrayListExtra("LISTA_CONEXIONES");
        vuelos = getIntent().getParcelableArrayListExtra("LISTA_VUELOS");

        // Vincular el MapView
        mapViewSecundario = findViewById(R.id.mapViewRutaCorta);
        mapViewSecundario.setMultiTouchControls(true);

        ArrayList<String> codigosRuta = getIntent().getStringArrayListExtra("ruta");

        List<Aeropuerto> rutaAeropuertos = new ArrayList<>();


        for (String codigo : codigosRuta) {
            for (Aeropuerto a : aeropuertos) {
                if (a.getCodigo().equalsIgnoreCase(codigo)) {
                    rutaAeropuertos.add(a);
                    break;
                }
            }
        }

        List<GeoPoint> puntos = new ArrayList<>();
        for (Aeropuerto a : rutaAeropuertos) {
            puntos.add(a.toGeoPoint());
        }

        Polyline rutaPolyline = new Polyline();
        rutaPolyline.setPoints(puntos);
        rutaPolyline.setWidth(8f);
        rutaPolyline.setColor(0xFF0000FF);
        mapViewSecundario.getOverlays().add(rutaPolyline);

        for (Aeropuerto a : rutaAeropuertos) {
            Marker marker = new Marker(mapViewSecundario);
            marker.setPosition(a.toGeoPoint());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(a.getNombreCompleto());
            mapViewSecundario.getOverlays().add(marker);
        }

        if (!puntos.isEmpty()) {
            mapViewSecundario.getController().setZoom(4.5);
            mapViewSecundario.getController().setCenter(puntos.get(0));
        }

        mapViewSecundario.invalidate();

    }


    @Override
    protected void onResume() {
        super.onResume();
        mapViewSecundario.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapViewSecundario.onPause();
    }

    public void volver(View view){
        Intent resultIntent = new Intent();
        resultIntent.putParcelableArrayListExtra("LISTA_AEROPUERTOS", aeropuertos);
        resultIntent.putParcelableArrayListExtra("LISTA_VUELOS", vuelos);
        resultIntent.putParcelableArrayListExtra("LISTA_CONEXIONES", conexiones);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}
