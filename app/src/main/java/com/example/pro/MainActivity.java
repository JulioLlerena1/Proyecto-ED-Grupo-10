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
        GeoPoint usaPoint = new GeoPoint(38.9072, -77.0369); // Washington D.C., USA
        GeoPoint ecuadorPoint = new GeoPoint(-0.1807, -78.4678); // Quito, Ecuador

        // Configuración inicial del mapa (centrado en el medio)
        mapView.getController().setZoom(4.0);
        mapView.getController().setCenter(new GeoPoint(15.0, -77.0)); // Centro aproximado entre ambos

        // Marcador en USA
        Marker usaMarker = new Marker(mapView);
        usaMarker.setPosition(usaPoint);
        usaMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        usaMarker.setTitle("Estados Unidos");
        mapView.getOverlays().add(usaMarker);

        // Marcador en Ecuador
        Marker ecuadorMarker = new Marker(mapView);
        ecuadorMarker.setPosition(ecuadorPoint);
        ecuadorMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        ecuadorMarker.setTitle("Ecuador");
        mapView.getOverlays().add(ecuadorMarker);

        // Línea entre USA y Ecuador
        Polyline line = new Polyline();
        List<GeoPoint> points = new ArrayList<>();
        points.add(usaPoint);
        points.add(ecuadorPoint);
        line.setPoints(points);
        line.setWidth(6f);
        line.setColor(0xFF0000FF); // Azul
        mapView.getOverlays().add(line);
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