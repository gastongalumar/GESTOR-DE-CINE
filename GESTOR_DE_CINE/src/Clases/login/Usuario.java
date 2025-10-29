package Clases.login;

import Enumeradores.login.TipoUsuario;
import Enumeradores.login.EstadoUsuario;

import Excepciones.UsuarioException;


import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class Usuario {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;
    private TipoUsuario tipoUsuario;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaUltimoAcceso;
    private EstadoUsuario estado;
    private int intentosFallidos;

    // Constructor principal
    public Usuario(String nombre, String apellido, String email, String password,
                   String telefono, TipoUsuario tipoUsuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.tipoUsuario = tipoUsuario;
        this.fechaRegistro = LocalDateTime.now();
        this.fechaUltimoAcceso = LocalDateTime.now();
        this.estado = EstadoUsuario.ACTIVO;
        this.intentosFallidos = 0;
    }

    public Usuario() {
    }

    // Métodos de validación
    public static boolean validarEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    public static boolean validarTelefono(String telefono) {
        return telefono != null && telefono.matches("\\d{7,15}");
    }

    public static boolean validarPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public void validarDatos() throws UsuarioException {
        UsuarioException UsuarioException = null;
        if (nombre == null || nombre.trim().isEmpty()) {
            throw UsuarioException.datosInvalidos("nombre");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw UsuarioException.datosInvalidos("apellido");
        }
        if (!validarEmail(email)) {
            throw UsuarioException.datosInvalidos("email");
        }
        if (!validarPassword(password)) {
            throw UsuarioException.datosInvalidos("password");
        }
        if (!validarTelefono(telefono)) {
            throw UsuarioException.datosInvalidos("telefono");
        }
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }

    public LocalDateTime getFechaUltimoAcceso() { return fechaUltimoAcceso; }
    public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) {
        this.fechaUltimoAcceso = fechaUltimoAcceso;
    }

    public EstadoUsuario getEstado() { return estado; }
    public void setEstado(EstadoUsuario estado) { this.estado = estado; }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }
    public void incrementarIntentosFallidos() {
        this.intentosFallidos++;
        if (this.intentosFallidos >= 5) {
            this.estado = EstadoUsuario.BLOQUEADO;
        }
    }
    public void resetearIntentosFallidos() {
        this.intentosFallidos = 0;
    }

    public boolean isActivo() {
        return estado == EstadoUsuario.ACTIVO;
    }

    public boolean puedeRealizarAccion(String accion) {
        if (!isActivo()) return false;

        switch (tipoUsuario) {
            case ADMINISTRADOR:
                return true;
            case EMPLEADO:
                return !accion.equals("gestion_usuarios");
            case CLIENTE:
                return accion.equals("comprar_entradas") ||
                        accion.equals("ver_cartelera") ||
                        accion.equals("ver_perfil");
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return String.format("Usuario{nombre='%s', apellido='%s', email='%s', tipo=%s, estado=%s}",
                nombre, apellido, email, tipoUsuario.getDescripcion(), estado.getDescripcion());
    }

    public String toJSONString() {
        return String.format(
                "{\"nombre\":\"%s\",\"apellido\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"," +
                        "\"telefono\":\"%s\",\"tipoUsuario\":\"%s\",\"fechaRegistro\":\"%s\"," +
                        "\"fechaUltimoAcceso\":\"%s\",\"estado\":\"%s\",\"intentosFallidos\":%d}",
                nombre, apellido, email, password, telefono, tipoUsuario.name(),
                fechaRegistro, fechaUltimoAcceso, estado.name(), intentosFallidos
        );
    }



}