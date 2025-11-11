package ManejoJSON;

import Clases.Funcion;
import Clases.GestorFunciones;
import Clases.SalaCine;
import Interfaces.ConversorJson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.*;
import java.nio.file.Files;

import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GestorJsonAsientos implements ConversorJson {
    private static final String CARPETA_JSON = "JSONasientos/";
    private static final String ARCHIVO_POR_DEFECTO = CARPETA_JSON + "Asientos.json";
    private final SalaCine sala;
    private final String archivoAsientos;

    public GestorJsonAsientos(SalaCine sala) {
        this(sala, ARCHIVO_POR_DEFECTO);
    }

    // Constructor que permite especificar el archivo JSON
    public GestorJsonAsientos(SalaCine sala, String archivoAsientos) {
        this.sala = sala;


        if (archivoAsientos != null && !archivoAsientos.isEmpty()) {
            if (archivoAsientos.startsWith(CARPETA_JSON)) {
                this.archivoAsientos = archivoAsientos;
            } else {
                this.archivoAsientos = CARPETA_JSON + archivoAsientos;
            }
        } else {
            this.archivoAsientos = ARCHIVO_POR_DEFECTO;
        }

        crearCarpetaJSON();
        inicializarArchivo();
    }

    /**
     * Crea la carpeta JSONasientos si no existe
     */
    public void crearCarpetaJSON() {
        File carpeta = new File(CARPETA_JSON);
        if (!carpeta.exists()) {
            if (carpeta.mkdirs()) {
                System.out.println("📁 Carpeta creada: " + CARPETA_JSON);
            } else {
                System.err.println("❌ No se pudo crear la carpeta: " + CARPETA_JSON);
            }
        }
    }

    public void inicializarArchivo() {
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
                        case "LIBRE":
                            libresJson++;
                            break;
                        case "OCUPADO":
                            ocupadosJson++;
                            break;
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

    /**
     * Limpia selecciones y guarda
     */
    public void limpiarSelecciones() {
        sala.limpiarSelecciones();
        guardarEstadoCompleto();
    }


    public static void renombrarArchivoAsientos(Funcion funcionVieja, Funcion funcionNueva) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

        String nombreAnt = funcionVieja.getPelicula().getNombrePelicula().replaceAll("\\s+", "_");
        String horarioAnt = funcionVieja.getHorarioFuncion().format(fmt);
        String nombreNuev = funcionNueva.getPelicula().getNombrePelicula().replaceAll("\\s+", "_");
        String horarioNuev = funcionNueva.getHorarioFuncion().format(fmt);

        File fileViejo = new File(CARPETA_JSON, String.format("Asientos_%s_%s.json", nombreAnt, horarioAnt));
        File fileNuevo = new File(CARPETA_JSON, String.format("Asientos_%s_%s.json", nombreNuev, horarioNuev));

        if (fileViejo.exists()) {
            boolean renombrado = fileViejo.renameTo(fileNuevo);
            if (renombrado) {
                System.out.println("✅ Archivo renombrado correctamente: " + fileNuevo.getName());
            } else {
                System.out.println("⚠️ No se pudo renombrar el archivo de asientos");
            }
        } else {
            System.out.println("⚠️ No se encontró el archivo de asientos original: " + fileViejo.getName());
        }
    }


    public static void copiarArchivosAsientos(String nombreAnterior, String nuevoNombre, GestorFunciones gestorFunciones) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
            File carpeta = new File(CARPETA_JSON);

            boolean algunaCopia = false;

            // 🔸 PRIMERA PASADA: copiar y eliminar archivos
            for (Funcion funcion : gestorFunciones.getListaFunciones().getElementos()) {
                String nombreFuncion = funcion.getPelicula().getNombrePelicula();
                String horarioFuncion = funcion.getHorarioFuncion().format(fmt);

                if (nombreFuncion.equalsIgnoreCase(nombreAnterior)) {
                    algunaCopia = true;

                    String nombreAnt = nombreAnterior.replaceAll("\\s+", "_");
                    String nombreNuev = nuevoNombre.replaceAll("\\s+", "_");

                    File fileViejo = new File(CARPETA_JSON, String.format("Asientos_%s_%s.json", nombreAnt, horarioFuncion));
                    File fileNuevo = new File(CARPETA_JSON, String.format("Asientos_%s_%s.json", nombreNuev, horarioFuncion));

                    if (fileViejo.exists()) {
                        try {
                            String contenido = new String(Files.readAllBytes(fileViejo.toPath()));
                            Files.write(fileNuevo.toPath(), contenido.getBytes(),
                                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                            boolean eliminado = fileViejo.delete();
                            if (!eliminado) {
                                System.gc();
                                Thread.sleep(100);
                                eliminado = fileViejo.delete();
                            }


                        } catch (Exception e) {

                        }
                    }
                }
            }

            if (algunaCopia) {
                for (Funcion f : gestorFunciones.getListaFunciones().getElementos()) {
                    if (f.getPelicula().getNombrePelicula().equalsIgnoreCase(nombreAnterior)) {
                        f.getPelicula().setNombrePelicula(nuevoNombre);
                    }
                }

                FuncionesJSON.serializarFunciones(gestorFunciones.getListaFunciones().getElementos());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private int[] etiquetaACoordenadas(String etiqueta) {
        if (etiqueta == null || etiqueta.length() < 2) {
            return new int[]{-1, -1};
        }

        try {
            // Letra = Fila, Número = Columna VISIBLE
            char letraFila = etiqueta.charAt(0);
            String numeroStr = etiqueta.substring(1);
            int columnaVisible = Integer.parseInt(numeroStr);

            // Convertir letra a índice de fila
            int fila = letraFila - 'A';

            // ✅ MAPEO DE COLUMNAS VISIBLES A COLUMNAS REALES
            // Columnas visibles: 1-14
            // Columnas reales en JSON: 0-2, 4-11, 13-15 (16 columnas total con pasillos)
            int columnaReal = -1;

            if (columnaVisible >= 1 && columnaVisible <= 3) {
                // Bloque izquierdo: 1->0, 2->1, 3->2
                columnaReal = columnaVisible - 1;
            } else if (columnaVisible >= 4 && columnaVisible <= 11) {
                // Bloque central: 4->4, 5->5, ..., 11->11
                columnaReal = columnaVisible;
            } else if (columnaVisible >= 12 && columnaVisible <= 14) {
                // Bloque derecho: 12->13, 13->14, 14->15
                columnaReal = columnaVisible + 1;
            }

            System.out.println("🔍 Etiqueta '" + etiqueta + "' -> Fila:" + fila +
                    ", ColVisible:" + columnaVisible + ", ColReal:" + columnaReal);

            if (columnaReal == -1) {
                System.err.println("❌ Columna visible inválida: " + columnaVisible);
                return new int[]{-1, -1};
            }

            return new int[]{fila, columnaReal};

        } catch (NumberFormatException e) {
            System.err.println("❌ Error parseando etiqueta: " + etiqueta);
            return new int[]{-1, -1};
        }
    }

    public boolean liberarAsientos(List<String> asientosALiberar) {
        try {
            System.out.println("🔄 Liberando " + asientosALiberar.size() + " asientos: " + asientosALiberar);
            System.out.println("📁 Archivo: " + archivoAsientos);

            File archivo = new File(archivoAsientos);
            if (!archivo.exists()) {
                System.err.println("❌ Archivo no existe: " + archivoAsientos);
                return false;
            }

            // Leer archivo JSON
            JSONObject estadoSala = JSONUtiles.leerObject(archivoAsientos);
            if (estadoSala == null) {
                System.err.println("❌ No se pudo leer el archivo JSON");
                return false;
            }

            JSONArray matrizAsientos = estadoSala.getJSONArray("matrizAsientos");
            int liberados = 0;

            // Para cada etiqueta a liberar
            for (String etiqueta : asientosALiberar) {
                String etiquetaNormalizada = etiqueta.trim().toUpperCase();
                System.out.println("🎯 Procesando: '" + etiquetaNormalizada + "'");

                // Convertir etiqueta a coordenadas
                int[] coordenadas = etiquetaACoordenadas(etiquetaNormalizada);
                if (coordenadas[0] == -1 || coordenadas[1] == -1) {
                    System.err.println("❌ No se pudo convertir etiqueta: " + etiquetaNormalizada);
                    continue;
                }

                int fila = coordenadas[0];
                int columna = coordenadas[1];

                System.out.println("📍 Coordenadas: Fila " + fila + ", Columna " + columna);

                // Verificar que las coordenadas estén dentro de los límites
                if (fila >= 0 && fila < matrizAsientos.length()) {
                    JSONArray filaArray = matrizAsientos.getJSONArray(fila);
                    if (columna >= 0 && columna < filaArray.length()) {
                        JSONObject asientoJson = filaArray.getJSONObject(columna);
                        String estadoActual = asientoJson.getString("estado");
                        String etiquetaEnJson = asientoJson.getString("etiqueta");

                        System.out.println("📋 En JSON: " + etiquetaEnJson + " -> Estado: " + estadoActual);

                        if ("OCUPADO".equals(estadoActual)) {
                            asientoJson.put("estado", "LIBRE");
                            liberados++;
                            System.out.println("✅ Asiento liberado: " + etiquetaNormalizada);
                        } else {
                            System.out.println("⚠️ Asiento " + etiquetaNormalizada + " no está ocupado (estado: " + estadoActual + ")");
                        }
                    } else {
                        System.err.println("❌ Columna fuera de rango: " + columna + " para etiqueta: " + etiquetaNormalizada);
                    }
                } else {
                    System.err.println("❌ Fila fuera de rango: " + fila + " para etiqueta: " + etiquetaNormalizada);
                }
                System.out.println("---");
            }

            if (liberados > 0) {
                // Guardar cambios
                estadoSala.put("fechaActualizacion", LocalDateTime.now().toString());
                JSONUtiles.grabar(estadoSala, archivoAsientos);
                System.out.println("💾 " + liberados + " asientos liberados exitosamente");
                return true;
            } else {
                System.out.println("⚠️ No se liberó ningún asiento");
                return false;
            }

        } catch (Exception e) {
            System.err.println("❌ Error liberando asientos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}