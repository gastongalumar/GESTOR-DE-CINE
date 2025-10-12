package Clases;

import java.time.LocalDateTime;

public class Pelicula {
    private String nombrePelicula;
    private LocalDateTime fechaEstreno;
    private LocalDateTime fechaSalida;

    public Pelicula(String nombrePelicula) {
        this.nombrePelicula = nombrePelicula;
    }

    public Pelicula(String nombrePelicula, LocalDateTime fechaEstreno, LocalDateTime fechaSalida) {
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

    public LocalDateTime getFechaEstreno() {
        return fechaEstreno;
    }

    public void setFechaEstreno(LocalDateTime fechaEstreno) {
        this.fechaEstreno = fechaEstreno;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }
}
