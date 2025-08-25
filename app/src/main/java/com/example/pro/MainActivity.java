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
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.os.Parcelable;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import modelo.Aeropuerto;
import modelo.Conexion;
import modelo.DynamicGraph;
import modelo.Vuelo;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar topAppBar;
    private ActionBarDrawerToggle toggle;
    public static DynamicGraph<Aeropuerto, Double> graph;
    private ArrayList<Aeropuerto> aeropuertos;
    private ArrayList<Vuelo> vuelos;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuración básica de osmdroid
        Configuration.getInstance().setUserAgentValue(getPackageName());
        Configuration.getInstance().setOsmdroidBasePath(new File(getFilesDir(), "osmdroid"));
        Configuration.getInstance().setOsmdroidTileCache(new File(getCacheDir(), "tiles"));

        setContentView(R.layout.activity_main);

        aeropuertos = new ArrayList<>();


        Intent intent = getIntent();
        aeropuertos = (ArrayList<Aeropuerto>) intent.getSerializableExtra("LISTA_AEROPUERTOS");
        vuelos = (ArrayList<Vuelo>) intent.getSerializableExtra("LISTA_VUELOS");


        if (aeropuertos == null) {

            try {

                aeropuertos = Aeropuerto.cargarAeropuertos(this);

            } catch (IOException e) {

                throw new RuntimeException(e);

            }

        }

        if(vuelos == null){

            try {

                vuelos = Vuelo.cargarVuelos(this,aeropuertos);

            } catch (IOException e) {

                throw new RuntimeException(e);

            }

        }


        // Creacion del menu deslizable
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        topAppBar = findViewById(R.id.topAppBar);

        setSupportActionBar(topAppBar);
        toggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                topAppBar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Navegacion por el menu deslizable
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_item_one) {

                    Intent intent = new Intent(MainActivity.this, ConfiguracionVuelos.class);

                    intent.putParcelableArrayListExtra("LISTA_AEROPUERTOS",aeropuertos);
                    intent.putParcelableArrayListExtra("LISTA_VUELOS",vuelos);
                    startActivity(intent);

                } else if (id == R.id.nav_item_two) {

                    Intent intent = new Intent(MainActivity.this, ConfiguracionAeropuertos.class);

                    intent.putParcelableArrayListExtra("LISTA_AEROPUERTOS",aeropuertos);
                    intent.putParcelableArrayListExtra("LISTA_VUELOS",vuelos);
                    startActivity(intent);

                } else if (id == R.id.nav_send) {

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putParcelableArrayListExtra("LISTA_AEROPUERTOS",aeropuertos);
                    intent.putParcelableArrayListExtra("LISTA_VUELOS",vuelos);
                    startActivity(intent);

                }
                drawerLayout.closeDrawers(); // Cierra el drawer después de la selección
                return true;
            }
        });

        // Solicitar permisos de ubicación
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Crear grafo dinámico
        graph = new DynamicGraph<>(false);
        for (Aeropuerto a : aeropuertos) graph.addVertex(a);

        Aeropuerto laxAeropuerto = aeropuertos.get(0);      // LAX
        Aeropuerto quitoAeropuerto = aeropuertos.get(1);    // UIO
        Aeropuerto frankfurtAeropuerto = aeropuertos.get(2); // FRA
        Aeropuerto jfkAeropuerto = aeropuertos.get(3);      // JFK
        Aeropuerto madAeropuerto = aeropuertos.get(4);      // MAD
        Aeropuerto daxingAeropuerto = aeropuertos.get(5);   // PKX

        Aeropuerto cdgAeropuerto = aeropuertos.get(6);   // CDG
        Aeropuerto gruAeropuerto = aeropuertos.get(7);   // GRU
        Aeropuerto sydAeropuerto = aeropuertos.get(8);   // SYD
        Aeropuerto dxbAeropuerto = aeropuertos.get(9);   // DXB
        Random random = new Random();

        // Conectar aeropuertos en el grafo y agregar líneas en el mapa
            GeoPoint lax = laxAeropuerto.toGeoPoint();
            GeoPoint quito = quitoAeropuerto.toGeoPoint();
            GeoPoint frankfurt = frankfurtAeropuerto.toGeoPoint();
            GeoPoint jfkPoint = jfkAeropuerto.toGeoPoint();
            GeoPoint madPoint = madAeropuerto.toGeoPoint();
            GeoPoint daxing = daxingAeropuerto.toGeoPoint();
            GeoPoint cdgPoint = cdgAeropuerto.toGeoPoint();
            GeoPoint gruPoint = gruAeropuerto.toGeoPoint();
            GeoPoint sydPoint = sydAeropuerto.toGeoPoint();
            GeoPoint dxbPoint = dxbAeropuerto.toGeoPoint();


            graph = new DynamicGraph<Aeropuerto, Double>(false);
            graph.addVertex(laxAeropuerto);
            graph.addVertex(quitoAeropuerto);
            graph.addVertex(frankfurtAeropuerto);
            graph.addVertex(jfkAeropuerto);
            graph.addVertex(madAeropuerto);
            graph.addVertex(daxingAeropuerto);
            graph.addVertex(cdgAeropuerto);
            graph.addVertex(gruAeropuerto);
            graph.addVertex(sydAeropuerto);
            graph.addVertex(dxbAeropuerto);

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
            double distCdgMad = cdgPoint.distanceToAsDouble(madPoint) / 1000.0;
            double distCdgFra = cdgPoint.distanceToAsDouble(frankfurt) / 1000.0;
            double distGruQuito = gruPoint.distanceToAsDouble(quito) / 1000.0;
            double distGruLax = gruPoint.distanceToAsDouble(lax) / 1000.0;
            double distSydDaxing = sydPoint.distanceToAsDouble(daxing) / 1000.0;
            double distSydLax = sydPoint.distanceToAsDouble(lax) / 1000.0;
            double distDxbFra = dxbPoint.distanceToAsDouble(frankfurt) / 1000.0;
            double distDxbPkx = dxbPoint.distanceToAsDouble(daxing) / 1000.0;


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
            graph.connect(cdgAeropuerto, madAeropuerto, distCdgMad);
            graph.connect(cdgAeropuerto, frankfurtAeropuerto, distCdgFra);
            graph.connect(gruAeropuerto, quitoAeropuerto, distGruQuito);
            graph.connect(gruAeropuerto, laxAeropuerto, distGruLax);
            graph.connect(sydAeropuerto, daxingAeropuerto, distSydDaxing);
            graph.connect(sydAeropuerto, laxAeropuerto, distSydLax);
            graph.connect(dxbAeropuerto, frankfurtAeropuerto, distDxbFra);
            graph.connect(dxbAeropuerto, daxingAeropuerto, distDxbPkx);

            // Configuración inicial del mapa (centrado en el medio)
            mapView.getController().setZoom(4.0);
            mapView.getController().setCenter(new GeoPoint(15.0, -77.0)); // Centro aproximado entre ambos

            for (Aeropuerto a : aeropuertos) {
                Marker marker = new Marker(mapView);
                marker.setPosition(a.toGeoPoint());
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setTitle(a.getNombreCompleto());
                marker.setOnMarkerClickListener((m, map) -> {
                    mostrarInformacionAeropuerto(a);
                    return true;
                });
                mapView.getOverlays().add(marker);
            }


            List<Conexion> conexiones = Arrays.asList(
                    new Conexion(lax, quito, 0xFF0000FF),   // Azul
                    new Conexion(lax, frankfurt, 0xFF00FF00), // Verde
                    new Conexion(frankfurt, daxing, 0xFFFF0000), // Rojo
                    new Conexion(quito, daxing, 0xFFFFFF00), // Amarillo
                    new Conexion(quito, jfkPoint, 0xFF00FFFF), // Cyan
                    new Conexion(lax, jfkPoint, 0xFFFF00FF), // Magenta
                    new Conexion(jfkPoint, frankfurt, 0xFF888888), // Gris
                    new Conexion(frankfurt, madPoint, 0xFFFF8800), // Naranja
                    new Conexion(madPoint, jfkPoint, 0xFF008800), // Verde oscuro
                    new Conexion(madPoint, daxing, 0xFF8800FF), // Violeta
                    new Conexion(cdgPoint, madPoint, 0xFFAA0000), // Rojo oscuro
                    new Conexion(cdgPoint, frankfurt, 0xFF00AAFF), // Celeste
                    new Conexion(gruPoint, quito, 0xFF008800), // Verde oscuro
                    new Conexion(gruPoint, lax, 0xFFAA5500), // Marrón
                    new Conexion(sydPoint, daxing, 0xFF8800FF), // Violeta
                    new Conexion(sydPoint, lax, 0xFFFFFF00), // Amarillo
                    new Conexion(dxbPoint, frankfurt, 0xFF00FFFF), // Cyan
                    new Conexion(dxbPoint, daxing, 0xFFFF8800) // Naranja
            );

            for (Conexion c : conexiones) {
                Polyline line = new Polyline();
                line.setPoints(Arrays.asList(c.origen, c.destino));
                line.setWidth(5f);
                line.setColor(c.color);
                mapView.getOverlays().add(line);
            }
            mapView.invalidate();

    }


    private void mostrarInformacionAeropuerto(Aeropuerto aeropuerto) {
        Intent intent = new Intent(this, AeropuertoInfo.class);
        intent.putParcelableArrayListExtra("LISTA_AEROPUERTOS",aeropuertos);
        intent.putParcelableArrayListExtra("LISTA_VUELOS",vuelos);
        intent.putExtra("AEROPUERTO_SELECCIONADO",(Serializable) aeropuerto);
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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
        } else {
            super.onBackPressed();
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