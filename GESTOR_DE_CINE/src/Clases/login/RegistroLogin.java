package Clases.login;

import java.time.LocalDateTime;

public class RegistroLogin {
    private String usuario;
    private String tipoUsuario;
    private LocalDateTime fechaHora;

    public RegistroLogin(String usuario, String tipoUsuario, LocalDateTime fechaHora) {
        this.usuario = usuario;
        this.tipoUsuario = tipoUsuario;
        this.fechaHora = fechaHora;
    }

    public String getUsuario() { return usuario; }
    public String getTipoUsuario() { return tipoUsuario; }
    public LocalDateTime getFechaHora() { return fechaHora; }
}