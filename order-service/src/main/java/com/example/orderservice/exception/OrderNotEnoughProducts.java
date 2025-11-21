package com.example.orderservice.exception;

public class OrderNotEnoughProducts extends OrderException {

    public OrderNotEnoughProducts(String message) {
        super(message);
    }

}
