package Clases.Utilidades;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ListaGenerica<T>  {
    private List<T> elementos;

    public ListaGenerica() {
        this.elementos = new ArrayList<>();
    }

    public ListaGenerica(List<T> elementosIniciales) {
        this.elementos = new ArrayList<>(elementosIniciales);
    }

    // Operaciones básicas
    public void agregar(T elemento) {
        elementos.add(elemento);
    }

    public boolean eliminar(T elemento) {
        return elementos.remove(elemento);
    }

    public boolean eliminarSi(Predicate<T> criterio) {
        return elementos.removeIf(criterio);
    }

    public T buscar(Predicate<T> criterio) {
        return elementos.stream()
                .filter(criterio)
                .findFirst()
                .orElse(null);
    }

    public List<T> buscarTodos(Predicate<T> criterio) {
        return elementos.stream()
                .filter(criterio)
                .toList();
    }

    public void actualizar(T elementoViejo, T elementoNuevo) {
        int index = elementos.indexOf(elementoViejo);
        if (index != -1) {
            elementos.set(index, elementoNuevo);
        }
    }

    public void actualizarSi(Predicate<T> criterio, T elementoNuevo) {
        for (int i = 0; i < elementos.size(); i++) {
            if (criterio.test(elementos.get(i))) {
                elementos.set(i, elementoNuevo);
                return;
            }
        }
    }

    // Getters y utilidades
    public List<T> obtenerTodos() {
        return new ArrayList<>(elementos);
    }

    public int tamaño() {
        return elementos.size();
    }

    public boolean estaVacia() {
        return elementos.isEmpty();
    }

    public void limpiar() {
        elementos.clear();
    }

    public boolean existe(Predicate<T> criterio) {
        return elementos.stream().anyMatch(criterio);
    }


    public List<T> getElementos() {
        return elementos;
    }

    public void setElementos(List<T> elementos) {
        this.elementos = elementos;
    }

    public T obtener(int i) {
        return elementos.get(i);

    }
}