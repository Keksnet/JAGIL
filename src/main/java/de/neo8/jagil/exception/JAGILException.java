package de.neo8.jagil.exception;

public class JAGILException extends RuntimeException {

    public JAGILException(String message) {
        super(message);
    }

    public JAGILException(String message, Throwable cause) {
        super(message, cause);
    }

}
