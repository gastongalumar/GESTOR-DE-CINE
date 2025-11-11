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
        } catch (Exception e) {
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
        nuevoUsuario.validarDatos();

        if (existeUsuario(nuevoUsuario.getEmail())) {
            throw UsuarioException.usuarioDuplicado(nuevoUsuario.getEmail());
        }

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
            usuarioParaGuardar = new Administrador(
                    nuevoUsuario.getNombre(),
                    nuevoUsuario.getApellido(),
                    nuevoUsuario.getEmail(),
                    nuevoUsuario.getPassword(),
                    nuevoUsuario.getTelefono()
            );
        }

        usuarioParaGuardar.setFechaRegistro(LocalDateTime.now());
        usuarioParaGuardar.setFechaUltimoAcceso(LocalDateTime.now());
        usuarioParaGuardar.setEstado(EstadoUsuario.ACTIVO);
        usuarioParaGuardar.setIntentosFallidos(0);

        usuarios.agregar(usuarioParaGuardar);
        guardarUsuarios();

    }
}