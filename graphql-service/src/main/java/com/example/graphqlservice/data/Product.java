package com.example.graphqlservice.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Product {

    private int productId;
    private String name;
    private double weight;
    private String description;
    private double price;
    private int quantity;

}
