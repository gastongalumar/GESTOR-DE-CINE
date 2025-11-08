package ManejoJSON;

import Clases.login.usuario.Administrador;
import Clases.login.usuario.Cliente;
import Clases.login.usuario.Usuario;
import Enumeradores.login.TipoUsuario;
import Interfaces.ConversorJson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GestorJsonLogin{
  /*  private final String nombreArchivo;

    public GestorJsonLogin(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }



    @Override
    public JSONObject aJson(Usuario usuario) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("nombre", usuario.getNombre());
            obj.put("apellido", usuario.getApellido());
            obj.put("email", usuario.getEmail());
            obj.put("password", usuario.getPassword());
            obj.put("telefono", usuario.getTelefono());
            obj.put("tipoUsuario", usuario.getTipoUsuario().toString());
            obj.put("activo", usuario.isActivo());

            if (usuario instanceof Administrador) {
                obj.put("nivelAcceso", ((Administrador) usuario).getNivelAcceso());
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al convertir usuario a JSON: " + e.getMessage());
        }

        return obj;
    }

    @Override
    public Usuario desdeJson(JSONObject obj) {
        try {
            String nombre = obj.getString("nombre");
            String apellido = obj.getString("apellido");
            String email = obj.getString("email");
            String password = obj.getString("password");
            String telefono = obj.getString("telefono");

            boolean activo = true;
            try {
                activo = obj.getBoolean("activo");
            } catch (Exception ignored) { }

            TipoUsuario tipo = TipoUsuario.valueOf(obj.getString("tipoUsuario").toUpperCase());

            Usuario usuario;
            if (tipo == TipoUsuario.ADMINISTRADOR) {
                String nivelAcceso = "avanzado";
                try {
                    nivelAcceso = obj.getString("nivelAcceso");
                } catch (Exception ignored) { }

                usuario = new Administrador(nombre, apellido, email, password, telefono, nivelAcceso);
            } else {
                usuario = new Cliente(nombre, apellido, email, password, telefono);
            }

            return usuario;

        } catch (Exception e) {
            System.err.println("❌ Error al convertir JSON a Usuario: " + e.getMessage());
            return null;
        }
    }

    public List<Usuario> cargar() {
        List<Usuario> lista = new ArrayList<>();
        try {
            JSONTokener tokener = JSONUtiles.leer(nombreArchivo);
            if (tokener == null) return lista;

            JSONObject raiz = new JSONObject(tokener);
            JSONArray datos = raiz.getJSONArray("data");

            for (int i = 0; i < datos.length(); i++) {
                JSONObject objetoJson = datos.getJSONObject(i);
                Usuario usuario = desdeJson(objetoJson);
                lista.add(usuario);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }
        return lista;
    }

    public void guardar(List<Usuario> lista) {
        try {
            JSONArray datos = new JSONArray();
            for (Usuario u : lista) {
                datos.put(aJson(u));
            }

            JSONObject raiz = new JSONObject();
            raiz.put("data", datos);
            raiz.put("ultimaActualizacion", LocalDateTime.now().toString());
            raiz.put("totalElementos", lista.size());

            JSONUtiles.grabar(raiz, nombreArchivo);
        } catch (Exception e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }
}
*/}
