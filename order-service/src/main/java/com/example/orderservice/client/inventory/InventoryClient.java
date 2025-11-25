package com.example.orderservice.client.inventory;

import com.example.orderservice.data.dto.ProductQuantityDto;
import com.example.orderservice.data.model.InventoryQuantityChange;

import java.util.List;

public interface InventoryClient {

    List<InventoryQuantityChange> takeProductsFromInventories(List<ProductQuantityDto> products);
    void updateProductsQuantityInInventories(List<InventoryQuantityChange> inventoryQuantityChanges);

}
