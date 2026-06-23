package com.main.EMS_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = UserNotFoundException.class)
    ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), 404);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = EventNotFoundException.class)
    ResponseEntity<ErrorResponse> handleEventNotFound(EventNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), 404);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = InvalidCredentialsException.class)
    ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), 401);
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), 500);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
