package com.example.notificationservice.exception.handler;

import com.example.notificationservice.data.dto.ErrorDto;
import com.example.notificationservice.exception.NotificationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NotificationExceptionHandler {

    @ExceptionHandler(NotificationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleException(NotificationNotFoundException e) {
        return ErrorDto.builder().code("404").message(e.getMessage()).build();
    }

}
