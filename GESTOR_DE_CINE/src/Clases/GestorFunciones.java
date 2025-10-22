package Clases;

import java.util.ArrayList;
import java.util.List;

public class GestorFunciones {
   private static List<Funcion> listaFunciones;

    public GestorFunciones(){
        listaFunciones = new ArrayList<>();
    }

    public static List<Funcion> getListaFunciones() {
        return listaFunciones;
    }

    public static void setListaFunciones(List<Funcion> listaFunciones) {
        GestorFunciones.listaFunciones = listaFunciones;
    }

    public void agregarFuncion(Funcion f){
        listaFunciones.add(f);
    }
}
