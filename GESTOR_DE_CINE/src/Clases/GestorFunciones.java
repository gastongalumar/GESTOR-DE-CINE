package Clases;

import Excepciones.FechaInvalidaException;
import ManejoJSON.FuncionesJSON;

import java.util.ArrayList;
import java.util.List;

public class GestorFunciones {
    private static List<Funcion> listaFunciones = new ArrayList<>();

    public GestorFunciones() {
        // Ya no es necesario inicializar la lista aquí; se hace en la declaración
    }

    public static List<Funcion> getListaFunciones() {
        return listaFunciones;
    }

    public static void setListaFunciones(List<Funcion> listaFunciones) {
        GestorFunciones.listaFunciones = listaFunciones;
    }

    public static void agregarFuncion(Funcion f) throws FechaInvalidaException {

        if (f.getHorarioFuncion().toLocalDate().isBefore(f.getPelicula().getFechaEstreno()) || f.getHorarioFuncion().toLocalDate().isAfter(f.getPelicula().getFechaSalida())) {
            //throw new FechaInvalidaException("La fecha indicada no puede ser anterior a la fecha de estreno ni posterior a la fecha de salida");
           // GestorAdministrador.mostrarAlerta("La fecha indicada no puede ser anterior a la fecha de estreno ni posterior a la fecha de salida");
        } else {
            listaFunciones.add(f);
            FuncionesJSON.serializarFunciones(listaFunciones);
        }
    }


    public static void eliminarFuncion(Funcion f) {
        listaFunciones.remove(f);
        FuncionesJSON.serializarFunciones(listaFunciones);
    }
}
