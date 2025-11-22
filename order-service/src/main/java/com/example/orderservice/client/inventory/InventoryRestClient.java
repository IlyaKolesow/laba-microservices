package com.example.orderservice.client.inventory;

import com.example.orderservice.data.dto.ProductInventoryDto;
import com.example.orderservice.data.dto.ProductQuantityDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class InventoryRestClient {

    private final RestClient restClient = RestClient.builder().baseUrl("http://inventory-service:8081/api/inventories").build();

    public List<ProductInventoryDto> getInventoriesByProduct(int productId) {
        return restClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public void takeProductsFromInventories(List<ProductQuantityDto> products) {
        restClient.patch()
                .uri("/products/take")
                .body(products)
                .retrieve()
                .toBodilessEntity();
    }

}
