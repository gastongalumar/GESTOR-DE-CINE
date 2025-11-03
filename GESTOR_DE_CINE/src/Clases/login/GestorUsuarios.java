package Clases.login;

import Clases.ListaGenerica;
import Enumeradores.login.EstadoUsuario;
import Enumeradores.login.TipoUsuario;
import Excepciones.AutenticacionException;
import Excepciones.UsuarioException;
import ManejoJSON.GestorJsonLogin;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.List;

public class GestorUsuarios {
    private static final String ARCHIVO_USUARIOS = "usuarios.json";
    private static final int MAX_INTENTOS = 5;
    private ListaGenerica<Usuario> usuarios;
    private GestorJsonLogin gestorJson;


    //CONSTRUCTOR
    public GestorUsuarios() {
        this.gestorJson = new GestorJsonLogin<>(
                ARCHIVO_USUARIOS,
                this::jsonToUsuario,
                this::usuarioToJson
        );
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        List<Usuario> usuariosCargados = gestorJson.cargar();
        this.usuarios = new ListaGenerica<>(usuariosCargados);
    }

    private void guardarUsuarios() {
        gestorJson.guardar(usuarios.obtenerTodos());
    }


    public Usuario autenticarUsuario(String email, String password)
            throws AutenticacionException, UsuarioException {

        Usuario usuario = usuarios.buscar(u -> u.getEmail().equalsIgnoreCase(email));

        if (usuario == null) {
            throw AutenticacionException.credencialesInvalidas();
        }

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
    }

    public void actualizarUsuario(Usuario usuarioActualizado) {
        usuarios.actualizarSi(
                u -> u.getEmail().equalsIgnoreCase(usuarioActualizado.getEmail()),
                usuarioActualizado
        );
        guardarUsuarios();
    }

    public Usuario buscarPorEmail(String email) throws UsuarioException {
        Usuario usuario = usuarios.buscar(u -> u.getEmail().equalsIgnoreCase(email));
        if (usuario == null) {
            throw UsuarioException.usuarioNoEncontrado(email);
        }
        return usuario;
    }

    public List<Usuario> obtenerTodosUsuarios() {
        return usuarios.obtenerTodos();
    }

    // Convertidores JSON (específicos para Usuario)
    private Usuario jsonToUsuario(JSONObject jsonUser) {
        try {
            Usuario usuario = new Usuario(
                    jsonUser.getString("nombre"),
                    jsonUser.getString("apellido"),
                    jsonUser.getString("email"),
                    jsonUser.getString("password"),
                    jsonUser.getString("telefono"),
                    TipoUsuario.valueOf(jsonUser.getString("tipoUsuario"))
            );

            usuario.setEstado(EstadoUsuario.valueOf(jsonUser.getString("estado")));
            usuario.setIntentosFallidos(jsonUser.getInt("intentosFallidos"));

            if (jsonUser.has("fechaUltimoAcceso")) {
                usuario.setFechaUltimoAcceso(LocalDateTime.parse(jsonUser.getString("fechaUltimoAcceso")));
            }

            return usuario;
        } catch (JSONException e) {
            throw new RuntimeException("Error convirtiendo JSON a Usuario", e);
        }
    }

    private JSONObject usuarioToJson(Usuario usuario) {
        try {
            JSONObject jsonUser = new JSONObject();
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
            throw new RuntimeException("Error convirtiendo Usuario a JSON", e);
        }
    }

    public boolean existeUsuario(String email) {
        return usuarios.existe(u -> u.getEmail().equalsIgnoreCase(email));
    }

    public void registrarUsuario(Usuario nuevoUsuario) throws UsuarioException {
        // Validar datos del usuario
        nuevoUsuario.validarDatos();

        // Verificar si el email ya está registrado
        if (existeUsuario(nuevoUsuario.getEmail())) {
            throw UsuarioException.usuarioDuplicado(nuevoUsuario.getEmail());
        }

        // Asignar fecha de registro y estado inicial
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());
        nuevoUsuario.setFechaUltimoAcceso(LocalDateTime.now());
        nuevoUsuario.setEstado(EstadoUsuario.ACTIVO);
        nuevoUsuario.setIntentosFallidos(0);

        // Agregar y guardar
        usuarios.agregar(nuevoUsuario);
        guardarUsuarios();

        System.out.println("✅ Nuevo usuario registrado: " + nuevoUsuario.getEmail() +
                " - Tipo: " + nuevoUsuario.getTipoUsuario().getDescripcion());
    }

    // Método específico para registro de clientes (público)
    public void registrarCliente(String nombre, String apellido, String email,
                                 String password, String telefono) throws UsuarioException {
        Usuario nuevoCliente = new Usuario(
                nombre, apellido, email, password, telefono, TipoUsuario.CLIENTE
        );
        registrarUsuario(nuevoCliente);
    }


    // Mantener usuarios de prueba SOLO si no hay usuarios reales
    public void cargarUsuariosPrueba() {
        if (usuarios.estaVacia()) {
            System.out.println("🎬 Cargando usuarios de prueba...");

            // Solo cargar admin y empleado de prueba, clientes se registran
            usuarios.agregar(new Usuario("Admin", "Sistema", "admin@cine.com",
                    "admin123", "123456789", TipoUsuario.ADMINISTRADOR));
            usuarios.agregar(new Usuario("Empleado", "Ventas", "empleado@cine.com",
                    "empleado123", "987654321", TipoUsuario.EMPLEADO));

            guardarUsuarios();
            System.out.println("✅ Usuarios de prueba (admin/empleado) creados");
        } else {
            System.out.println("📊 Usuarios existentes: " + usuarios.tamaño());
        }
    }
}