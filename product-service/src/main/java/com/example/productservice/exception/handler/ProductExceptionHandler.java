package com.example.productservice.exception.handler;

import com.example.productservice.data.dto.ErrorDto;
import com.example.productservice.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleException(ProductNotFoundException e) {
        return ErrorDto.builder().code("404").message(e.getMessage()).build();
    }

}
