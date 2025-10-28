package Clases;

//crear la clase GestorDePagos

import ManejoJSON.FuncionesJSON;
import java.util.ArrayList;
import java.util.List;

public class GestorDePagos {
    private static List<Pago> listaPagos = new ArrayList<>();
    private static List<MetodoDePago> metodoDePagos = new ArrayList<>();

    public GestorDePagos() {
        // Ya no es necesario inicializar la lista aquí; se hace en la declaración
    }

    public static List<Pago> getListaPagos() {
        return listaPagos;
    }

    public static void setListaPagos(List<Pago> listaPagos) {
        GestorDePagos.listaPagos = listaPagos;
    }

    public static void agregarPago(Pago p) {
        listaPagos.add(p);
        FuncionesJSON.serializarPagos(listaPagos);
    }

    public static void eliminarPago(Pago p) {
        listaPagos.remove(p);
        FuncionesJSON.serializarPagos(listaPagos);
    }

    public static List<MetodoDePago> getMetodoDePagos() {
        return metodoDePagos;
    }

    public static void setMetodoDePagos(List<MetodoDePago> metodoDePagos) {
        GestorDePagos.metodoDePagos = metodoDePagos;
    }


    //Metodos


//agregar metodo de pago
    public void agregarMetodoDePago(MetodoDePago metodo) {
        metodoDePagos.add(metodo);
    }

    //eliminar metodo de pago
    public void eliminarMetodoDePago(MetodoDePago metodo) {
        metodoDePagos.remove(metodo);

    }

    //mostrar las opciones de pago disponibles
    public void mostrarOpcionesDePago() {
        System.out.println("Opciones de pago disponibles:");
        for (MetodoDePago metodo  : metodoDePagos) {
            System.out.println(metodo.getId() + ". " + metodo.getNombre());

        }
    }
}





