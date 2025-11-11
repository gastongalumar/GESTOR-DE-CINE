package Clases;

import Enumeradores.EstadoAsiento;

import java.util.Arrays;

public class SalaCine {

    private String nombreSala;
    private int capacidad;
    private final int filas;
    private final int columnas;
    private final EstadoAsiento[][] asientos;


    public SalaCine(String s, int filas, int columnas) {
        this.nombreSala = s;
        this.filas = filas;
        this.columnas = columnas;
        this.asientos = new EstadoAsiento[filas][columnas];
        inicializarAsientosPorDefecto();
    }




    public String getNombreSala() {
        return nombreSala;
    }

    public void setNombreSala(String nombreSala) {
        this.nombreSala = nombreSala;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public EstadoAsiento[][] getAsientos() {
        return asientos;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }


    //METODOS
    private void inicializarAsientosPorDefecto() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                asientos[i][j] = EstadoAsiento.LIBRE;
            }
        }
    }


    public void cargarEstadosDesdeJSON(org.json.JSONArray matrizAsientos) {
        try {

            int ocupadosCargados = 0;

            for (int i = 0; i < matrizAsientos.length() && i < filas; i++) {
                org.json.JSONArray filaArray = matrizAsientos.getJSONArray(i);
                for (int j = 0; j < filaArray.length() && j < columnas; j++) {
                    org.json.JSONObject asientoJson = filaArray.getJSONObject(j);
                    String estadoStr = asientoJson.getString("estado");

                    if ("OCUPADO".equals(estadoStr)) {
                        asientos[i][j] = EstadoAsiento.OCUPADO;
                        ocupadosCargados++;
                    } else {
                        asientos[i][j] = EstadoAsiento.LIBRE;
                    }
                }
            }


        } catch (Exception e) {
            System.err.println("Error al cargar estados desde JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public EstadoAsiento getEstadoAsiento(int fila, int columna) {
        if (fila >= 0 && fila < filas && columna >= 0 && columna < columnas) {
            return asientos[fila][columna];
        }
        return null;
    }

    public void toggleSeleccionAsiento(int fila, int columna) {
        if (fila >= 0 && fila < filas && columna >= 0 && columna < columnas) {
            if (asientos[fila][columna] == EstadoAsiento.LIBRE) {
                asientos[fila][columna] = EstadoAsiento.SELECCIONADO;
            } else if (asientos[fila][columna] == EstadoAsiento.SELECCIONADO) {
                asientos[fila][columna] = EstadoAsiento.LIBRE;
            }
        }
    }


    public int confirmarSelecciones() {
        int confirmados = 0;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (asientos[i][j] == EstadoAsiento.SELECCIONADO) {
                    asientos[i][j] = EstadoAsiento.OCUPADO;
                    confirmados++;
                }
            }
        }
        System.out.println(confirmados + " asientos confirmados como ocupados");
        return confirmados;
    }


    public void limpiarSelecciones() {
        int limpiados = 0;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (asientos[i][j] == EstadoAsiento.SELECCIONADO) {
                    asientos[i][j] = EstadoAsiento.LIBRE;
                    limpiados++;
                }
            }
        }
    }

    public int contarAsientosSeleccionados() {
        int count = 0;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (asientos[i][j] == EstadoAsiento.SELECCIONADO) {
                    count++;
                }
            }
        }
        return count;
    }

    public int contarAsientosOcupados() {
        int count = 0;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (asientos[i][j] == EstadoAsiento.OCUPADO) {
                    count++;
                }
            }
        }
        return count;
    }

    public int contarAsientosLibres() {
        int count = 0;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (asientos[i][j] == EstadoAsiento.LIBRE) {
                    count++;
                }
            }
        }
        return count;
    }


    @Override
    public String toString() {
        return "SalaCine{" +
                "nombreSala='" + nombreSala + '\'' +
                ", capacidad=" + capacidad +
                ", filas=" + filas +
                ", columnas=" + columnas +
                ", asientos=" + Arrays.toString(asientos) +
                '}';
    }
}