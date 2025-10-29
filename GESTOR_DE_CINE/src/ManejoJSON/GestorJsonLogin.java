package ManejoJSON;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GestorJsonLogin<T> {
    private final String archivo;
    private final Function<JSONObject, T> fromJsonConverter;
    private final Function<T, JSONObject> toJsonConverter;

    public GestorJsonLogin(String archivo,
                           Function<JSONObject, T> fromJsonConverter,
                           Function<T, JSONObject> toJsonConverter) {
        this.archivo = archivo;
        this.fromJsonConverter = fromJsonConverter;
        this.toJsonConverter = toJsonConverter;
    }

    // Cargar datos desde archivo
    public List<T> cargar() {
        List<T> elementos = new ArrayList<>();

        try {
            JSONTokener tokener = JSONUtiles.leer(archivo);
            if (tokener == null) {
                System.out.println("📝 Creando archivo nuevo: " + archivo);
                return elementos;
            }

            JSONObject root = new JSONObject(tokener);
            JSONArray jsonArray = root.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                T elemento = fromJsonConverter.apply(jsonObj);
                elementos.add(elemento);
            }

            System.out.println("✅ " + elementos.size() + " elementos cargados de " + archivo);
            return elementos;

        } catch (Exception e) {
            System.err.println("❌ Error cargando de " + archivo + ": " + e.getMessage());
            return elementos;
        }
    }

    // Guardar datos a archivo
    public void guardar(List<T> elementos) {
        try {
            JSONArray jsonArray = new JSONArray();

            for (T elemento : elementos) {
                JSONObject jsonObj = toJsonConverter.apply(elemento);
                jsonArray.put(jsonObj);
            }

            JSONObject root = new JSONObject();
            root.put("data", jsonArray);
            root.put("ultimaActualizacion", LocalDateTime.now().toString());
            root.put("totalElementos", elementos.size());

            JSONUtiles.grabar(root, archivo);
            System.out.println("✅ Datos guardados en " + archivo + ": " + elementos.size() + " elementos");

        } catch (Exception e) {
            System.err.println("❌ Error guardando en " + archivo + ": " + e.getMessage());
        }
    }
}