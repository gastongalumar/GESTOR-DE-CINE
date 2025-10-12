package Clases;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Pelicula {
    private String nombrePelicula;
    private LocalDate fechaEstreno;
    private LocalDate fechaSalida;

    public Pelicula(String nombrePelicula, LocalDate fechaEstreno, LocalDate fechaSalida) {
        this.nombrePelicula = nombrePelicula;
        this.fechaEstreno = fechaEstreno;
        this.fechaSalida = fechaSalida;
    }

    public String getNombrePelicula() {
        return nombrePelicula;
    }

    public void setNombrePelicula(String nombrePelicula) {
        this.nombrePelicula = nombrePelicula;
    }

    public LocalDate getFechaEstreno() {
        return fechaEstreno;
    }

    public void setFechaEstreno(LocalDate fechaEstreno) {
        this.fechaEstreno = fechaEstreno;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDate fechaSalida) {
        this.fechaSalida = fechaSalida;
    }
}
