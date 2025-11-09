package com.example.notificationservice.data.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorDto {

    private String code;
    private String message;

}
