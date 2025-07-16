package com.example.demo.errorshandler;


public class EntrepriseAlreadyExistsException extends RuntimeException {
    public EntrepriseAlreadyExistsException(String message) {
        super(message);
    }
}

