package Excepciones;

public class FuncionInvalidaException extends RuntimeException {
    public FuncionInvalidaException(String message) {
        super(message);
    }
}
