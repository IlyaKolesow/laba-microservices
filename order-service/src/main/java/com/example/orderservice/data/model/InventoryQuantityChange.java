package com.example.orderservice.data.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InventoryQuantityChange {

    private int inventoryId;
    private int productId;
    private int delta;

}