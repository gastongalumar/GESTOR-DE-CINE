package ManejoJSON;

import Clases.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FuncionesJSON {

    public static void serializarFunciones(List<Funcion> listaFunciones){
        JSONArray jsonFunciones = new JSONArray();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            try {
                if(!listaFunciones.isEmpty()) {
                    for (int i = 0; i < listaFunciones.size(); i++) {
                        JSONObject jsonFuncion = new JSONObject();
                        Funcion funcion = listaFunciones.get(i);
                        jsonFuncion.put("Sala", funcion.getSala().getNombreSala());
                        jsonFuncion.put("Pelicula", funcion.getPelicula().getNombrePelicula());
                        jsonFuncion.put("Fecha y hora", funcion.getHorarioFuncion().format(formato));

                        jsonFunciones.put(jsonFuncion);
                    }

                    JSONUtiles.grabar(jsonFunciones, "funciones.json");
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }



    }



    public static List<Funcion> deserializarFunciones(List<Pelicula> listaPeliculas, List<SalaCine> listaSalas) {
        List<Funcion> listaFunciones = new ArrayList<>();

        try {
            JSONArray jsonFunciones = new JSONArray(JSONUtiles.leer("funciones.json"));
            if (jsonFunciones == null) {
                System.out.println("⚠️ No hay funciones guardadas en el JSON.");
                return listaFunciones;
            }

            for (int i = 0; i < jsonFunciones.length(); i++) {
                JSONObject obj = jsonFunciones.getJSONObject(i);

                String nombreSala = obj.getString("Sala");
                String nombrePelicula = obj.getString("Pelicula");
                String fechaHoraStr = obj.getString("Fecha y hora");

                SalaCine salaEncontrada = buscarSalaPorNombre(listaSalas, nombreSala);
                Pelicula peliculaEncontrada = buscarPeliculaPorNombre(listaPeliculas, nombrePelicula);
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                if (salaEncontrada != null && peliculaEncontrada != null) {
                    LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraStr, formato);
                    Funcion f = new Funcion(salaEncontrada, peliculaEncontrada, fechaHora);
                    listaFunciones.add(f);
                } else {
                    System.out.println("⚠️ No se encontró coincidencia para: " + nombrePelicula + " / " + nombreSala);
                }
            }

            GestorFunciones.setListaFunciones(listaFunciones);

        } catch (Exception e) {
            System.out.println("❌ Error al deserializar funciones: " + e.getMessage());
        }

        return listaFunciones;
    }

    // 🔸 Métodos auxiliares de búsqueda
    private static Pelicula buscarPeliculaPorNombre(List<Pelicula> lista, String nombre) {
        for (Pelicula p : lista) {
            if (p.getNombrePelicula().equalsIgnoreCase(nombre)) {
                return p;
            }
        }
        return null;
    }

    private static SalaCine buscarSalaPorNombre(List<SalaCine> lista, String nombre) {
        for (SalaCine s : lista) {
            if (s.getNombreSala().equalsIgnoreCase(nombre)) {
                return s;
            }
        }
        return null;
    }




    private static final String RUTA_JSON = "peliculas.json";

    public static void serializarPeliculas(List<Pelicula> listaPeliculas){
        JSONArray jsonPeliculas = new JSONArray();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            for (Pelicula p : listaPeliculas) {
                JSONObject jsonPelicula = new JSONObject();
                jsonPelicula.put("Nombre", p.getNombrePelicula());
                jsonPelicula.put("RutaImagen", p.getRutaImagen());
                jsonPelicula.put("FechaEstreno", p.getFechaEstreno().format(formatoFecha));
                jsonPelicula.put("FechaSalida", p.getFechaSalida().format(formatoFecha));

                jsonPeliculas.put(jsonPelicula);
            }

            JSONUtiles.grabar(jsonPeliculas, RUTA_JSON);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static List<Pelicula> deserializarPeliculas(){
        List<Pelicula> listaPeliculas = new ArrayList<>();
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            JSONArray jsonPeliculas = new JSONArray(JSONUtiles.leer(RUTA_JSON));

            for(int i = 0; i < jsonPeliculas.length(); i++){
                JSONObject obj = jsonPeliculas.getJSONObject(i);

                String nombre = obj.getString("Nombre");
                String rutaImagen = obj.getString("RutaImagen");
                LocalDate fechaEstreno = LocalDate.parse(obj.getString("FechaEstreno"), formatoFecha);
                LocalDate fechaSalida = LocalDate.parse(obj.getString("FechaSalida"), formatoFecha);

                Pelicula p = new Pelicula(nombre, rutaImagen, fechaEstreno, fechaSalida);
                listaPeliculas.add(p);
            }

            GestorPeliculas.setListaPeliculas(listaPeliculas);
        } catch (Exception e) {
            System.out.println("❌ Error al deserializar películas: " + e.getMessage());
        }

        return listaPeliculas;
    }

    public static void serializarPagos(List<Pago> listaPagos) {
        JSONArray jsonPagos = new JSONArray();

        try {
            for (Pago pago : listaPagos) {
                JSONObject jsonPago = new JSONObject();
                jsonPago.put("ID", pago.getIdPago());
                jsonPago.put("Monto", pago.getMonto());
                jsonPago.put("Fecha", pago.getFechaPago().toString());
                jsonPago.put("Metodo", pago.getMetodoPago());

                jsonPagos.put(jsonPago);
            }

            JSONUtiles.grabar(jsonPagos, "pagos.json");

        } catch (JSONException e) {
            e.printStackTrace();
    }
}
