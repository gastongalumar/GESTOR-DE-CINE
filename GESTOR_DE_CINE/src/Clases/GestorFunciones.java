package Clases;

import Excepciones.FechaInvalidaException;
import ManejoJSON.FuncionesJSON;

public class GestorFunciones {
    private ListaGenerica<Funcion> listaFunciones;

    public GestorFunciones() {
        listaFunciones = new ListaGenerica<>();
    }


    public void agregarFuncion(Funcion f) throws FechaInvalidaException {

        if (f.getHorarioFuncion().toLocalDate().isBefore(f.getPelicula().getFechaEstreno()) || f.getHorarioFuncion().toLocalDate().isAfter(f.getPelicula().getFechaSalida())) {
        } else {
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
        FuncionesJSON.serializarFunciones(listaFunciones.getElementos());
    }
}
