package Clases.login.usuario;

import Enumeradores.login.TipoUsuario;

import java.time.LocalDateTime;

public class Cliente extends Usuario {
    private int puntosFidelidad;

    public Cliente(String nombre, String apellido, String email, String password, String telefono) {
        super(nombre, apellido, email, password, telefono, TipoUsuario.CLIENTE);
        this.puntosFidelidad = 0;
    }

    public Cliente() {
        super();
    }


    public Cliente(String nombre, String apellido, String email, String password, String telefono,
                   String estado, LocalDateTime fechaRegistro, LocalDateTime fechaUltimoAcceso,
                   int intentosFallidos, int puntosFidelidad) {
        super(nombre, apellido, email, password, telefono, estado, fechaRegistro,
                fechaUltimoAcceso, intentosFallidos, TipoUsuario.CLIENTE);
        this.puntosFidelidad = puntosFidelidad;
    }
    @Override
    public boolean puedeRealizarAccion(String accion) {
        if (!isActivo()) return false;

        switch (accion) {
            case "comprar_entradas":
            case "ver_cartelera":
            case "ver_perfil":
            case "ver_promociones":
                return true;
            case "canjear_puntos":
                return puntosFidelidad >= 100;
            default:
                return false;
        }
    }


    // Getters y Setters específicos
    public int getPuntosFidelidad() {
        return puntosFidelidad;
    }

    public void setPuntosFidelidad(int puntosFidelidad) {
        this.puntosFidelidad = puntosFidelidad;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "puntosFidelidad=" + puntosFidelidad +
                "} " + super.toString();
    }
}