package Clases;

import java.time.Duration;
import java.time.LocalDate;

public class Pelicula {
    private String nombrePelicula;
    private LocalDate fechaEstreno;
    private LocalDate fechaSalida;
    private String rutaImagen;
    private Duration duracion;

    public Pelicula(String nombrePelicula) {
        this.nombrePelicula = nombrePelicula;
        this.fechaEstreno = null;
        this.fechaSalida = null;
    }

    // Sobrecarga para crear la película con fechas
    public Pelicula(String nombrePelicula, LocalDate fechaEstreno, LocalDate fechaSalida) {
        this.nombrePelicula = nombrePelicula;
        this.fechaEstreno = fechaEstreno;
        this.fechaSalida = fechaSalida;
    }

    public Pelicula(String nombre, String rutaImagen, LocalDate estreno, LocalDate salida, Duration duracion) {
        this.nombrePelicula = nombre;
        this.rutaImagen = rutaImagen;
        this.fechaEstreno = estreno;
        this.fechaSalida = salida;
        this.duracion = duracion;
    }


    public Duration getDuracion() {
        return duracion;
    }

    public void setDuracion(Duration duracion) {
        this.duracion = duracion;
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

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "nombrePelicula='" + nombrePelicula + '\'' +
                ", fechaEstreno=" + fechaEstreno +
                ", fechaSalida=" + fechaSalida +
                ", rutaImagen='" + rutaImagen + '\'' +
                '}';
    }
}
