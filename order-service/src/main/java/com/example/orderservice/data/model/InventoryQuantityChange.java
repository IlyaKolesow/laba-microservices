package com.example.orderservice.data.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryQuantityChange {

    private int inventoryId;
    private int productId;
    private int delta;

}