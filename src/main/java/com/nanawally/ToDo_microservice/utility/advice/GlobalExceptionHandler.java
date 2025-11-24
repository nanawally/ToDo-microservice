package com.nanawally.ToDo_microservice.utility.advice;

import com.nanawally.ToDo_microservice.utility.advice.exception.TaskNotFoundException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ErrorResponseBody> buildResponse(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest request
    ) {
        ErrorResponseBody apiError = new ErrorResponseBody(
                status.value(),
                error,
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(apiError);
    }

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ErrorResponseBody> handleRequestNotPermitted(RequestNotPermitted e, HttpServletRequest request) {
        logger.warn("Rate Limit reached: {}", e.getMessage());
        return buildResponse(
                HttpStatus.TOO_MANY_REQUESTS,
                "Too Many Requests",
                "Rate limit reached, try again later",
                request
        );
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponseBody> handleTaskNotFoundException(TaskNotFoundException e, HttpServletRequest request) {
        logger.warn("Task Not Found: {}", e.getMessage());
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Task Not Found",
                e.getMessage(),
                request
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseBody> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logger.error("Unexpected Runtime Exception: ", e);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Runtime Exception",
                e.getMessage(),
                request
        );
    }
}
