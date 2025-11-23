package com.example.orderservice.exception.handler;

import com.example.orderservice.data.dto.ErrorDto;
import com.example.orderservice.exception.OrderException;
import com.example.orderservice.exception.OrderInventoryRequestRuntimeException;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.exception.OrderRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleException(OrderNotFoundException e) {
        return ErrorDto.builder().code("404").message(e.getMessage()).build();
    }

    @ExceptionHandler(OrderRuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorDto handleException(OrderRuntimeException e) {
        return ErrorDto.builder().code("502").message(e.getMessage()).build();
    }

    @ExceptionHandler(OrderException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorDto handleException(OrderException e) {
        return ErrorDto.builder().code("502").message(e.getMessage()).build();
    }

    @ExceptionHandler(OrderInventoryRequestRuntimeException.class)
    public ResponseEntity<ErrorDto> handleException(OrderInventoryRequestRuntimeException e) {
        return new ResponseEntity<>(ErrorDto.builder()
                .code(e.getStatusCode().toString())
                .message(e.getMessage())
                .build(),
                e.getStatusCode());
    }

}
