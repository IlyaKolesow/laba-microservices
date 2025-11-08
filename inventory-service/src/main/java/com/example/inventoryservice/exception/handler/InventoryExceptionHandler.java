package com.example.inventoryservice.exception.handler;

import com.example.inventoryservice.data.dto.ErrorDto;
import com.example.inventoryservice.exception.InventoryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InventoryExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleException(InventoryNotFoundException e) {
        return ErrorDto.builder().code("404").message(e.getMessage()).build();
    }

}
