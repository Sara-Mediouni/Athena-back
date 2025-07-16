package com.example.demo.errorshandler;

import lombok.Data;

@Data
public class ErrorResponse {
    private String message;
    private int status;
    private long timestamp;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters et setters
}
