package com.example.pro;


import static modelo.Aeropuerto.cargarAeropuertos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import modelo.Aeropuerto;
import modelo.DynamicGraph;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    public static DynamicGraph graph;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración básica de osmdroid
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().setOsmdroidBasePath(new File(getFilesDir(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(getCacheDir(), "tiles"));

        setContentView(R.layout.activity_main);

        // Solicitar permisos de ubicación
        requestPermissionsIfNecessary(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);


        List<Aeropuerto> aeropuertos = new ArrayList<>();

        try {
            aeropuertos = Aeropuerto.cargarAeropuertos(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Aeropuerto laxAeropuerto = aeropuertos.get(0);      // LAX
        Aeropuerto quitoAeropuerto = aeropuertos.get(1);    // UIO
        Aeropuerto frankfurtAeropuerto = aeropuertos.get(2); // FRA
        Aeropuerto jfkAeropuerto = aeropuertos.get(3);      // JFK
        Aeropuerto madAeropuerto = aeropuertos.get(4);      // MAD
        Aeropuerto daxingAeropuerto = aeropuertos.get(5);   // PKX

        GeoPoint lax = laxAeropuerto.toGeoPoint();
        GeoPoint quito = quitoAeropuerto.toGeoPoint();
        GeoPoint frankfurt = frankfurtAeropuerto.toGeoPoint();
        GeoPoint jfkPoint = jfkAeropuerto.toGeoPoint();
        GeoPoint madPoint = madAeropuerto.toGeoPoint();
        GeoPoint daxing = daxingAeropuerto.toGeoPoint();


        graph = new DynamicGraph(false);
        graph.addVertex(laxAeropuerto);
        graph.addVertex(quitoAeropuerto);
        graph.addVertex(frankfurtAeropuerto);
        graph.addVertex(jfkAeropuerto);
        graph.addVertex(madAeropuerto);
        graph.addVertex(daxingAeropuerto);

        //Asignacion de pesos
        double distLaxQuito = lax.distanceToAsDouble(quito) / 1000.0;       // km
        double distLaxFrankfurt = lax.distanceToAsDouble(frankfurt) / 1000.0; // km
        double distFrankfurtDaxing = frankfurt.distanceToAsDouble(daxing) / 1000.0; // km
        double distQuitoDaxing = quito.distanceToAsDouble(daxing) / 1000.0; // km
        double distQuitoJFK = quito.distanceToAsDouble(jfkPoint) / 1000.0;   // km
        double distLaxJFK = lax.distanceToAsDouble(jfkPoint) / 1000.0;       // km
        double distJFKFrankfurt = jfkPoint.distanceToAsDouble(frankfurt) / 1000.0; // km
        double distFrankfurtMad = frankfurt.distanceToAsDouble(madPoint) / 1000.0;  // km
        double distMadJFK = madPoint.distanceToAsDouble(jfkPoint) / 1000.0;         // km
        double distMadDaxing = madPoint.distanceToAsDouble(daxing) / 1000.0;       // km


        graph.connect(laxAeropuerto, quitoAeropuerto, distLaxQuito);
        graph.connect(laxAeropuerto, frankfurtAeropuerto, distLaxFrankfurt);
        graph.connect(frankfurtAeropuerto, daxingAeropuerto, distFrankfurtDaxing);
        graph.connect(quitoAeropuerto, daxingAeropuerto, distQuitoDaxing);
        graph.connect(quitoAeropuerto, jfkAeropuerto, distQuitoJFK);
        graph.connect(laxAeropuerto, jfkAeropuerto, distLaxJFK);
        graph.connect(jfkAeropuerto, frankfurtAeropuerto, distJFKFrankfurt);
        graph.connect(frankfurtAeropuerto, madAeropuerto, distFrankfurtMad);
        graph.connect(madAeropuerto, jfkAeropuerto, distMadJFK);
        graph.connect(madAeropuerto, daxingAeropuerto, distMadDaxing);


        // Configuración inicial del mapa (centrado en el medio)
        mapView.getController().setZoom(4.0);
        mapView.getController().setCenter(new GeoPoint(15.0, -77.0)); // Centro aproximado entre ambos

        for (Aeropuerto a : aeropuertos) {
            GeoPoint punto = a.toGeoPoint();
            Marker marker = new Marker(mapView);
            marker.setPosition(punto);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(a.getNombreCompleto());
            // Listener para click
            marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker, MapView mapView) {
                    mostrarInformacionAeropuerto(a);
                    return true;
                }
            });
            mapView.getOverlays().add(marker);
        }


        // Crear líneas (aristas del grafo) entre aeropuertos
        Polyline line1 = new Polyline();
        line1.setPoints(Arrays.asList(lax, quito));
        line1.setWidth(5f);
        line1.setColor(0xFF0000FF); // Azul
        mapView.getOverlays().add(line1);

        Polyline line2 = new Polyline();
        line2.setPoints(Arrays.asList(lax, frankfurt));
        line2.setWidth(5f);
        line2.setColor(0xFF00FF00); // Verde
        mapView.getOverlays().add(line2);

        Polyline line3 = new Polyline();
        line3.setPoints(Arrays.asList(frankfurt, daxing));
        line3.setWidth(5f);
        line3.setColor(0xFFFF0000); // Rojo
        mapView.getOverlays().add(line3);

        Polyline line4 = new Polyline();
        line4.setPoints(Arrays.asList(quito, daxing));
        line4.setWidth(5f);
        line4.setColor(0xFFFFFF00); // Amarillo
        mapView.getOverlays().add(line4);

        Polyline line5 = new Polyline();
        line5.setPoints(Arrays.asList(quito, jfkPoint));
        line5.setWidth(5f);
        line5.setColor(0xFF00FFFF); // Cyan
        mapView.getOverlays().add(line5);

        Polyline line6 = new Polyline();
        line6.setPoints(Arrays.asList(lax, jfkPoint));
        line6.setWidth(5f);
        line6.setColor(0xFFFF00FF); // Magenta
        mapView.getOverlays().add(line6);

        Polyline line7 = new Polyline();
        line7.setPoints(Arrays.asList(jfkPoint, frankfurt));
        line7.setWidth(5f);
        line7.setColor(0xFF888888); // Gris
        mapView.getOverlays().add(line7);

        Polyline line8 = new Polyline();
        line8.setPoints(Arrays.asList(frankfurt, madPoint));
        line8.setWidth(5f);
        line8.setColor(0xFFFF8800); // Naranja
        mapView.getOverlays().add(line8);

        Polyline line9 = new Polyline();
        line9.setPoints(Arrays.asList(madPoint, jfkPoint));
        line9.setWidth(5f);
        line9.setColor(0xFF008800); // Verde oscuro
        mapView.getOverlays().add(line9);

        Polyline line10 = new Polyline();
        line10.setPoints(Arrays.asList(madPoint, daxing));
        line10.setWidth(5f);
        line10.setColor(0xFF8800FF); // Violeta
        mapView.getOverlays().add(line10);


        mapView.invalidate();


    }

    private void mostrarInformacionAeropuerto(Aeropuerto aeropuerto) {
        Intent intent = new Intent(this, AeropuertoInfo.class);
        intent.putExtra("aeropuerto", aeropuerto); //
        startActivity(intent);
    }


    private void requestPermissionsIfNecessary(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}