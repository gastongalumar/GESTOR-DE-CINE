package Clases.login;

import Clases.ListaGenerica;
import Clases.login.usuario.Administrador;
import Clases.login.usuario.Cliente;
import Clases.login.usuario.Usuario;
import Enumeradores.login.EstadoUsuario;
import Enumeradores.login.TipoUsuario;
import Excepciones.AutenticacionException;
import Excepciones.UsuarioException;
import ManejoJSON.FuncionesJSON;
import ManejoJSON.GestorJsonLogin;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.List;

public class GestorUsuarios {
    private static final int MAX_INTENTOS = 5;

    private ListaGenerica<Usuario> usuarios;


    public ListaGenerica<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ListaGenerica<Usuario> usuarios) {
        this.usuarios = usuarios;
    }


    public GestorUsuarios() {
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        try {
            List<Usuario> lista = FuncionesJSON.deserializarUsuarios();
            this.usuarios = new ListaGenerica<>(lista);
            System.out.println("✅ Usuarios cargados: " + usuarios.tamaño());
        } catch (Exception e) {
            System.err.println("❌ Error al cargar usuarios: " + e.getMessage());
            this.usuarios = new ListaGenerica<>();
        }
    }

    private void guardarUsuarios() {
        try {
            FuncionesJSON.serializarUsuarios(usuarios.obtenerTodos());
        } catch (Exception e) {

        }
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

        // ✅ CORREGIDO: SOLO CLIENTES Y ADMINISTRADORES
        Usuario usuarioParaGuardar;
        if (nuevoUsuario.getTipoUsuario() == TipoUsuario.CLIENTE) {
            usuarioParaGuardar = new Cliente(
                    nuevoUsuario.getNombre(),
                    nuevoUsuario.getApellido(),
                    nuevoUsuario.getEmail(),
                    nuevoUsuario.getPassword(),
                    nuevoUsuario.getTelefono()
            );
        } else {
            // ✅ Solo administradores - eliminar la parte de empleados
            usuarioParaGuardar = new Administrador(
                    nuevoUsuario.getNombre(),
                    nuevoUsuario.getApellido(),
                    nuevoUsuario.getEmail(),
                    nuevoUsuario.getPassword(),
                    nuevoUsuario.getTelefono()
            );
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
    /*public void cargarUsuariosPrueba() {
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
    }*/
}