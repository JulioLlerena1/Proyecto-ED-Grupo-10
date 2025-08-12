package modelo;

import java.time.LocalTime;

public class Vuelo {

    private LocalTime horaI;
    private LocalTime horaF;
    private int numPasajeros;
    private int numAsientos;
    private Aeropuerto destino;
    private Aeropuerto partida;

    public Vuelo(LocalTime horaI, LocalTime horaF, int numPasajeros, int numAsientos, Aeropuerto destino, Aeropuerto partida) {
        this.horaI = horaI;
        this.horaF = horaF;
        this.numPasajeros = numPasajeros;
        this.numAsientos = numAsientos;
        this.destino = destino;
        this.partida = partida;
    }

    public LocalTime getHoraI() {
        return horaI;
    }

    public LocalTime getHoraF() {
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

    public void setHoraI(LocalTime horaI) {
        this.horaI = horaI;
    }

    public void setHoraF(LocalTime horaF) {
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
}
