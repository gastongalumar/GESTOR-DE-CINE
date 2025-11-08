package Clases.login.usuario;

import Enumeradores.login.TipoUsuario;

public class Cliente extends Usuario {
    private int puntosFidelidad;

    public Cliente(String nombre, String apellido, String email, String password, String telefono) {
        super(nombre, apellido, email, password, telefono, TipoUsuario.CLIENTE);
        this.puntosFidelidad = 0;
    }

    public Cliente() {
        super();
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

    // Método específico del cliente
    public void agregarPuntos(int puntos) {
        this.puntosFidelidad += puntos;
    }

    public boolean canjearPuntos(int puntos) {
        if (puntosFidelidad >= puntos) {
            puntosFidelidad -= puntos;
            return true;
        }
        return false;
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