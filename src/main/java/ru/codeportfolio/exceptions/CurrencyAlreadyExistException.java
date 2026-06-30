package ru.codeportfolio.exceptions;

public class CurrencyAlreadyExistException extends RuntimeException{
    public CurrencyAlreadyExistException() {
    }

    public CurrencyAlreadyExistException(String message) {
        super(message);
    }

    public CurrencyAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyAlreadyExistException(Throwable cause) {
        super(cause);
    }
}
