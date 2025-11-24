package com.nanawally.ToDo_microservice.utility.advice;

public record ErrorResponseBody(
        long timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public ErrorResponseBody(int status, String error, String message, String path) {
        this(System.currentTimeMillis(), status, error, message, path);
    }
}
