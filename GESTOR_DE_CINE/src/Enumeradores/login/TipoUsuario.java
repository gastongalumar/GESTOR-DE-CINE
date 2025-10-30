package Enumeradores.login;


public enum TipoUsuario {
    ADMINISTRADOR("Administrador"),
    EMPLEADO("Empleado"),
    CLIENTE("Cliente");

    private final String descripcion;

    TipoUsuario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}