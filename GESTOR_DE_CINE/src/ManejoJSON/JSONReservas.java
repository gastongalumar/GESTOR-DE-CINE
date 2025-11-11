package ManejoJSON;

import Clases.GestionDePagos.GestorDePagos;
import Clases.GestionDePagos.Reserva;
import Clases.ListaGenerica;
import Clases.SalaCine;
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
    private static final String ARCHIVO_RESERVAS = "reservas.json";

    public static void guardarReserva(Reserva reserva) {
        try {
            ListaGenerica<JSONObject> reservasExistentes = leerTodasLasReservas();

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

    public static boolean eliminarReserva(String numeroTicket) {
        try {
            ListaGenerica<JSONObject> reservas = leerTodasLasReservas();
            ListaGenerica<JSONObject> reservasFiltradas = new ListaGenerica<>();
            boolean encontrada = false;
            JSONObject reservaAEliminar = null;

            for (JSONObject reserva : reservas.obtenerTodos()) {
                try {
                    if (reserva.has("numeroTicket") &&
                            reserva.getString("numeroTicket").equals(numeroTicket)) {
                        reservaAEliminar = reserva;
                        encontrada = true;
                    } else {
                        reservasFiltradas.agregar(reserva);
                    }
                } catch (JSONException e) {
                    reservasFiltradas.agregar(reserva);
                }
            }

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