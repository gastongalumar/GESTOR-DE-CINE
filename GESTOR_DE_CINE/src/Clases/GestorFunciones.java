package Clases;

import Excepciones.FechaInvalidaException;
import ManejoJSON.FuncionesJSON;

public class GestorFunciones {
    //private static List<Funcion> listaFunciones = new ArrayList<>();
    private ListaGenerica<Funcion> listaFunciones = new ListaGenerica<>();

    public GestorFunciones() {
        // Ya no es necesario inicializar la lista aquí; se hace en la declaración
        listaFunciones = new ListaGenerica<>();
    }

   /* public static List<Funcion> getListaFunciones() {
        return listaFunciones;
    }

    public static void setListaFunciones(List<Funcion> listaFunciones) {
        GestorFunciones.listaFunciones = listaFunciones;
    }*/

    public void agregarFuncion(Funcion f) throws FechaInvalidaException {

        if (f.getHorarioFuncion().toLocalDate().isBefore(f.getPelicula().getFechaEstreno()) || f.getHorarioFuncion().toLocalDate().isAfter(f.getPelicula().getFechaSalida())) {
            //throw new FechaInvalidaException("La fecha indicada no puede ser anterior a la fecha de estreno ni posterior a la fecha de salida");
           // GestorAdministrador.mostrarAlerta("La fecha indicada no puede ser anterior a la fecha de estreno ni posterior a la fecha de salida");
        } else {
            //listaFunciones.add(f);
            listaFunciones.agregar(f);
            FuncionesJSON.serializarFunciones(listaFunciones.getElementos());
        }
    }


    public void eliminarFuncion(Funcion f) {
        listaFunciones.getElementos().remove(f);
        FuncionesJSON.serializarFunciones(listaFunciones.getElementos());
    }

    public ListaGenerica<Funcion> getListaFunciones() {
        return listaFunciones;
    }

    public void setListaFunciones(ListaGenerica<Funcion> listaFunciones) {
        this.listaFunciones = listaFunciones;
    }
}
