package com.example.productservice.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {

    private int id;
    private String name;
    private double weight;
    private String description;
    private double price;

}
