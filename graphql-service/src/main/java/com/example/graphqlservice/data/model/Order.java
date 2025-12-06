package com.example.graphqlservice.data.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Order {

    private String id;
    private String customerName;
    private LocalDateTime createdAt;
    private double totalPrice;
    private List<Product> products;

}
