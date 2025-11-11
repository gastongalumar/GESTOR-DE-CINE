package Clases.GestionSelectorAsientos;

import Enumeradores.EstadoAsiento;

public class Asiento {
    private int nroAsiento;
    private EstadoAsiento estadoAsiento;

    //CONSTRUCTOR

    public Asiento(int nroAsiento, EstadoAsiento estadoAsiento) {
        this.nroAsiento = nroAsiento;
        this.estadoAsiento = estadoAsiento;
    }

    //GETTER Y SETTER

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


    //TO STRING
    @Override
    public String toString() {
        return "Asiento{" +
                "nroAsiento=" + nroAsiento +
                ", estadoAsiento=" + estadoAsiento +
                '}';
    }
}


