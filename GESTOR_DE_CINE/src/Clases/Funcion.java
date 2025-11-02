package Clases;

import java.time.LocalDateTime;
import java.util.List;

public class Funcion {
    private SalaCine sala;
    private Pelicula pelicula;
    private LocalDateTime horarioFuncion;
    private double precio = 5000.0;


    //CONSTRUCTOR


    public Funcion(SalaCine sala, double precio, LocalDateTime horarioFuncion, Pelicula pelicula) {
        this.sala = sala;
        this.precio = precio;
        this.horarioFuncion = horarioFuncion;
        this.pelicula = pelicula;
    }

    public Funcion(SalaCine sala, Pelicula pelicula, LocalDateTime horarioFuncion) {
        this.sala = sala;
        this.pelicula = pelicula;
        this.horarioFuncion = horarioFuncion;
        // Registrar automáticamente la función en el gestor
        GestorFunciones.agregarFuncion(this);
    }

    public Funcion(String nombreSala, String nombrePelicula, LocalDateTime horarioFuncion, List<Pelicula> listaPeliculas, double precio){
        boolean encontrado = true;
        SalaCine salaEncontrada = null;
        Pelicula peliculaEncontrada = null;
        for(Funcion f: GestorFunciones.getListaFunciones()){
            if(f.getSala().getNombreSala().equalsIgnoreCase(nombreSala)){
                salaEncontrada = f.getSala();
                encontrado = true;
            }
        }
        for(Pelicula p: listaPeliculas){
            if(p.getNombrePelicula().equalsIgnoreCase(nombrePelicula)){
                encontrado = true;
                peliculaEncontrada = p;
            }
        }

        this.sala = salaEncontrada;
        this.pelicula = peliculaEncontrada;
        this.horarioFuncion = horarioFuncion;
       /* if(encontrado){
            GestorFunciones.agregarFuncion(this);
        }*/
    }

    //GETTER Y SETTER
    public LocalDateTime getHorarioFuncion() {
        return horarioFuncion;
    }

    public void setHorarioFuncion(LocalDateTime horarioFuncion) {
        this.horarioFuncion = horarioFuncion;
    }

    public SalaCine getSala() {
        return sala;
    }

    public void setSala(SalaCine sala) {
        this.sala = sala;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    //TO STRING
    @Override
    public String toString() {
        return "Funcion{" +
                "sala=" + sala +
                ", pelicula=" + pelicula +
                ", horarioFuncion=" + horarioFuncion +
                '}';
    }
}
