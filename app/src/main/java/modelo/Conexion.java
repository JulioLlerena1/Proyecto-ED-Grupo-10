package modelo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Conexion implements Parcelable {
    private Aeropuerto origen;
    private Aeropuerto destino;
    private double distanciaKm;
    private int color;

    public int getColor() {
        return color;
    }
    protected Conexion(Parcel in) {
        origen = in.readParcelable(Aeropuerto.class.getClassLoader());
        destino = in.readParcelable(Aeropuerto.class.getClassLoader());
        distanciaKm = in.readDouble();
        color = in.readInt();
    }

    public Conexion(Aeropuerto origen, Aeropuerto destino, int color) {
        this.origen = origen;
        this.destino = destino;
        this.distanciaKm = origen.toGeoPoint().distanceToAsDouble(destino.toGeoPoint()) / 1000.0;
        this.color = color;
    }
    public static ArrayList<Conexion> cargarConexiones(Context context, List<Aeropuerto> aeropuertos) throws IOException {
        ArrayList<Conexion> listaConexiones = new ArrayList<>();
        Random random = new Random();

        AssetManager am = context.getAssets();
        InputStream is = am.open("conexiones.txt"); // archivo con origen;destino
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");

            if (parts.length == 2) {
                String codOrigen = parts[0].trim();
                String codDestino = parts[1].trim();

                Aeropuerto origen = null;
                Aeropuerto destino = null;

                // Buscar aeropuertos en la lista
                for (Aeropuerto a : aeropuertos) {
                    if (a.getCodigo().equalsIgnoreCase(codOrigen)) origen = a;
                    if (a.getCodigo().equalsIgnoreCase(codDestino)) destino = a;
                }

                if (origen != null && destino != null) {
                    int colorAleatorio = 0xFF000000
                            | (random.nextInt(256) << 16)
                            | (random.nextInt(256) << 8)
                            | random.nextInt(256);

                    listaConexiones.add(new Conexion(origen, destino, colorAleatorio));
                }
            }
        }
        reader.close();
        return listaConexiones;
    }


    public Aeropuerto getOrigen() {
        return origen;
    }

    public Aeropuerto getDestino() {
        return destino;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(origen, flags);
        dest.writeParcelable(destino, flags);
        dest.writeDouble(distanciaKm);
        dest.writeInt(color);
    }

    public static final Creator<Conexion> CREATOR = new Creator<Conexion>() {
        @Override
        public Conexion createFromParcel(Parcel in) {
            return new Conexion(in);
        }

        @Override
        public Conexion[] newArray(int size) {
            return new Conexion[size];
        }
    };
}
