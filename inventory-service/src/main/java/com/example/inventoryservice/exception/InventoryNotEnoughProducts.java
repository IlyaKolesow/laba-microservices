package com.example.inventoryservice.exception;

public class InventoryNotEnoughProducts extends InventoryException {

    public InventoryNotEnoughProducts(String message) {
        super(message);
    }

}
