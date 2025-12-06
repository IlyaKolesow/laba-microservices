package com.example.graphqlservice.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderCreationDto {

    private String customerName;
    private List<ProductQuantityDto> products;

}