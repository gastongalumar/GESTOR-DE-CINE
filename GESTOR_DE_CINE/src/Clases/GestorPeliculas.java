package Clases;

import ManejoJSON.FuncionesJSON;

import java.util.ArrayList;
import java.util.List;

public class GestorPeliculas {

    private static List<Pelicula> listaPeliculas = new ArrayList<>();

    public GestorPeliculas() {

    }

    public static List<Pelicula> getListaPeliculas() {
        return listaPeliculas;
    }

    public static void setListaPeliculas(List<Pelicula> listaPeliculas) {
        GestorPeliculas.listaPeliculas = listaPeliculas;
    }

    public static void agregarPelicula(Pelicula p) {
        listaPeliculas.add(p);
        FuncionesJSON.serializarPeliculas(listaPeliculas);
    }

    public static void eliminarPelicula(Pelicula p) {
        listaPeliculas.remove(p);
        FuncionesJSON.serializarPeliculas(listaPeliculas);
    }

    public void cargarDatosEjemplo() {
////        // Cargar algunas películas de ejemplo
////        Pelicula p1 = new Pelicula("Inception", 12/, 148);
////        Pelicula p2 = new Pelicula("The Dark Knight", "Acción", 152);
////        Pelicula p3 = new Pelicula("Interstellar", "Ciencia Ficción", 169);
//
//        listaPeliculas.add(p1);
//        listaPeliculas.add(p2);
//        listaPeliculas.add(p3);
//    }
    }
}
