package com.koerber.inventory_service.ExceptionHandler;

public class ProductNotExistException extends RuntimeException{
    public ProductNotExistException(String message) {
        super(message);
    }
}
