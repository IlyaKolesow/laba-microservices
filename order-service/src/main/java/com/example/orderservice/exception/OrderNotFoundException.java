package com.example.orderservice.exception;

public class OrderNotFoundException extends OrderException {

    public OrderNotFoundException(String message) {
        super(message);
    }

}
