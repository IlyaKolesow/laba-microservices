package com.example.orderservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class OrderInventoryRequestRuntimeException extends OrderRuntimeException {

    private final HttpStatusCode statusCode;

    public OrderInventoryRequestRuntimeException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

}
