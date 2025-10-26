package ManejoJSON;

import Clases.Funcion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.format.DateTimeFormatter;
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
}
