package com.conjunto.control_vehicular.exception;

public class AutorizacionDenegadaException extends RuntimeException {
    public AutorizacionDenegadaException(String mensaje) {
        super(mensaje);
    }
}