package com.reliaquest.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reliaquest.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmployeeNotFoundException(
            EmployeeNotFoundException ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Employee Not Found",
                ex.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Handle Retry failed in case of 429 TOO_MANY_REQUESTS
    @ExceptionHandler(RetryFailedException.class)
    public ResponseEntity<ErrorResponse> handleRetryFailed(RetryFailedException ex, HttpServletRequest req) {

        return new ResponseEntity<>(
                new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_GATEWAY.value(),
                        "Retry Failed",
                        "Unable to reach remote server after multiple attempts",
                        req.getRequestURI()),
                HttpStatus.BAD_GATEWAY);
    }

    // Handles validation errors (for @Valid inputs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String msg = ex.getBindingResult().getFieldError().getDefaultMessage();

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Validation Failed", msg, request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handles WebClient errors (4xx / 5xx from external API)
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientResponseException(
            WebClientResponseException ex, HttpServletRequest request) {

        @SuppressWarnings("deprecation")
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                ex.getRawStatusCode(),
                "Remote API Error",
                ex.getResponseBodyAsString(),
                request.getRequestURI());

        return new ResponseEntity<>(error, ex.getStatusCode());
    }

    // Handles JSON parsing / decoding issues
    @ExceptionHandler({DecodingException.class, JsonProcessingException.class})
    public ResponseEntity<ErrorResponse> handleDecodingExceptions(Exception ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Invalid Response Format",
                "Failed to decode server response",
                request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handles all other runtime / unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
