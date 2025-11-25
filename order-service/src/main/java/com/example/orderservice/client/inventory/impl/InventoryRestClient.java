package com.example.orderservice.client.inventory.impl;

import com.example.orderservice.client.inventory.InventoryClient;
import com.example.orderservice.data.dto.ProductQuantityDto;
import com.example.orderservice.data.model.InventoryQuantityChange;
import com.example.orderservice.exception.OrderInventoryRequestRuntimeException;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@Profile("rest")
public class InventoryRestClient implements InventoryClient {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://inventory-service:8081/api/inventories")
            .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                if (response.getStatusCode().isError()) {
                    String message = new String(response.getBody().readAllBytes());
                    throw new OrderInventoryRequestRuntimeException(message, response.getStatusCode());
                }
            })
            .build();

    @Override
    public List<InventoryQuantityChange> takeProductsFromInventories(List<ProductQuantityDto> products) {
        return restClient.patch()
                .uri("/products/take")
                .body(products)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    @Override
    public void updateProductsQuantityInInventories(List<InventoryQuantityChange> inventoryQuantityChanges) {
        restClient.patch()
                .uri("/products/quantity")
                .body(inventoryQuantityChanges)
                .retrieve()
                .toBodilessEntity();
    }

}
