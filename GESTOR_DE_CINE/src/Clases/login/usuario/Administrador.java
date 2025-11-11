package Clases.login.usuario;

import Enumeradores.login.TipoUsuario;

import java.time.LocalDateTime;

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


    public Administrador(String nombre, String apellido, String email, String password, String telefono,
                         String estado, LocalDateTime fechaRegistro, LocalDateTime fechaUltimoAcceso,
                         int intentosFallidos, String nivelAcceso) {
        super(nombre, apellido, email, password, telefono, estado, fechaRegistro,
                fechaUltimoAcceso, intentosFallidos, TipoUsuario.ADMINISTRADOR);
        this.nivelAcceso = nivelAcceso;
    }

    @Override
    public boolean puedeRealizarAccion(String accion) {
        if (!isActivo()) return false;

        switch (accion) {
            case "gestion_usuarios":
            case "gestion_peliculas":
            case "gestion_salas":
            case "ver_reportes":
            case "configurar_sistema":
                return true;
            default:
                return true;
        }
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