package com.main.EMS_backend.exception;

public class EventNotFoundException extends RuntimeException{
    public EventNotFoundException(String message) {
        super(message);
    }
}
