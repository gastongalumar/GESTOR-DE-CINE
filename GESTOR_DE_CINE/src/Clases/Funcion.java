package Clases;

import java.time.LocalDateTime;

public class Funcion {
    private Sala sala;
    private Pelicula pelicula;
    private LocalDateTime horarioFuncion;

    public Funcion(Sala sala, Pelicula pelicula, LocalDateTime horarioFuncion) {
        this.sala = sala;
        this.pelicula = pelicula;
        this.horarioFuncion = horarioFuncion;
        // Registrar automáticamente la función en el gestor
        GestorFunciones.agregarFuncion(this);
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
