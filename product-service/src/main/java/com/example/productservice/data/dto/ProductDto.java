package com.example.productservice.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

    private Integer id;
    private String name;
    private Double weight;
    private String description;

}
