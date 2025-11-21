package com.example.orderservice.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductItem {

    private int productId;
    private String name;
    private int quantity;
    private double price;

}
