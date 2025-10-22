package Clases;

public class SalaCine {
    public enum EstadoAsiento { LIBRE, SELECCIONADO, OCUPADO }

    private final int filas;
    private final int columnas;
    private final EstadoAsiento[][] asientos;

    public SalaCine(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.asientos = new EstadoAsiento[filas][columnas];
        inicializarAsientosPorDefecto();
    }

    private void inicializarAsientosPorDefecto() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                asientos[i][j] = EstadoAsiento.LIBRE;
            }
        }

        // Algunos asientos ocupados por defecto
        asientos[2][3] = EstadoAsiento.OCUPADO;
        asientos[5][7] = EstadoAsiento.OCUPADO;
        asientos[8][1] = EstadoAsiento.OCUPADO;
    }

    /**
     * Carga los estados desde un JSONArray
     */
    public void cargarEstadosDesdeJSON(org.json.JSONArray matrizAsientos) {
        try {
            System.out.println("🔄 Cargando estados desde JSON...");
            int cambios = 0;
            for (int i = 0; i < matrizAsientos.length() && i < filas; i++) {
                org.json.JSONArray filaArray = matrizAsientos.getJSONArray(i);
                for (int j = 0; j < filaArray.length() && j < columnas; j++) {
                    org.json.JSONObject asientoJson = filaArray.getJSONObject(j);
                    String estadoStr = asientoJson.getString("estado");
                    EstadoAsiento estado = EstadoAsiento.valueOf(estadoStr);

                    // Solo actualizar si es diferente
                    if (asientos[i][j] != estado) {
                        asientos[i][j] = estado;
                        cambios++;
                    }
                }
            }
            System.out.println("✅ " + cambios + " estados actualizados desde JSON");
        } catch (Exception e) {
            System.err.println("❌ Error al cargar estados desde JSON: " + e.getMessage());
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
            // Los asientos OCUPADOS no se pueden cambiar
        }
    }

    /**
     * Establece el estado de un asiento específico
     */
    public void setEstadoAsiento(int fila, int columna, EstadoAsiento estado) {
        if (fila >= 0 && fila < filas && columna >= 0 && columna < columnas) {
            // No permitir cambiar asientos ocupados
            if (asientos[fila][columna] != EstadoAsiento.OCUPADO || estado == EstadoAsiento.OCUPADO) {
                asientos[fila][columna] = estado;
            }
        }
    }

    /**
     * Limpia todas las selecciones (convierte SELECCIONADO a LIBRE)
     */
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

    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }
}

//package Clases;
//
//public class SalaCine {
//    public enum EstadoAsiento { LIBRE, SELECCIONADO, OCUPADO }
//
//    private final int filas;
//    private final int columnas;
//    private final EstadoAsiento[][] asientos;
//
//    public SalaCine(int filas, int columnas) {
//        this.filas = filas;
//        this.columnas = columnas;
//        this.asientos = new EstadoAsiento[filas][columnas];
//        inicializarAsientos();
//    }
//
//    private void inicializarAsientos() {
//        for (int i = 0; i < filas; i++) {
//            for (int j = 0; j < columnas; j++) {
//                asientos[i][j] = EstadoAsiento.LIBRE;
//            }
//        }
//
//        // Simular algunos asientos ocupados
//        asientos[2][3] = EstadoAsiento.OCUPADO;
//        asientos[5][7] = EstadoAsiento.OCUPADO;
//        asientos[8][1] = EstadoAsiento.OCUPADO;
//    }
//
//    public EstadoAsiento getEstadoAsiento(int fila, int columna) {
//        if (fila >= 0 && fila < filas && columna >= 0 && columna < columnas) {
//            return asientos[fila][columna];
//        }
//        return null;
//    }
//
//    public void toggleSeleccionAsiento(int fila, int columna) {
//        if (fila >= 0 && fila < filas && columna >= 0 && columna < columnas) {
//            if (asientos[fila][columna] == EstadoAsiento.LIBRE) {
//                asientos[fila][columna] = EstadoAsiento.SELECCIONADO;
//            } else if (asientos[fila][columna] == EstadoAsiento.SELECCIONADO) {
//                asientos[fila][columna] = EstadoAsiento.LIBRE;
//            }
//            // Los asientos OCUPADOS no se pueden cambiar
//        }
//    }
//
//    /**
//     * Establece el estado de un asiento específico
//     */
//    public void setEstadoAsiento(int fila, int columna, EstadoAsiento estado) {
//        if (fila >= 0 && fila < filas && columna >= 0 && columna < columnas) {
//            // No permitir cambiar asientos ocupados
//            if (asientos[fila][columna] != EstadoAsiento.OCUPADO || estado == EstadoAsiento.OCUPADO) {
//                asientos[fila][columna] = estado;
//            }
//        }
//    }
//
//    public int contarAsientosSeleccionados() {
//        int count = 0;
//        for (int i = 0; i < filas; i++) {
//            for (int j = 0; j < columnas; j++) {
//                if (asientos[i][j] == EstadoAsiento.SELECCIONADO) {
//                    count++;
//                }
//            }
//        }
//        return count;
//    }
//
//    public int getFilas() { return filas; }
//    public int getColumnas() { return columnas; }
//}
//
////package Clases;
////
////public class SalaCine {
////    public enum EstadoAsiento { LIBRE, SELECCIONADO, OCUPADO }
////
////    private final int filas;
////    private final int columnas;
////    private final EstadoAsiento[][] asientos;
////
////    public SalaCine(int filas, int columnas) {
////        this.filas = filas;
////        this.columnas = columnas;
////        this.asientos = new EstadoAsiento[filas][columnas];
////        inicializarAsientos();
////    }
////
////    private void inicializarAsientos() {
////        for (int i = 0; i < filas; i++) {
////            for (int j = 0; j < columnas; j++) {
////                asientos[i][j] = EstadoAsiento.LIBRE;
////            }
////        }
////
////        // Simular algunos asientos ocupados
////        asientos[2][3] = EstadoAsiento.OCUPADO;
////        asientos[5][7] = EstadoAsiento.OCUPADO;
////        asientos[8][1] = EstadoAsiento.OCUPADO;
////    }
////
////    public EstadoAsiento getEstadoAsiento(int fila, int columna) {
////        if (fila >= 0 && fila < filas && columna >= 0 && columna < columnas) {
////            return asientos[fila][columna];
////        }
////        return null;
////    }
////
////    public void toggleSeleccionAsiento(int fila, int columna) {
////        if (fila >= 0 && fila < filas && columna >= 0 && columna < columnas) {
////            if (asientos[fila][columna] == EstadoAsiento.LIBRE) {
////                asientos[fila][columna] = EstadoAsiento.SELECCIONADO;
////            } else if (asientos[fila][columna] == EstadoAsiento.SELECCIONADO) {
////                asientos[fila][columna] = EstadoAsiento.LIBRE;
////            }
////            // Los asientos OCUPADOS no se pueden cambiar
////        }
////    }
////
////    public int contarAsientosSeleccionados() {
////        int count = 0;
////        for (int i = 0; i < filas; i++) {
////            for (int j = 0; j < columnas; j++) {
////                if (asientos[i][j] == EstadoAsiento.SELECCIONADO) {
////                    count++;
////                }
////            }
////        }
////        return count;
////    }
////
////    public int getFilas() { return filas; }
////    public int getColumnas() { return columnas; }
////}
//
