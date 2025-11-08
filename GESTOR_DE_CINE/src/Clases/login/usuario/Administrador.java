package Clases.login.usuario;

import Enumeradores.login.TipoUsuario;

public class Administrador extends Usuario {
    private String nivelAcceso; // "basico", "avanzado", "super"

    public Administrador(String nombre, String apellido, String email, String password, String telefono) {
        super(nombre, apellido, email, password, telefono, TipoUsuario.ADMINISTRADOR);
        this.nivelAcceso = "avanzado"; // Por defecto
    }

    public Administrador(String nombre, String apellido, String email, String password, String telefono, String nivelAcceso) {
        super(nombre, apellido, email, password, telefono, TipoUsuario.ADMINISTRADOR);
        this.nivelAcceso = nivelAcceso;
    }

    public Administrador() {
        super();
    }

    @Override
    public boolean puedeRealizarAccion(String accion) {
        if (!isActivo()) return false;

        // Administradores pueden hacer todo
        switch (accion) {
            case "gestion_usuarios":
            case "gestion_peliculas":
            case "gestion_salas":
            case "ver_reportes":
            case "configurar_sistema":
                return true;
            default:
                return true; // Por defecto, los admins pueden hacer todo
        }
    }

    // Métodos específicos del administrador
    public boolean puedeGestionarUsuarios() {
        return !nivelAcceso.equals("basico");
    }

    public boolean puedeConfigurarSistema() {
        return nivelAcceso.equals("super");
    }

    // Getters y Setters específicos
    public String getNivelAcceso() {
        return nivelAcceso;
    }

    public void setNivelAcceso(String nivelAcceso) {
        this.nivelAcceso = nivelAcceso;
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "nivelAcceso='" + nivelAcceso + '\'' +
                "} " + super.toString();
    }
}