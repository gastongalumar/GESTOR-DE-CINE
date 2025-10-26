package Clases;

import java.util.ArrayList;
import java.util.List;

public class GestorPeliculas {

    private List<Pelicula> listaPeliculas;

    public GestorPeliculas(){
        listaPeliculas = new ArrayList<>();
    }

    public List<Pelicula> getListaPeliculas() {
        return listaPeliculas;
    }

    public void setListaPeliculas(List<Pelicula> listaPeliculas) {
        this.listaPeliculas = listaPeliculas;
    }
}
