package Clases;

import Enumeradores.EstadoAsiento;

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

    public Sala(String nombreSala, int capacidad, List<Asiento> listaAsientos) {
        this.id = ++contID;
        this.nombreSala = nombreSala;
        this.capacidad = capacidad;
        this.listaAsientos = listaAsientos;
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

    public int getId() {
        return id;
    }

    public static Sala crearSala(String nombreSala, int capacidad){
        List<Asiento> listaAsientos = new ArrayList<>();
        for(int i = 1; i <= capacidad; i++){
            Asiento a = new Asiento(i, EstadoAsiento.DISPONIBLE);
            listaAsientos.add(a);
        }
        Sala s = new Sala(nombreSala,capacidad,listaAsientos);
        return s;
    }

    @Override
    public String toString() {
        return "Sala{" +
                "id=" + id +
                ", nombreSala='" + nombreSala + '\'' +
                ", capacidad=" + capacidad +
                ", listaAsientos=" + listaAsientos +
                '}';
    }
}
