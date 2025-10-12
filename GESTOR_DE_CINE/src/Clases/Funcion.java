package Clases;

import java.time.LocalDate;
import java.util.Date;

public class Funcion {
    private Sala sala;
    private Pelicula pelicula;
    private LocalDate fechaSalida;


    public Funcion(Sala sala, Pelicula pelicula, LocalDate fechaSalida) {
        this.sala = sala;
        this.pelicula = pelicula;
        this.fechaSalida = fechaSalida;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }
}
