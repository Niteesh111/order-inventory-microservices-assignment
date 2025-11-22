package com.koerber.order_service.ExceptionHandler;

public class InsufficientInventoryException extends RuntimeException{
    public InsufficientInventoryException(String message) {
        super(message);
    }
}
