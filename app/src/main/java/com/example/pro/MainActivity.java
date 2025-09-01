package com.example.pro;


import static modelo.Aeropuerto.cargarAeropuertos;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import org.osmdroid.views.overlay.Overlay;
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
    private ArrayList<Conexion> conexiones;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;


    // Registro del launcher para recibir resultados
    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        aeropuertos = data.getParcelableArrayListExtra("LISTA_AEROPUERTOS");
                        conexiones = data.getParcelableArrayListExtra("LISTA_CONEXIONES");
                        vuelos = data.getParcelableArrayListExtra("LISTA_VUELOS");
                        mostrarAeropuertosEnMapa(aeropuertos, vuelos, conexiones);
                        mostrarConexionesEnMapayCreacionGrafo(conexiones,aeropuertos);
                    }
                }
            }
    );


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
        aeropuertos = intent.getParcelableArrayListExtra("LISTA_AEROPUERTOS");
        vuelos = intent.getParcelableArrayListExtra("LISTA_VUELOS");


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

        try {
            conexiones = Conexion.cargarConexiones(this, aeropuertos);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
                    intent.putParcelableArrayListExtra("LISTA_CONEXIONES",conexiones);
                    launcher.launch(intent);

                } else if (id == R.id.nav_item_two) {

                    Intent intent = new Intent(MainActivity.this, ConfiguracionAeropuertos.class);

                    intent.putParcelableArrayListExtra("LISTA_AEROPUERTOS",aeropuertos);
                    intent.putParcelableArrayListExtra("LISTA_CONEXIONES",conexiones);
                    intent.putParcelableArrayListExtra("LISTA_VUELOS",vuelos);
                    launcher.launch(intent);

                } else if (id == R.id.nav_send) {

                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    intent.putParcelableArrayListExtra("LISTA_AEROPUERTOS",aeropuertos);
                    intent.putParcelableArrayListExtra("LISTA_VUELOS",vuelos);
                    intent.putParcelableArrayListExtra("LISTA_CONEXIONES",conexiones);
                    launcher.launch(intent);

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


        // Configuración inicial del mapa (centrado en el medio)
        mapView.getController().setZoom(4.0);
        mapView.getController().setCenter(new GeoPoint(15.0, -77.0)); // Centro aproximado entre ambos
        mapView.invalidate();


        mostrarConexionesEnMapayCreacionGrafo(conexiones,aeropuertos);
        mostrarAeropuertosEnMapa(aeropuertos,vuelos, conexiones);

    }

    private void mostrarConexionesEnMapayCreacionGrafo(ArrayList<Conexion> conexiones, ArrayList<Aeropuerto> aeropuertos) {
        graph = new DynamicGraph<>(false);

        List<Overlay> overlays = new ArrayList<>(mapView.getOverlays());
        for (Overlay o : overlays) {
            if (o instanceof Polyline) {
                mapView.getOverlays().remove(o);
            }
        }

        for (Aeropuerto a : aeropuertos) graph.addVertex(a);
        for (Conexion c : conexiones) {
            Aeropuerto origen = c.getOrigen();
            Aeropuerto destino = c.getDestino();
            double distanciaKm = c.getDistanciaKm();

            graph.connect(origen, destino, distanciaKm);

            Polyline linea = new Polyline();
            linea.setPoints(Arrays.asList(origen.toGeoPoint(), destino.toGeoPoint()));
            linea.setWidth(5f);
            linea.setColor(c.getColor());
            mapView.getOverlays().add(linea);
        }
        mapView.invalidate();

    }

    public static DynamicGraph<Aeropuerto, Double> crearGrafo(
            ArrayList<Aeropuerto> aeropuertos,
            ArrayList<Conexion> conexiones,
            boolean dirigido) {

        DynamicGraph<Aeropuerto, Double> grafo = new DynamicGraph<>(dirigido);

        // Agregar vertices (aeropuertos)
        for (Aeropuerto a : aeropuertos) {
            grafo.addVertex(a);
        }

        // Agregar aristas (conexiones)
        for (Conexion c : conexiones) {
            Aeropuerto origen = c.getOrigen();
            Aeropuerto destino = c.getDestino();
            double distancia = c.getDistanciaKm();

            grafo.connect(origen, destino, distancia);
        }

        return grafo;
    }

    private void mostrarAeropuertosEnMapa(ArrayList<Aeropuerto> aeropuertos, ArrayList<Vuelo> vuelos, ArrayList<Conexion> conexiones) {
        List<Overlay> overlays = mapView.getOverlays();
        overlays.removeIf(o -> o instanceof Marker);
        for (Aeropuerto a : aeropuertos) {
            Marker marker = new Marker(mapView);
            marker.setPosition(a.toGeoPoint());
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(a.getNombreCompleto());
            marker.setOnMarkerClickListener((m, map) -> {
                mostrarInformacionAeropuerto(a, aeropuertos, vuelos, conexiones);
                return true;
            });
            mapView.getOverlays().add(marker);
        }
        mapView.invalidate(); // refrescar el mapa
    }


    private void mostrarInformacionAeropuerto(Aeropuerto aeropuerto, ArrayList<Aeropuerto> aeropuertos, ArrayList<Vuelo> vuelos, ArrayList<Conexion> conexiones) {
        Intent intent = new Intent(this, AeropuertoInfo.class);
        intent.putParcelableArrayListExtra("LISTA_AEROPUERTOS",aeropuertos);
        intent.putParcelableArrayListExtra("LISTA_VUELOS",vuelos);
        intent.putExtra("AEROPUERTO_SELECCIONADO",(Serializable) aeropuerto);
        intent.putParcelableArrayListExtra("LISTA_CONEXIONES",conexiones);
        launcher.launch(intent);
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