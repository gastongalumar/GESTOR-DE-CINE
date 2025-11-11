package Clases.login.usuario;

import Enumeradores.login.TipoUsuario;
import Enumeradores.login.EstadoUsuario;
import Excepciones.UsuarioException;
import Interfaces.ConversorJson;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public abstract class Usuario{
    protected String nombre;
    protected String apellido;
    protected String email;
    protected String password;
    protected String telefono;
    protected TipoUsuario tipoUsuario;
    protected LocalDateTime fechaRegistro;
    protected LocalDateTime fechaUltimoAcceso;
    protected EstadoUsuario estado;
    protected int intentosFallidos;


    // Constructor para clases anónimas (empleados)
    protected Usuario(String nombre, String apellido, String email, String password,
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

    protected Usuario() {
    }


    public Usuario(String nombre, String apellido, String email, String password, String telefono,
                   String estado, LocalDateTime fechaRegistro, LocalDateTime fechaUltimoAcceso,
                   int intentosFallidos, TipoUsuario tipoUsuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.estado = EstadoUsuario.ACTIVO;
        this.fechaRegistro = fechaRegistro;
        this.fechaUltimoAcceso = fechaUltimoAcceso;
        this.intentosFallidos = intentosFallidos;
        this.tipoUsuario = tipoUsuario;
    }


    public static boolean validarNombreApellido(String texto) {
        return texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s']+$");
    }
    public abstract boolean puedeRealizarAccion(String accion);

    // Métodos de validación (mantener igual)
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

    // Getters y Setters (mantener igual)
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
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getFechaUltimoAcceso() { return fechaUltimoAcceso; }
    public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) {
        this.fechaUltimoAcceso = fechaUltimoAcceso;
    }

    public EstadoUsuario getEstado() { return estado; }
    public void setEstado(EstadoUsuario estado) { this.estado = estado; }

    public int getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }




    @Override
    public String toString() {
        return String.format("Usuario{nombre='%s', apellido='%s', email='%s', tipo=%s, estado=%s}",
                nombre, apellido, email, tipoUsuario.getDescripcion(), estado.getDescripcion());
    }

}