package Clases;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class Funcion {
    private Sala sala;
    private Pelicula pelicula;
    private LocalDateTime horarioFuncion;

    public Funcion(Sala sala, Pelicula pelicula, LocalDateTime horarioFuncion) {
        this.sala = sala;
        this.pelicula = pelicula;
        this.horarioFuncion = horarioFuncion;
    }

    public LocalDateTime getHorarioFuncion() {
        return horarioFuncion;
    }

    public void setHorarioFuncion(LocalDateTime horarioFuncion) {
        this.horarioFuncion = horarioFuncion;
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
