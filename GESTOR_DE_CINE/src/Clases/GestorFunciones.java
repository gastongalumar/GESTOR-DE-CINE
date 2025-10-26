package Clases;

import java.util.ArrayList;
import java.util.List;

public class GestorFunciones {
   private static List<Funcion> listaFunciones = new ArrayList<>();

    public GestorFunciones(){
        // Ya no es necesario inicializar la lista aquí; se hace en la declaración
    }

    public static List<Funcion> getListaFunciones() {
        return listaFunciones;
    }

    public static void setListaFunciones(List<Funcion> listaFunciones) {
        GestorFunciones.listaFunciones = listaFunciones;
    }

    public static void agregarFuncion(Funcion f){
        listaFunciones.add(f);
    }

    public static void eliminarFuncion(Funcion f){
        listaFunciones.remove(f);
    }
}
