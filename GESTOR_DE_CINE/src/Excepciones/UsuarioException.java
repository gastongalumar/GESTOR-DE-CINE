package Excepciones;

public class UsuarioException extends CineException {
    // Códigos específicos de usuario
    public static final int USUARIO_NO_ENCONTRADO = 1001;
    public static final int USUARIO_DUPLICADO = 1002;
    public static final int USUARIO_INACTIVO = 1003;
    public static final int DATOS_INVALIDOS = 1004;
    public static final int PERMISOS_INSUFICIENTES = 1005;

    public UsuarioException(String mensaje, int codigoError) {
        super(mensaje, codigoError, "Gestión de Usuarios", "USER");
    }



    // Métodos factory estáticos
    public static UsuarioException usuarioNoEncontrado(String email) {
        return new UsuarioException(
                "Usuario no encontrado: " + email,
                USUARIO_NO_ENCONTRADO
        );
    }

    public static UsuarioException usuarioDuplicado(String email) {
        return new UsuarioException(
                "El usuario ya existe: " + email,
                USUARIO_DUPLICADO
        );
    }

    public static UsuarioException usuarioInactivo(String email) {
        return new UsuarioException(
                "Usuario inactivo: " + email,
                USUARIO_INACTIVO
        );
    }

    public static UsuarioException datosInvalidos(String campo) {
        return new UsuarioException(
                "Datos inválidos en campo: " + campo,
                DATOS_INVALIDOS
        );
    }

    public static UsuarioException permisosInsuficientes() {
        return new UsuarioException(
                "Permisos insuficientes para esta operación",
                PERMISOS_INSUFICIENTES
        );
    }
}