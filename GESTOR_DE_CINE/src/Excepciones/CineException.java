package Excepciones;

public abstract class CineException extends Exception {
    private final int codigoError;
    private final String modulo;
    private final String tipo;
    private final String timestamp;

    public CineException(String mensaje, int codigoError, String modulo, String tipo) {
        super(mensaje);
        this.codigoError = codigoError;
        this.modulo = modulo;
        this.tipo = tipo;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public int getCodigoError() { return codigoError; }
    public String getModulo() { return modulo; }
    public String getTipo() { return tipo; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String getMessage() {
        return String.format("[%s-%04d] %s | Módulo: %s | Time: %s",
                tipo, codigoError, super.getMessage(), modulo, timestamp);
    }

    public String toJSON() {
        return String.format("{\"error\":\"%s\",\"codigo\":%d,\"modulo\":\"%s\",\"tipo\":\"%s\",\"timestamp\":\"%s\"}",
                super.getMessage(), codigoError, modulo, tipo, timestamp);
    }
}