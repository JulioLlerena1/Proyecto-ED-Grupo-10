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


        if(aeropuertos == null){

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
        requestPermissionsIfNecessary(new String[] {
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

        Random random = new Random();

        // Conectar aeropuertos en el grafo y agregar líneas en el mapa
        for (Vuelo v : vuelos) {
            Aeropuerto origen = v.getPartida();
            Aeropuerto destino = v.getDestino();
            double distanciaKm = origen.toGeoPoint().distanceToAsDouble(destino.toGeoPoint()) / 1000.0;

            // Conectar en el grafo
            graph.connect(origen, destino, distanciaKm);

            // Generar color aleatorio
            int colorAleatorio = 0xFF000000 // alfa 255
                    | (random.nextInt(256) << 16)
                    | (random.nextInt(256) << 8)
                    | random.nextInt(256);

            // Dibujar Polyline en el mapa
            Polyline linea = new Polyline();
            linea.setPoints(Arrays.asList(origen.toGeoPoint(), destino.toGeoPoint()));
            linea.setWidth(5f);
            linea.setColor(colorAleatorio);
            mapView.getOverlays().add(linea);
        }

        // Agregar marcadores dinámicos
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

        // Configuración inicial del mapa
        mapView.getController().setZoom(4.0);
        if (!aeropuertos.isEmpty()) {
            GeoPoint centro = aeropuertos.get(0).toGeoPoint(); // centro aproximado
            mapView.getController().setCenter(centro);
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