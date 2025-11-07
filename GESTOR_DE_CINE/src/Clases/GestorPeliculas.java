package Clases;

import ManejoJSON.FuncionesJSON;

import java.util.ArrayList;
import java.util.List;

public class GestorPeliculas {

    private static List<Pelicula> listaPeliculas = new ArrayList<>();

    public GestorPeliculas(){

    }

    public static List<Pelicula> getListaPeliculas() {
        return listaPeliculas;
    }

    public static void setListaPeliculas(List<Pelicula> listaPeliculas) {
        GestorPeliculas.listaPeliculas = listaPeliculas;
    }

    public static void agregarPelicula(Pelicula p){
        listaPeliculas.add(p);
        FuncionesJSON.serializarPeliculas(listaPeliculas);
    }
    public static void eliminarPelicula(Pelicula p){
        listaPeliculas.remove(p);
        FuncionesJSON.serializarPeliculas(listaPeliculas);
    }
}
