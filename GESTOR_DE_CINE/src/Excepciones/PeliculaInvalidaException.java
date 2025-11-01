package Excepciones;

public class PeliculaInvalidaException extends RuntimeException {
    public PeliculaInvalidaException(String message) {
        super(message);
    }
}
