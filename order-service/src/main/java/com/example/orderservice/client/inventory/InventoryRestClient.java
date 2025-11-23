package com.example.orderservice.client.inventory;

import com.example.orderservice.data.dto.ProductQuantityDto;
import com.example.orderservice.data.model.InventoryQuantityChange;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class InventoryRestClient {

    private final RestClient restClient = RestClient.builder().baseUrl("http://inventory-service:8081/api/inventories").build();

    public List<InventoryQuantityChange> takeProductsFromInventories(List<ProductQuantityDto> products) {
        return restClient.patch()
                .uri("/products/take")
                .body(products)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public void updateProductsQuantityInInventories(List<InventoryQuantityChange> inventoryQuantityChanges) {
        restClient.patch()
                .uri("/products/quantity")
                .body(inventoryQuantityChanges)
                .retrieve()
                .toBodilessEntity();
    }

}
