package ManejoJSON;

import Clases.SalaCine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import ManejoJSON.JSONUtiles;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GestorJsonAsientos {
    // Archivo por defecto si no se especifica uno
    private static final String ARCHIVO_POR_DEFECTO = "Asientos.json";
    private final SalaCine sala;
    private final String archivoAsientos;

    public GestorJsonAsientos(SalaCine sala) {
        this(sala, ARCHIVO_POR_DEFECTO);
    }

    // Constructor nuevo que permite especificar el archivo JSON (por función, por ejemplo)
    public GestorJsonAsientos(SalaCine sala, String archivoAsientos) {
        this.sala = sala;
        this.archivoAsientos = archivoAsientos != null && !archivoAsientos.isEmpty() ? archivoAsientos : ARCHIVO_POR_DEFECTO;
        inicializarArchivo();
    }

    private void inicializarArchivo() {
        File archivo = new File(archivoAsientos);
        if (!archivo.exists()) {
            System.out.println("📝 Creando nuevo archivo JSON... -> " + archivoAsientos);
            guardarEstadoCompleto();
        } else {
            System.out.println("📁 Archivo JSON encontrado: " + archivoAsientos);
        }
    }

    public boolean cargarEstadoGuardado() {
        try {
            JSONObject estadoSala = JSONUtiles.leerObject(archivoAsientos);
            if (estadoSala == null) {
                System.err.println("❌ No se pudo leer el archivo JSON o está vacío");
                return false;
            }

            JSONArray matrizAsientos = estadoSala.getJSONArray("matrizAsientos");

            System.out.println("📊 ===== CARGANDO ESTADO DESDE JSON =====");
            System.out.println("📅 Última actualización: " + estadoSala.getString("fechaActualizacion"));

            int libresJson = 0, ocupadosJson = 0;
            for (int i = 0; i < matrizAsientos.length(); i++) {
                JSONArray filaArray = matrizAsientos.getJSONArray(i);
                for (int j = 0; j < filaArray.length(); j++) {
                    JSONObject asientoJson = filaArray.getJSONObject(j);
                    String estado = asientoJson.getString("estado");
                    switch (estado) {
                        case "LIBRE": libresJson++; break;
                        case "OCUPADO": ocupadosJson++; break;
                    }
                }
            }

            System.out.println("📦 ESTADOS EN ARCHIVO JSON:");
            System.out.println("   ⚪ Libres: " + libresJson);
            System.out.println("   🔴 Ocupados: " + ocupadosJson);

            // Cargar los estados en la sala
            sala.cargarEstadosDesdeJSON(matrizAsientos);

            // Mostrar información después de cargar
            System.out.println("📈 ESTADOS DESPUÉS DE CARGAR:");
            System.out.println("   ⚪ Libres: " + sala.contarAsientosLibres());
            System.out.println("   🔴 Ocupados: " + sala.contarAsientosOcupados());
            System.out.println("   🔵 Seleccionados: " + sala.contarAsientosSeleccionados() + " (temporal)");
            System.out.println("✅ ===== ESTADO CARGADO CORRECTAMENTE =====");

            return true;

        } catch (JSONException e) {
            System.err.println("❌ Error de JSON al cargar estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error inesperado al cargar estado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Guarda el estado completo en JSON
     */
    public void guardarEstadoCompleto() {
        try {
            JSONObject estadoSala = new JSONObject();
            estadoSala.put("fechaActualizacion", java.time.LocalDateTime.now().toString());
            estadoSala.put("totalFilas", sala.getFilas());
            estadoSala.put("totalColumnas", sala.getColumnas());
            estadoSala.put("asientosOcupados", sala.contarAsientosOcupados());

            // Matriz de asientos
            JSONArray matrizAsientos = new JSONArray();
            for (int i = 0; i < sala.getFilas(); i++) {
                JSONArray filaArray = new JSONArray();
                for (int j = 0; j < sala.getColumnas(); j++) {
                    JSONObject asiento = new JSONObject();
                    asiento.put("fila", i);
                    asiento.put("columna", j);
                    asiento.put("estado", sala.getEstadoAsiento(i, j).toString());
                    asiento.put("etiqueta", generarEtiquetaAsiento(i, j));
                    filaArray.put(asiento);
                }
                matrizAsientos.put(filaArray);
            }
            estadoSala.put("matrizAsientos", matrizAsientos);

            JSONUtiles.grabar(estadoSala, archivoAsientos);

            System.out.println("💾 Estado guardado en JSON: -> " + archivoAsientos);
            System.out.println("   🔴 Ocupados: " + sala.contarAsientosOcupados());
            System.out.println("   ⚪ Libres: " + sala.contarAsientosLibres());

        } catch (Exception e) {
            System.err.println("❌ Error al guardar el estado de asientos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Confirma las selecciones y guarda en JSON
     */
    public int confirmarSelecciones() {
        int confirmados = sala.confirmarSelecciones();
        guardarEstadoCompleto();
        return confirmados;
    }

    public JSONObject generarReporte() {
        try {
            JSONObject reporte = new JSONObject();
            reporte.put("fechaReporte", java.time.LocalDateTime.now().toString());
            reporte.put("totalAsientos", sala.getFilas() * sala.getColumnas());
            reporte.put("asientosSeleccionados", sala.contarAsientosSeleccionados());
            reporte.put("asientosOcupados", sala.contarAsientosOcupados());
            reporte.put("asientosLibres", sala.contarAsientosLibres());

            try {
                JSONObject estadoSala = JSONUtiles.leerObject(archivoAsientos);
                if (estadoSala != null) {
                    reporte.put("ultimaActualizacion", estadoSala.getString("fechaActualizacion"));
                }
            } catch (Exception e) {
                reporte.put("ultimaActualizacion", "No disponible");
            }

            return reporte;

        } catch (Exception e) {
            System.err.println("❌ Error al generar reporte: " + e.getMessage());
            try {
                return new JSONObject().put("error", "No se pudo generar el reporte");
            } catch (JSONException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public boolean archivoExiste() {
        File archivo = new File(archivoAsientos);
        return archivo.exists();
    }

    private String generarEtiquetaAsiento(int fila, int columna) {
        return String.valueOf((char) ('A' + columna)) + (fila + 1);
    }


    //GETTERS Y SETTERS
    public String getJsonCompleto() {
        try {
            return new String(Files.readAllBytes(Paths.get(archivoAsientos)));
        } catch (IOException e) {
            return "{}";
        }
    }

    /**
     * Limpia selecciones y guarda
     */
    public void limpiarSelecciones() {
        sala.limpiarSelecciones();
        guardarEstadoCompleto();
    }
}