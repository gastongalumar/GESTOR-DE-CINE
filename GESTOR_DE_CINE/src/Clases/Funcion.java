package Clases;

import java.time.LocalDateTime;
import java.util.List;

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

    public Funcion(String nombreSala, String nombrePelicula, LocalDateTime horarioFuncion, List<Pelicula> listaPeliculas){
        boolean encontrado = true;
        Sala salaEncontrada = null;
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


    @Override
    public String toString() {
        return "Funcion{" +
                "sala=" + sala +
                ", pelicula=" + pelicula +
                ", horarioFuncion=" + horarioFuncion +
                '}';
    }
}
