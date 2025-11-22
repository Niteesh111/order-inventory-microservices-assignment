package com.koerber.order_service.ExceptionHandler;

public class ProductNotExistException extends RuntimeException{
    public ProductNotExistException(String message, RuntimeException e) {
        super(message);
    }
}
