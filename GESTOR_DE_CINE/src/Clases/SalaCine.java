package Clases;

import Enumeradores.EstadoAsiento;

public class SalaCine {

    private String nombreSala;
    private int capacidad;
    private final int filas;
    private final int columnas;
    private final EstadoAsiento[][] asientos;


    //CONSTRUCTOR
//    public SalaCine(String s, int columnas) {
//        this.nombreSala = s;
//        this.columnas = columnas;
//        this.filas = 1; // Default value for filas
//        this.asientos = new EstadoAsiento[filas][columnas];
//        inicializarAsientosPorDefecto();
//    }

    public SalaCine(String s, int filas, int columnas) {
        this.nombreSala = s;
        this.filas = filas;
        this.columnas = columnas;
        this.asientos = new EstadoAsiento[filas][columnas];
        inicializarAsientosPorDefecto();
    }


    //GETTER Y SETTER


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
            System.out.println("🔄 Cargando estados desde JSON...");
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

            System.out.println("✅ Estados cargados desde JSON:");
            System.out.println("   🔴 Ocupados: " + ocupadosCargados);
            System.out.println("   ⚪ Libres: " + (filas * columnas - ocupadosCargados));

        } catch (Exception e) {
            System.err.println("❌ Error al cargar estados desde JSON: " + e.getMessage());
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
                System.out.println("🔵 Asiento " + (fila + 1) + "-" + (char) ('A' + columna) + " seleccionado");
            } else if (asientos[fila][columna] == EstadoAsiento.SELECCIONADO) {
                asientos[fila][columna] = EstadoAsiento.LIBRE;
                System.out.println("⚪ Asiento " + (fila + 1) + "-" + (char) ('A' + columna) + " liberado");
            } else {
                System.out.println("🔴 Asiento " + (fila + 1) + "-" + (char) ('A' + columna) + " está OCUPADO - No se puede cambiar");
            }
        }
    }


    // Método para identificar pasillos basado en tu diagrama
    private boolean esPasillo(int fila, int columna) {
        String letraFila = obtenerLetraFila(fila);
        int numeroColumna = columna + 1;

        // Pasillos principales entre filas
        if (letraFila.equals("A") && (numeroColumna == 1 || numeroColumna == 2)) return true; // A0, A1
        if (letraFila.equals("B") && (numeroColumna == 2 || numeroColumna == 3)) return true; // B2, B3
        if (letraFila.equals("C") && (numeroColumna == 2 || numeroColumna == 3)) return true; // C2, C3
        if (letraFila.equals("D") && (numeroColumna == 2 || numeroColumna == 3 || numeroColumna == 4)) return true; // D2, D3, D4
        if (letraFila.equals("E") && (numeroColumna == 2 || numeroColumna == 3)) return true; // E2, E3
        if (letraFila.equals("F") && (numeroColumna >= 1 && numeroColumna <= 3)) return true; // F1, F2, F3
        if (letraFila.equals("G") && (numeroColumna >= 1 && numeroColumna <= 3)) return true; // G1, G2, G3
        if (letraFila.equals("H") && (numeroColumna >= 1 && numeroColumna <= 3)) return true; // H1, H2, H3
        if (letraFila.equals("M") && (numeroColumna >= 1 && numeroColumna <= 3)) return true; // M1, M2, M3

        // Columnas que son pasillos (basado en el patrón del diagrama)
        if (numeroColumna == 4 || numeroColumna == 8 || numeroColumna == 12) return true;

        return false;
    }

    // Mapeo corregido de filas
    private String obtenerLetraFila(int fila) {
        String[] letrasFilas = {"A", "B", "C", "D", "E", "F", "G", "H", "M"};
        if (fila >= 0 && fila < letrasFilas.length) {
            return letrasFilas[fila];
        }
        return "?";
    }

    public int confirmarSelecciones() {
        int confirmados = 0;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (asientos[i][j] == EstadoAsiento.SELECCIONADO) {
                    asientos[i][j] = EstadoAsiento.OCUPADO;
                    confirmados++;
                    System.out.println("✅ Asiento " + (i + 1) + "-" + (char) ('A' + j) + " confirmado como OCUPADO");
                }
            }
        }
        System.out.println("🎉 " + confirmados + " asientos confirmados como ocupados");
        return confirmados;
    }

    public void setEstadoAsiento(int fila, int columna, EstadoAsiento estado) {
        if (fila >= 0 && fila < filas && columna >= 0 && columna < columnas) {
            if (asientos[fila][columna] != EstadoAsiento.OCUPADO || estado == EstadoAsiento.OCUPADO) {
                asientos[fila][columna] = estado;
            }
        }
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
        System.out.println("🧹 " + limpiados + " selecciones limpiadas");
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

    /**
     * Libera un asiento ocupado (cambia estado de OCUPADO a LIBRE)
     */
    public boolean liberarAsiento(int fila, int columna) {
        try {
            // Asumiendo que tienes una matriz de estados de asientos llamada 'asientos'
            // y un enum EstadoAsiento con valores LIBRE, SELECCIONADO, OCUPADO

            EstadoAsiento estadoActual = asientos[fila][columna];

            if (estadoActual == EstadoAsiento.OCUPADO) {
                asientos[fila][columna] = EstadoAsiento.LIBRE;
                System.out.println("✅ Asiento [" + fila + "," + columna + "] liberado de OCUPADO a LIBRE");
                return true;
            } else {
                System.out.println("⚠️ Asiento [" + fila + "," + columna + "] no estaba ocupado (estado: " + estadoActual + ")");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Error al liberar asiento [" + fila + "," + columna + "]: " + e.getMessage());
            return false;
        }
    }

}