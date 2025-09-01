package modelo;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Vuelo implements Parcelable {

    private Date horaI;
    private Date horaF;
    private int numPasajeros;
    private int numAsientos;
    private Aeropuerto destino;
    private Aeropuerto partida;

    public Vuelo(Date horaI, Date horaF, int numPasajeros, int numAsientos, Aeropuerto destino, Aeropuerto partida) {
        this.horaI = horaI;
        this.horaF = horaF;
        this.numPasajeros = numPasajeros;
        this.numAsientos = numAsientos;
        this.destino = destino;
        this.partida = partida;
    }

    public Date getHoraI() {
        return horaI;
    }

    public Date getHoraF() {
        return horaF;
    }

    public Aeropuerto getDestino() {
        return destino;
    }

    public int getNumPasajeros() {
        return numPasajeros;
    }

    public Aeropuerto getPartida() {
        return partida;
    }

    public void setHoraI(Date horaI) {
        this.horaI = horaI;
    }

    public void setHoraF(Date horaF) {
        this.horaF = horaF;
    }

    public void setNumPasajeros(int numPasajeros) {
        this.numPasajeros = numPasajeros;
    }

    public void setDestino(Aeropuerto destino) {
        this.destino = destino;
    }

    public void setPartida(Aeropuerto partida) {
        this.partida = partida;
    }

    @Override
    public String toString() {
        return "Vuelo: " +
                "horaI=" + horaI +
                ", horaF=" + horaF +
                ", numPasajeros=" + numPasajeros +
                ", destino=" + destino +
                ", partida=" + partida ;
    }


    public static ArrayList<Vuelo> cargarVuelos(Context context, List<Aeropuerto> aeropuertos) throws IOException {
        ArrayList<Vuelo> listaVuelos = new ArrayList<>();

        AssetManager am = context.getAssets();
        InputStream is = am.open("vuelos.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm"); // formato de hora

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");

            if (parts.length == 6) {
                String codPartida = parts[0].trim();
                String codDestino = parts[1].trim();

                Date horaI = null;
                Date horaF = null;

                try {
                    horaI = sdf.parse(parts[2].trim());
                    horaF = sdf.parse(parts[3].trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                    continue; // saltar línea si hay error de parseo
                }

                int numPasajeros = Integer.parseInt(parts[4].trim());
                int numAsientos = Integer.parseInt(parts[5].trim());

                // Buscar aeropuertos en la lista
                Aeropuerto partida = null;
                Aeropuerto destino = null;

                for (Aeropuerto a : aeropuertos) {
                    if (a.getCodigo().equalsIgnoreCase(codPartida)) partida = a;
                    if (a.getCodigo().equalsIgnoreCase(codDestino)) destino = a;
                }

                if (partida != null && destino != null) {
                    listaVuelos.add(new Vuelo(horaI, horaF, numPasajeros, numAsientos, destino, partida));
                }
            }
        }

        reader.close();
        return listaVuelos;
    }

    protected Vuelo(Parcel in) {
        long tmpHoraI = in.readLong();
        horaI = tmpHoraI != -1 ? new Date(tmpHoraI) : null;
        long tmpHoraF = in.readLong();
        horaF = tmpHoraF != -1 ? new Date(tmpHoraF) : null;
        numPasajeros = in.readInt();
        numAsientos = in.readInt();
        destino = in.readParcelable(Aeropuerto.class.getClassLoader());
        partida = in.readParcelable(Aeropuerto.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(horaI != null ? horaI.getTime() : -1);
        dest.writeLong(horaF != null ? horaF.getTime() : -1);
        dest.writeInt(numPasajeros);
        dest.writeInt(numAsientos);
        dest.writeParcelable(destino, flags);
        dest.writeParcelable(partida, flags);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Vuelo> CREATOR = new Creator<Vuelo>() {
        @Override
        public Vuelo createFromParcel(Parcel in) {
            return new Vuelo(in);
        }

        @Override
        public Vuelo[] newArray(int size) {
            return new Vuelo[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vuelo vuelo = (Vuelo) obj;
        return partida.getCodigo().equals(vuelo.partida.getCodigo()) &&
                destino.getCodigo().equals(vuelo.destino.getCodigo()) &&
                horaI.equals(vuelo.horaI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partida.getCodigo(), destino.getCodigo(), horaI);
    }

}
