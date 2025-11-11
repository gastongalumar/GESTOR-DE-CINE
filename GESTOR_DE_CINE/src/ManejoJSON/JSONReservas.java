package ManejoJSON;

import Clases.GestionDePagos.GestorDePagos;
import Clases.GestionDePagos.Reserva;
import Clases.Utilidades.ListaGenerica;
import Clases.GestionSelectorAsientos.SalaCine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JSONReservas {

    // Archivo donde se guardan todas las reservas
    private static final String ARCHIVO_RESERVAS = "reservas.json";

    // Guarda una nueva reserva en el archivo JSON
    public static void guardarReserva(Reserva reserva) {
        try {
            // Leer reservas existentes
            ListaGenerica<JSONObject> reservasExistentes = leerTodasLasReservas();

            // Convertir reserva a JSON
            JSONObject reservaJson = new JSONObject();
            reservaJson.put("clienteEmail", reserva.getClienteEmail());
            reservaJson.put("clienteNombre", reserva.getClienteNombre());
            reservaJson.put("numeroTicket", reserva.getNumeroTicket());
            reservaJson.put("codigoOR", reserva.getCodigoOR());
            reservaJson.put("metodoPago", reserva.getMetodoPago());
            reservaJson.put("monto", reserva.getMonto());
            reservaJson.put("nombrePelicula", reserva.getNombrePelicula());
            reservaJson.put("horarioFuncion", reserva.getHorarioFuncion());
            reservaJson.put("salaNombre", reserva.getSalaNombre());
            reservaJson.put("fechaEmision", reserva.getFechaEmision().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            reservaJson.put("asientosSeleccionados", new JSONArray(reserva.getAsientosSeleccionados()));

            reservasExistentes.agregar(reservaJson);
            guardarEnArchivo(reservasExistentes);

            System.out.println("Reserva guardada para: " + reserva.getClienteEmail());

        } catch (Exception e) {
            System.err.println("Error al guardar reserva: " + e.getMessage());
        }
    }

    // Obtiene todas las reservas de un cliente específico
    public static ListaGenerica<JSONObject> obtenerReservasPorCliente(String clienteEmail) {
        ListaGenerica<JSONObject> todasLasReservas = leerTodasLasReservas();
        ListaGenerica<JSONObject> reservasFiltradas = new ListaGenerica<>();

        for (JSONObject reserva : todasLasReservas.obtenerTodos()) {
            try {
                if (reserva.has("clienteEmail") &&
                        reserva.getString("clienteEmail").equals(clienteEmail)) {
                    reservasFiltradas.agregar(reserva);
                }
            } catch (JSONException e) {
                System.err.println("Error al leer reserva: " + e.getMessage());
            }
        }

        return reservasFiltradas;
    }

    // Lee todas las reservas del archivo JSON
    public static ListaGenerica<JSONObject> leerTodasLasReservas() {
        ListaGenerica<JSONObject> reservas = new ListaGenerica<>();

        try {
            if (!Files.exists(Paths.get(ARCHIVO_RESERVAS))) {
                guardarEnArchivo(reservas);
                return reservas;
            }

            String contenido = new String(Files.readAllBytes(Paths.get(ARCHIVO_RESERVAS)));
            if (contenido.trim().isEmpty()) {
                return reservas;
            }

            JSONArray jsonArray = new JSONArray(contenido);
            for (int i = 0; i < jsonArray.length(); i++) {
                reservas.agregar(jsonArray.getJSONObject(i));
            }

        } catch (Exception e) {
            System.err.println("Error al leer reservas: " + e.getMessage());
        }

        return reservas;
    }

    // Guarda la lista de reservas en el archivo JSON
    private static void guardarEnArchivo(ListaGenerica<JSONObject> reservas) {
        try (FileWriter file = new FileWriter(ARCHIVO_RESERVAS)) {
            JSONArray jsonArray = new JSONArray();
            for (JSONObject reserva : reservas.obtenerTodos()) {
                jsonArray.put(reserva);
            }
            file.write(jsonArray.toString(4));
            file.flush();
        } catch (Exception e) {
            System.err.println("Error al guardar archivo: " + e.getMessage());
        }
    }



    // Elimina una reserva por número de ticket, verificando si la función ya pasó o está en curso
    public static boolean eliminarReserva(String numeroTicket) {
        try {
            ListaGenerica<JSONObject> reservas = leerTodasLasReservas();
            ListaGenerica<JSONObject> reservasFiltradas = new ListaGenerica<>();
            boolean encontrada = false;
            JSONObject reservaAEliminar = null;

            // Buscar la reserva por número de ticket
            for (JSONObject reserva : reservas.obtenerTodos()) {
                try {
                    if (reserva.has("numeroTicket") &&
                            reserva.getString("numeroTicket").equals(numeroTicket)) {
                        reservaAEliminar = reserva;
                        encontrada = true;

                        // Verificar si la función ya pasó o está en curso
                        if (esFuncionPasadaOEnCurso(reserva)) {
                            System.out.println("No se puede eliminar la reserva: la función ya pasó o está en curso");
                            return false;
                        }
                    } else {
                        reservasFiltradas.agregar(reserva);
                    }
                } catch (JSONException e) {
                    reservasFiltradas.agregar(reserva);
                }
            }

            // Si se encontró y puede eliminarse, proceder con la eliminación
            if (encontrada && reservaAEliminar != null) {
                liberarAsientosReserva(reservaAEliminar);
                guardarEnArchivo(reservasFiltradas);
                System.out.println("Reserva eliminada: " + numeroTicket);
                return true;
            } else {
                System.out.println("Reserva no encontrada: " + numeroTicket);
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error al eliminar reserva: " + e.getMessage());
            return false;
        }
    }

    // Verifica si la función de una reserva ya pasó o está en curso
    private static boolean esFuncionPasadaOEnCurso(JSONObject reserva) {
        try {
            // Obtener el horario de la función desde la reserva
            String horarioFuncionStr = reserva.getString("horarioFuncion");

            // Formateador para parsear la fecha/hora (ajustar según el formato usado en tus reservas)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime horarioFuncion = LocalDateTime.parse(horarioFuncionStr, formatter);

            // Obtener fecha/hora actual
            LocalDateTime ahora = LocalDateTime.now();

            // Verificar si la función ya pasó (es anterior a ahora)
            // O si está en curso (considerando margen de 30 minutos antes y 3 horas después)
            LocalDateTime inicioMargen = horarioFuncion.minusMinutes(30); // 30 min antes de la función
            LocalDateTime finMargen = horarioFuncion.plusHours(3); // 3 horas después de la función (duración máxima)

            // La función está en curso si estamos dentro del margen
            boolean enCurso = !ahora.isBefore(inicioMargen) && !ahora.isAfter(finMargen);

            // La función ya pasó si estamos después del fin del margen
            boolean yaPaso = ahora.isAfter(finMargen);

            if (enCurso) {
                System.out.println("La función está en curso o por comenzar: " + horarioFuncionStr);
            }
            if (yaPaso) {
                System.out.println("La función ya pasó: " + horarioFuncionStr);
            }

            return enCurso || yaPaso;

        } catch (Exception e) {
            System.err.println("Error al verificar horario de función: " + e.getMessage());
            // En caso de error, asumir que no se puede eliminar por seguridad
            return true;
        }
    }

    // Libera los asientos ocupados por una reserva que se está eliminando
    private static void liberarAsientosReserva(JSONObject reserva) {
        try {
            String nombrePelicula = reserva.getString("nombrePelicula");
            String horarioFuncion = reserva.getString("horarioFuncion");
            String salaNombre = reserva.getString("salaNombre");
            String numeroTicket = reserva.getString("numeroTicket");
            JSONArray asientosSeleccionados = reserva.getJSONArray("asientosSeleccionados");

            GestorDePagos.eliminarTicketTemporal(numeroTicket);


            // Buscar archivo de asientos
            String nombreArchivo = buscarArchivoAsientos(nombrePelicula, horarioFuncion);


            String archivoAsientos = "JSONasientos/" + nombreArchivo;


            // Convertir JSONArray a List<String>
            List<String> asientosList = new ArrayList<>();
            for (int i = 0; i < asientosSeleccionados.length(); i++) {
                asientosList.add(asientosSeleccionados.getString(i));
            }

            // Liberar asientos
            SalaCine sala = new SalaCine(salaNombre, 9, 14);
            GestorJsonAsientos gestorAsientos = new GestorJsonAsientos(sala, archivoAsientos);

            boolean exito = gestorAsientos.liberarAsientos(asientosList);

            if (exito) {
                System.out.println("Asientos liberados exitosamente");
            } else {
                System.err.println("Error al liberar asientos");
            }

        } catch (Exception e) {
            System.err.println("Error en liberarAsientosReserva: " + e.getMessage());
        }
    }

    // Busca el archivo de asientos correspondiente a una película y horario
    private static String buscarArchivoAsientos(String nombrePelicula, String horarioFuncion) {
        try {
            // Buscar en la carpeta JSONasientos
            File directorio = new File("JSONasientos");

            // Si la carpeta no existe, mostrar error
            if (!directorio.exists()) {
                System.err.println(" Carpeta JSONasientos no encontrada");
                return null;
            }

            String nombreBusqueda = nombrePelicula.replaceAll("\\s+", "_");

            System.out.println("Buscando archivo EXACTO:");
            System.out.println("   Película: " + nombreBusqueda);
            System.out.println("   Horario reserva: " + horarioFuncion);

            // SOLO buscar el archivo EXACTO usando el horario
            try {
                // Convertir el horario de la reserva al formato del archivo
                DateTimeFormatter formatterEntrada = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                LocalDateTime horario = LocalDateTime.parse(horarioFuncion, formatterEntrada);
                DateTimeFormatter fmtGuardado = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
                String horarioFormateado = horario.format(fmtGuardado);

                String nombreArchivoExacto = String.format("Asientos_%s_%s.json",
                        nombreBusqueda, horarioFormateado);

                System.out.println("Archivo exacto esperado: " + nombreArchivoExacto);

                File archivoExacto = new File(directorio, nombreArchivoExacto);
                if (archivoExacto.exists()) {
                    System.out.println("Archivo exacto encontrado: " + nombreArchivoExacto);
                    return nombreArchivoExacto;
                } else {
                    System.err.println("Archivo exacto NO encontrado: " + nombreArchivoExacto);

                    // Listar archivos disponibles para debug
                    String[] archivos = directorio.list((dir, name) ->
                            name.startsWith("Asientos_") && name.endsWith(".json"));
                    if (archivos != null && archivos.length > 0) {
                        System.out.println(" Archivos disponibles en JSONasientos:");
                        for (String f : archivos) {
                            System.out.println("   - " + f);
                        }
                    }
                    return null;
                }
            } catch (Exception e) {
                System.err.println("Error buscando archivo exacto: " + e.getMessage());
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error buscando archivo: " + e.getMessage());
            return null;
        }
    }


}