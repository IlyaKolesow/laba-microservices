package com.example.orderservice.client.inventory;

import com.example.orderservice.data.dto.ProductInventoryDto;
import com.example.orderservice.data.dto.UpdateQuantityDto;
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

    public void updateProductsQuantity(List<UpdateQuantityDto> dto) {
        restClient.patch()
                .uri("/products/quantity")
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

}
