package com.example.orderservice.data.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductInventoryDto {

    private int id;
    private int productId;
    private int inventoryId;
    private int quantity;

}
