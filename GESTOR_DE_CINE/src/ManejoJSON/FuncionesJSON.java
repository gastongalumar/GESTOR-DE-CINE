package ManejoJSON;

import Clases.Funcion;
import Clases.GestorFunciones;
import Clases.Pelicula;
import Clases.SalaCine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FuncionesJSON {

    public static void serializarFunciones(List<Funcion> listaFunciones){
        JSONArray jsonFunciones = new JSONArray();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            try {
                for(int i = 0; i < listaFunciones.size(); i++) {
                    JSONObject jsonFuncion = new JSONObject();
                    Funcion funcion = listaFunciones.get(i);
                    jsonFuncion.put("Sala", funcion.getSala().getNombreSala());
                    jsonFuncion.put("Pelicula", funcion.getPelicula().getNombrePelicula());
                    jsonFuncion.put("Fecha y hora", funcion.getHorarioFuncion().format(formato));

                    jsonFunciones.put(jsonFuncion);
                }

                JSONUtiles.grabar(jsonFunciones, "pruebafunciones.json");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }



    }



    public static List<Funcion> deserializarFunciones(List<Pelicula> listaPeliculas, List<SalaCine> listaSalas) {
        List<Funcion> listaFunciones = new ArrayList<>();

        try {
            JSONArray jsonFunciones = new JSONArray(JSONUtiles.leer("pruebafunciones.json"));
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
}
