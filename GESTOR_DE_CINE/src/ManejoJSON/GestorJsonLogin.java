package ManejoJSON;

import Clases.login.Usuario;
import Enumeradores.login.EstadoUsuario;
import Enumeradores.login.TipoUsuario;
import Excepciones.AutenticacionException;
import Excepciones.UsuarioException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GestorJsonLogin {
    private static final String ARCHIVO_USUARIOS = "usuarios.json";
    private static final int MAX_INTENTOS = 5;

    // Guardar lista de usuarios
    public static void guardarUsuarios(List<Usuario> usuarios) throws UsuarioException {
        try {
            JSONArray jsonArray = new JSONArray();

            for (Usuario usuario : usuarios) {
                JSONObject jsonUser = usuarioToJSON(usuario);
                jsonArray.put(jsonUser);
            }

            JSONObject root = new JSONObject();
            root.put("usuarios", jsonArray);
            root.put("ultimaActualizacion", LocalDateTime.now().toString());
            root.put("totalUsuarios", usuarios.size());

            JSONUtiles.grabar(root, ARCHIVO_USUARIOS);
            System.out.println("✅ Usuarios guardados: " + usuarios.size());

        } catch (Exception e) {
            System.err.println("❌ Error guardando usuarios: " + e.getMessage());
            throw new UsuarioException("Error al guardar usuarios: " + e.getMessage(),
                    UsuarioException.USUARIO_NO_ENCONTRADO);
        }
    }

    // Cargar lista de usuarios
    public static List<Usuario> cargarUsuarios() throws UsuarioException {
        List<Usuario> usuarios = new ArrayList<>();

        try {
            JSONTokener tokener = JSONUtiles.leer(ARCHIVO_USUARIOS);
            if (tokener == null) {
                System.out.println("📝 Creando archivo de usuarios nuevo...");
                return usuarios;
            }

            JSONObject root = new JSONObject(tokener);
            JSONArray jsonArray = root.getJSONArray("usuarios");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonUser = jsonArray.getJSONObject(i);
                Usuario usuario = jsonToUsuario(jsonUser);
                usuarios.add(usuario);
            }

            System.out.println("✅ " + usuarios.size() + " usuarios cargados");
            return usuarios;

        } catch (Exception e) {
            System.err.println("❌ Error cargando usuarios: " + e.getMessage());
            throw UsuarioException.usuarioNoEncontrado("archivo");
        }
    }

    // Convertir JSON a Usuario
    private static Usuario jsonToUsuario(JSONObject jsonUser) {
        Usuario usuario = null;
        try {
            usuario = new Usuario(
                    jsonUser.getString("nombre"),
                    jsonUser.getString("apellido"),
                    jsonUser.getString("email"),
                    jsonUser.getString("password"),
                    jsonUser.getString("telefono"),
                    TipoUsuario.valueOf(jsonUser.getString("tipoUsuario"))
            );
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // Restaurar estado adicional
        try {
            usuario.setEstado(EstadoUsuario.valueOf(jsonUser.getString("estado")));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            usuario.setIntentosFallidos(jsonUser.getInt("intentosFallidos"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        if (jsonUser.has("fechaUltimoAcceso")) {
            try {
                usuario.setFechaUltimoAcceso(LocalDateTime.parse(jsonUser.getString("fechaUltimoAcceso")));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        return usuario;
    }

    // Convertir Usuario a JSON
    private static JSONObject usuarioToJSON(Usuario usuario) {
        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser.put("nombre", usuario.getNombre());
            jsonUser.put("apellido", usuario.getApellido());
        jsonUser.put("email", usuario.getEmail());
        jsonUser.put("password", usuario.getPassword());
        jsonUser.put("telefono", usuario.getTelefono());
        jsonUser.put("tipoUsuario", usuario.getTipoUsuario().name());
        jsonUser.put("fechaRegistro", usuario.getFechaRegistro().toString());
        jsonUser.put("fechaUltimoAcceso", usuario.getFechaUltimoAcceso().toString());
        jsonUser.put("estado", usuario.getEstado().name());
        jsonUser.put("intentosFallidos", usuario.getIntentosFallidos());
        return jsonUser;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    // Buscar usuario por email
    public static Usuario buscarUsuarioPorEmail(String email) throws UsuarioException {
        List<Usuario> usuarios = cargarUsuarios();

        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equalsIgnoreCase(email)) {
                return usuario;
            }
        }
        throw UsuarioException.usuarioNoEncontrado(email);
    }

    // Agregar nuevo usuario
    public static void agregarUsuario(Usuario nuevoUsuario) throws UsuarioException {
        try {
            nuevoUsuario.validarDatos();
            List<Usuario> usuarios = cargarUsuarios();

            // Verificar si ya existe
            for (Usuario usuario : usuarios) {
                if (usuario.getEmail().equalsIgnoreCase(nuevoUsuario.getEmail())) {
                    throw UsuarioException.usuarioDuplicado(nuevoUsuario.getEmail());
                }
            }

            usuarios.add(nuevoUsuario);
            guardarUsuarios(usuarios);
            System.out.println("✅ Usuario registrado: " + nuevoUsuario.getEmail());

        } catch (UsuarioException e) {
            throw e;
        } catch (Exception e) {
            throw new UsuarioException("Error al agregar usuario: " + e.getMessage(),
                    UsuarioException.DATOS_INVALIDOS);
        }
    }

    // Autenticar usuario
    public static Usuario autenticarUsuario(String email, String password)
            throws AutenticacionException, UsuarioException {

        try {
            Usuario usuario = buscarUsuarioPorEmail(email);

            // Verificar estado
            if (!usuario.isActivo()) {
                if (usuario.getEstado() == EstadoUsuario.BLOQUEADO) {
                    throw AutenticacionException.usuarioBloqueado(email);
                } else {
                    throw AutenticacionException.cuentaInactiva(email);
                }
            }

            // Verificar contraseña
            if (!usuario.getPassword().equals(password)) {
                usuario.incrementarIntentosFallidos();
                actualizarUsuario(usuario);

                if (usuario.getIntentosFallidos() >= MAX_INTENTOS) {
                    throw AutenticacionException.usuarioBloqueado(email);
                }
                throw AutenticacionException.credencialesInvalidas();
            }

            // Login exitoso
            usuario.resetearIntentosFallidos();
            usuario.setFechaUltimoAcceso(LocalDateTime.now());
            actualizarUsuario(usuario);

            return usuario;

        } catch (UsuarioException e) {
            throw AutenticacionException.credencialesInvalidas();
        }
    }

    // Actualizar usuario existente
    public static void actualizarUsuario(Usuario usuarioActualizado) throws UsuarioException {
        List<Usuario> usuarios = cargarUsuarios();

        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getEmail().equalsIgnoreCase(usuarioActualizado.getEmail())) {
                usuarios.set(i, usuarioActualizado);
                guardarUsuarios(usuarios);
                return;
            }
        }
        throw UsuarioException.usuarioNoEncontrado(usuarioActualizado.getEmail());
    }

    // Obtener todos los usuarios (solo admin)
    public static List<Usuario> obtenerTodosUsuarios() throws UsuarioException {
        return cargarUsuarios();
    }

    // Cargar usuarios de prueba (solo si el archivo está vacío)
    public static void cargarUsuariosPrueba() {
        try {
            List<Usuario> usuarios = cargarUsuarios();

            if (usuarios.isEmpty()) {
                System.out.println("🎬 Cargando usuarios de prueba...");

                usuarios.add(new Usuario("Admin", "Sistema", "admin@cine.com",
                        "admin123", "123456789", TipoUsuario.ADMINISTRADOR));
                usuarios.add(new Usuario("Empleado", "Ventas", "empleado@cine.com",
                        "empleado123", "987654321", TipoUsuario.EMPLEADO));
                usuarios.add(new Usuario("Cliente", "Ejemplo", "cliente@cine.com",
                        "cliente123", "555555555", TipoUsuario.CLIENTE));

                guardarUsuarios(usuarios);
                System.out.println("✅ Usuarios de prueba creados");
            }
        } catch (Exception e) {
            System.err.println("❌ Error creando usuarios de prueba: " + e.getMessage());
        }
    }
}