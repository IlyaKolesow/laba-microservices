package com.example.orderservice.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreationDto {

    private String customerName;
    private List<ProductItemDto> products;

}
