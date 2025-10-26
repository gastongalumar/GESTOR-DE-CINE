package Excepciones;

public class AutenticacionException extends CineException {
    public static final int CREDENCIALES_INVALIDAS = 2001;
    public static final int USUARIO_BLOQUEADO = 2002;
    public static final int SESION_EXPIRADA = 2003;
    public static final int CUENTA_INACTIVA = 2004;

    public AutenticacionException(String mensaje, int codigoError) {
        super(mensaje, codigoError, "Autenticación", "AUTH");
    }

    public static AutenticacionException credencialesInvalidas() {
        return new AutenticacionException(
                "Credenciales de acceso inválidas",
                CREDENCIALES_INVALIDAS
        );
    }

    public static AutenticacionException usuarioBloqueado(String email) {
        return new AutenticacionException(
                "Usuario bloqueado: " + email,
                USUARIO_BLOQUEADO
        );
    }

    public static AutenticacionException cuentaInactiva(String email) {
        return new AutenticacionException(
                "Cuenta inactiva: " + email,
                CUENTA_INACTIVA
        );
    }



}