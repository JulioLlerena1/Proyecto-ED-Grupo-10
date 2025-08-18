package com.example.pro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
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


        // Coordenadas
        GeoPoint daxing = new GeoPoint(39.509, 116.410); // PKX, Beijing
        GeoPoint lax = new GeoPoint(33.9416, -118.4085); // LAX, Los Ángeles
        GeoPoint quito = new GeoPoint(-0.1807, -78.4678); // UIO, Quito
        GeoPoint frankfurt = new GeoPoint(50.0379, 8.5622); // FRA, Alemania

        // Configuración inicial del mapa (centrado en el medio)
        mapView.getController().setZoom(4.0);
        mapView.getController().setCenter(new GeoPoint(15.0, -77.0)); // Centro aproximado entre ambos

        // Crear marcadores
        Marker pkxMarker = new Marker(mapView);
        pkxMarker.setPosition(daxing);
        pkxMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        pkxMarker.setTitle("Beijing Daxing (PKX)");
        mapView.getOverlays().add(pkxMarker);

        Marker laxMarker = new Marker(mapView);
        laxMarker.setPosition(lax);
        laxMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        laxMarker.setTitle("Los Ángeles (LAX)");
        mapView.getOverlays().add(laxMarker);

        Marker quitoMarker = new Marker(mapView);
        quitoMarker.setPosition(quito);
        quitoMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        quitoMarker.setTitle("Quito (UIO)");
        mapView.getOverlays().add(quitoMarker);

        Marker fraMarker = new Marker(mapView);
        fraMarker.setPosition(frankfurt);
        fraMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        fraMarker.setTitle("Frankfurt (FRA)");
        mapView.getOverlays().add(fraMarker);

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

        mapView.invalidate();


        // Distancias en metros usando OSMDroid
        double distLaxQuito = lax.distanceToAsDouble(quito) / 1000.0;       // km
        double distLaxFrankfurt = lax.distanceToAsDouble(frankfurt) / 1000.0; // km
        double distFrankfurtDaxing = frankfurt.distanceToAsDouble(daxing) / 1000.0; // km
        double distQuitoDaxing = quito.distanceToAsDouble(daxing) / 1000.0; // km

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