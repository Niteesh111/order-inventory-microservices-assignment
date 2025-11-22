package com.koerber.order_service.ExceptionHandler;

public class InventoryServiceException extends  RuntimeException{
    public InventoryServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
