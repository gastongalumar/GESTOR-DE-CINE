package Clases;

import java.util.ArrayList;
import java.util.List;

public class Sala {
    private static int contID = 0;
    private int id;
    private String nombreSala;
    private int capacidad;
    private List<Asiento> listaAsientos;

    public Sala(String nombreSala, int capacidad) {
        this.id = ++contID;
        this.nombreSala = nombreSala;
        this.capacidad = capacidad;
        listaAsientos = new ArrayList<>();
    }

    public String getNombreSala() {
        return nombreSala;
    }

    public void setNombreSala(String nombreSala) {
        this.nombreSala = nombreSala;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public List<Asiento> getListaAsientos() {
        return listaAsientos;
    }

    public void setListaAsientos(List<Asiento> listaAsientos) {
        this.listaAsientos = listaAsientos;
    }
}
