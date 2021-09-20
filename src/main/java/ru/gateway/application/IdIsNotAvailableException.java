package ru.gateway.application;

public class IdIsNotAvailableException extends RuntimeException {
    public IdIsNotAvailableException(String message) {
        super(message);
    }
}
