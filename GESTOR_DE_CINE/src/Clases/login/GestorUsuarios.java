package Clases.login;

import Clases.login.usuario.Administrador;
import Clases.login.usuario.Cliente;
import Clases.login.usuario.Usuario;
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
    private GestorJsonLogin<Usuario> gestorJson;

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

    // ✅ MÉTODO jsonToUsuario
    private Usuario jsonToUsuario(JSONObject jsonUser) {
        try {
            TipoUsuario tipo = TipoUsuario.valueOf(jsonUser.getString("tipoUsuario"));
            Usuario usuario;

            // CREAR LA INSTANCIA CORRECTA SEGÚN EL TIPO
            if (tipo == TipoUsuario.CLIENTE) {
                Cliente cliente = new Cliente(
                        jsonUser.getString("nombre"),
                        jsonUser.getString("apellido"),
                        jsonUser.getString("email"),
                        jsonUser.getString("password"),
                        jsonUser.getString("telefono")
                );
                // CARGAR PUNTOS DE FIDELIDAD SI EXISTEN
                if (jsonUser.has("puntosFidelidad")) {
                    cliente.setPuntosFidelidad(jsonUser.getInt("puntosFidelidad"));
                }
                usuario = cliente;

            } else if (tipo == TipoUsuario.ADMINISTRADOR) {
                Administrador admin = new Administrador(
                        jsonUser.getString("nombre"),
                        jsonUser.getString("apellido"),
                        jsonUser.getString("email"),
                        jsonUser.getString("password"),
                        jsonUser.getString("telefono")
                );
                // CARGAR NIVEL DE ACCESO SI EXISTE
                if (jsonUser.has("nivelAcceso")) {
                    admin.setNivelAcceso(jsonUser.getString("nivelAcceso"));
                }
                usuario = admin;

            } else {
                // Para EMPLEADO - crear clase anónima
                usuario = new Usuario(
                        jsonUser.getString("nombre"),
                        jsonUser.getString("apellido"),
                        jsonUser.getString("email"),
                        jsonUser.getString("password"),
                        jsonUser.getString("telefono"),
                        tipo
                ) {
                    @Override
                    public boolean puedeRealizarAccion(String accion) {
                        if (!isActivo()) return false;

                        switch (getTipoUsuario()) {
                            case EMPLEADO:
                                return accion.equals("vender_entradas") ||
                                        accion.equals("ver_cartelera") ||
                                        accion.equals("atender_clientes");
                            default:
                                return true;
                        }
                    }
                };
            }

            // ✅ SOLO UNA VEZ ESTE BLOQUE - ELIMINAR EL DUPLICADO
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

    // ✅ MÉTODO usuarioToJson ACTUALIZADO
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

            // AGREGAR ATRIBUTOS ESPECÍFICOS DE LAS SUBCLASES
            if (usuario instanceof Cliente cliente) {
                jsonUser.put("puntosFidelidad", cliente.getPuntosFidelidad());
            } else if (usuario instanceof Administrador admin) {
                jsonUser.put("nivelAcceso", admin.getNivelAcceso());
            }

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

        // CREAR LA INSTANCIA CORRECTA SEGÚN EL TIPO
        Usuario usuarioParaGuardar;
        if (nuevoUsuario.getTipoUsuario() == TipoUsuario.CLIENTE) {
            usuarioParaGuardar = new Cliente(
                    nuevoUsuario.getNombre(),
                    nuevoUsuario.getApellido(),
                    nuevoUsuario.getEmail(),
                    nuevoUsuario.getPassword(),
                    nuevoUsuario.getTelefono()
            );
        } else if (nuevoUsuario.getTipoUsuario() == TipoUsuario.ADMINISTRADOR) {
            usuarioParaGuardar = new Administrador(
                    nuevoUsuario.getNombre(),
                    nuevoUsuario.getApellido(),
                    nuevoUsuario.getEmail(),
                    nuevoUsuario.getPassword(),
                    nuevoUsuario.getTelefono()
            );
        } else {
            // Para empleados, crear clase anónima
            usuarioParaGuardar = new Usuario(
                    nuevoUsuario.getNombre(),
                    nuevoUsuario.getApellido(),
                    nuevoUsuario.getEmail(),
                    nuevoUsuario.getPassword(),
                    nuevoUsuario.getTelefono(),
                    nuevoUsuario.getTipoUsuario()
            ) {
                @Override
                public boolean puedeRealizarAccion(String accion) {
                    if (!isActivo()) return false;

                    switch (getTipoUsuario()) {
                        case EMPLEADO:
                            return accion.equals("vender_entradas") ||
                                    accion.equals("ver_cartelera");
                        default:
                            return true;
                    }
                }
            };
        }

        // Asignar fechas y estado
        usuarioParaGuardar.setFechaRegistro(LocalDateTime.now());
        usuarioParaGuardar.setFechaUltimoAcceso(LocalDateTime.now());
        usuarioParaGuardar.setEstado(EstadoUsuario.ACTIVO);
        usuarioParaGuardar.setIntentosFallidos(0);

        // Agregar y guardar
        usuarios.agregar(usuarioParaGuardar);
        guardarUsuarios();

        System.out.println("✅ Nuevo usuario registrado: " + usuarioParaGuardar.getEmail() +
                " - Tipo: " + usuarioParaGuardar.getTipoUsuario().getDescripcion());
    }

    // Mantener usuarios de prueba SOLO si no hay usuarios reales
    public void cargarUsuariosPrueba() {
        if (usuarios.estaVacia()) {
            System.out.println("🎬 Cargando usuarios de prueba...");

            // USAR LAS SUBCLASES CORRECTAS
            usuarios.agregar(new Administrador("Admin", "Sistema", "admin@cine.com",
                    "admin123", "123456789"));

            // Empleado como clase anónima
            usuarios.agregar(new Usuario("Empleado", "Ventas", "empleado@cine.com",
                    "empleado123", "987654321", TipoUsuario.EMPLEADO) {
                @Override
                public boolean puedeRealizarAccion(String accion) {
                    return accion.equals("vender_entradas") ||
                            accion.equals("ver_cartelera") ||
                            accion.equals("atender_clientes");
                }
            });

            guardarUsuarios();
            System.out.println("✅ Usuarios de prueba (admin/empleado) creados");
        } else {
            System.out.println("📊 Usuarios existentes: " + usuarios.tamaño());
        }
    }
}