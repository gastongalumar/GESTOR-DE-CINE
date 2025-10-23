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
     * Carga los estados desde JSON - OCUPADO o LIBRE
     */
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

    /**
     * Toggle entre LIBRE y SELECCIONADO (los OCUPADOS no se pueden cambiar)
     */
    public void toggleSeleccionAsiento(int fila, int columna) {
        if (fila >= 0 && fila < filas && columna >= 0 && columna < columnas) {
            if (asientos[fila][columna] == EstadoAsiento.LIBRE) {
                asientos[fila][columna] = EstadoAsiento.SELECCIONADO;
                System.out.println("🔵 Asiento " + (fila+1) + "-" + (char)('A'+columna) + " seleccionado");
            } else if (asientos[fila][columna] == EstadoAsiento.SELECCIONADO) {
                asientos[fila][columna] = EstadoAsiento.LIBRE;
                System.out.println("⚪ Asiento " + (fila+1) + "-" + (char)('A'+columna) + " liberado");
            } else {
                System.out.println("🔴 Asiento " + (fila+1) + "-" + (char)('A'+columna) + " está OCUPADO - No se puede cambiar");
            }
        }
    }

    /**
     * Confirma las selecciones - convierte SELECCIONADO a OCUPADO
     */
    public int confirmarSelecciones() {
        int confirmados = 0;
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                if (asientos[i][j] == EstadoAsiento.SELECCIONADO) {
                    asientos[i][j] = EstadoAsiento.OCUPADO;
                    confirmados++;
                    System.out.println("✅ Asiento " + (i+1) + "-" + (char)('A'+j) + " confirmado como OCUPADO");
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