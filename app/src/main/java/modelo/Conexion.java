package modelo;

import org.osmdroid.util.GeoPoint;

public class Conexion {
    public GeoPoint origen;
    public GeoPoint destino;
    public int color;

    public Conexion(GeoPoint origen, GeoPoint destino, int color) {
        this.origen = origen;
        this.destino = destino;
        this.color = color;
    }
}