package Clases;

import Enumeradores.EstadoAsiento;

public class Asiento {
    private int nroAsiento;
    private EstadoAsiento estadoAsiento;

    public Asiento(int nroAsiento, EstadoAsiento estadoAsiento) {
        this.nroAsiento = nroAsiento;
        this.estadoAsiento = estadoAsiento;
    }

    public int getNroAsiento() {
        return nroAsiento;
    }

    public void setNroAsiento(int nroAsiento) {
        this.nroAsiento = nroAsiento;
    }

    public EstadoAsiento getEstadoAsiento() {
        return estadoAsiento;
    }

    public void setEstadoAsiento(EstadoAsiento estadoAsiento) {
        this.estadoAsiento = estadoAsiento;
    }

    @Override
    public String toString() {
        return "Asiento{" +
                "nroAsiento=" + nroAsiento +
                ", estadoAsiento=" + estadoAsiento +
                '}';
    }
}


