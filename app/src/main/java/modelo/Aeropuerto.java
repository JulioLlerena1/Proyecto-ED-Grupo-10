package modelo;

import android.content.Context;
import android.content.res.AssetManager;

import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Aeropuerto implements Serializable {
    private String codigo;
    private String nombre;
    private double latitud;
    private double longitud;

    // Constructor
    public Aeropuerto(String codigo, String nombre, double latitud, double longitud) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Getters
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }

    /**
     * Lee el archivo aeropuertos.txt desde assets y devuelve una lista de Aeropuerto
     */
    public static List<Aeropuerto> cargarAeropuertos(Context context) throws IOException {
        List<Aeropuerto> lista = new ArrayList<>();

        AssetManager am = context.getAssets();
        InputStream is = am.open("aeropuertos");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            if (parts.length == 4) {
                String codigo = parts[0].trim();
                String nombre = parts[1].trim();
                double lat = Double.parseDouble(parts[2].trim());
                double lon = Double.parseDouble(parts[3].trim());

                lista.add(new Aeropuerto(codigo, nombre, lat, lon));
            }
        }
        reader.close();
        return lista;
    }

    public GeoPoint toGeoPoint() {
        return new GeoPoint(latitud, longitud);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + latitud + ", " + longitud + ")";
    }

    public String getNombreCompleto() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aeropuerto)) return false;
        Aeropuerto a = (Aeropuerto) o;
        return codigo.equalsIgnoreCase(a.codigo);
    }

    @Override
    public int hashCode() {
        return codigo.toLowerCase().hashCode();
    }
}
