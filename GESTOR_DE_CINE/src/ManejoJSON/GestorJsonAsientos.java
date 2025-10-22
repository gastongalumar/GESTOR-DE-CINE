package ManejoJSON;

import Clases.SalaCine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import ManejoJSON.JSONUtiles;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GestorJsonAsientos {
    private static final String ARCHIVO_ASIENTOS = "Asientos.json";
    private final SalaCine sala;

    public GestorJsonAsientos(SalaCine sala) {
        this.sala = sala;
        inicializarArchivo();
    }

    /**
     * Inicializa el archivo JSON si no existe
     */
    private void inicializarArchivo() {
        File archivo = new File(ARCHIVO_ASIENTOS);
        if (!archivo.exists()) {
            System.out.println("📝 Creando nuevo archivo JSON...");
            guardarEstadoCompleto();
        } else {
            System.out.println("📁 Archivo JSON encontrado: " + ARCHIVO_ASIENTOS);
        }
    }

    /**
     * Carga el estado guardado desde el archivo JSON y actualiza la sala
     */
    public boolean cargarEstadoGuardado() {
        try {
            JSONTokener tokener = JSONUtiles.leer(ARCHIVO_ASIENTOS);
            if (tokener == null) {
                System.err.println("❌ No se pudo leer el archivo JSON");
                return false;
            }

            JSONObject estadoSala = new JSONObject(tokener);
            JSONArray matrizAsientos = estadoSala.getJSONArray("matrizAsientos");

            System.out.println("📊 Cargando estado guardado...");
            System.out.println("📅 Última actualización: " + estadoSala.getString("fechaActualizacion"));
            System.out.println("🎯 Asientos seleccionados guardados: " + estadoSala.getInt("asientosSeleccionados"));

            // Cargar los estados en la sala
            sala.cargarEstadosDesdeJSON(matrizAsientos);

            // Mostrar información de carga
            System.out.println("✅ Estado cargado correctamente desde: " + ARCHIVO_ASIENTOS);
            System.out.println("📈 Resumen actual:");
            System.out.println("   🟢 Libres: " + sala.contarAsientosLibres());
            System.out.println("   🔵 Seleccionados: " + sala.contarAsientosSeleccionados());
            System.out.println("   🔴 Ocupados: " + sala.contarAsientosOcupados());

            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al cargar estado guardado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Guarda el estado completo de la sala en el archivo JSON
     */
    public void guardarEstadoCompleto() {
        try {
            JSONObject estadoSala = new JSONObject();
            estadoSala.put("fechaActualizacion", java.time.LocalDateTime.now().toString());
            estadoSala.put("totalFilas", sala.getFilas());
            estadoSala.put("totalColumnas", sala.getColumnas());
            estadoSala.put("asientosSeleccionados", sala.contarAsientosSeleccionados());
            estadoSala.put("asientosLibres", sala.contarAsientosLibres());
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

            // Usar JSONUtiles para guardar
            guardarJsonObject(estadoSala);

            System.out.println("💾 Estado guardado en: " + ARCHIVO_ASIENTOS +
                    " | 🎯 Seleccionados: " + sala.contarAsientosSeleccionados());

        } catch (Exception e) {
            System.err.println("❌ Error al guardar el estado de asientos: " + e.getMessage());
        }
    }

    /**
     * Guarda un JSONObject usando JSONUtiles
     */
    private void guardarJsonObject(JSONObject jsonObject) {
        try {
            FileWriter file = new FileWriter(ARCHIVO_ASIENTOS);
            try {
                file.write(jsonObject.toString(4)); // 4 espacios de indentación
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            file.flush();
            file.close();
        } catch (IOException e) {
            System.err.println("❌ Error al escribir archivo: " + e.getMessage());
        }
    }

    /**
     * Guarda el estado de un asiento específico
     */
    public void guardarEstadoAsiento(int fila, int columna) {
        try {
            // Leer el archivo existente usando JSONUtiles
            JSONTokener tokener = JSONUtiles.leer(ARCHIVO_ASIENTOS);
            if (tokener == null) {
                guardarEstadoCompleto();
                return;
            }

            JSONObject estadoSala = new JSONObject(tokener);

            // Actualizar fecha y contador
            estadoSala.put("fechaActualizacion", java.time.LocalDateTime.now().toString());
            estadoSala.put("asientosSeleccionados", sala.contarAsientosSeleccionados());
            estadoSala.put("asientosLibres", sala.contarAsientosLibres());
            estadoSala.put("asientosOcupados", sala.contarAsientosOcupados());

            // Actualizar el asiento específico en la matriz
            JSONArray matrizAsientos = estadoSala.getJSONArray("matrizAsientos");
            if (fila < matrizAsientos.length()) {
                JSONArray filaArray = matrizAsientos.getJSONArray(fila);
                if (columna < filaArray.length()) {
                    JSONObject asiento = filaArray.getJSONObject(columna);
                    asiento.put("estado", sala.getEstadoAsiento(fila, columna).toString());
                }
            }

            // Guardar los cambios
            guardarJsonObject(estadoSala);

        } catch (Exception e) {
            System.err.println("❌ Error al actualizar asiento: " + e.getMessage());
            guardarEstadoCompleto();
        }
    }

    /**
     * Genera un reporte resumido de la sala usando datos actuales
     */
    public JSONObject generarReporte() {
        try {
            JSONObject reporte = new JSONObject();
            reporte.put("fechaReporte", java.time.LocalDateTime.now().toString());
            reporte.put("totalAsientos", sala.getFilas() * sala.getColumnas());
            reporte.put("asientosSeleccionados", sala.contarAsientosSeleccionados());
            reporte.put("asientosLibres", sala.contarAsientosLibres());
            reporte.put("asientosOcupados", sala.contarAsientosOcupados());

            // Leer la última actualización del archivo
            try {
                JSONTokener tokener = JSONUtiles.leer(ARCHIVO_ASIENTOS);
                if (tokener != null) {
                    JSONObject estadoSala = new JSONObject(tokener);
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

    /**
     * Verifica si el archivo JSON existe y es válido
     */
    public boolean archivoExiste() {
        File archivo = new File(ARCHIVO_ASIENTOS);
        if (!archivo.exists()) {
            return false;
        }

        try {
            JSONTokener tokener = JSONUtiles.leer(ARCHIVO_ASIENTOS);
            if (tokener != null) {
                new JSONObject(tokener);
                return true;
            }
        } catch (Exception e) {
            System.err.println("❌ Archivo JSON corrupto: " + e.getMessage());
        }
        return false;
    }

    /**
     * Genera la etiqueta del asiento (ej: "A1", "B2")
     */
    private String generarEtiquetaAsiento(int fila, int columna) {
        return String.valueOf((char) ('A' + columna)) + (fila + 1);
    }

    /**
     * Obtiene el contenido completo del archivo JSON como String
     */
    public String getJsonCompleto() {
        try {
            return new String(Files.readAllBytes(Paths.get(ARCHIVO_ASIENTOS)));
        } catch (IOException e) {
            return "{}";
        }
    }

    /**
     * Método para limpiar/resetear todos los asientos (excepto ocupados)
     */
    public void limpiarSelecciones() {
        sala.limpiarSelecciones();
        guardarEstadoCompleto();
    }
}