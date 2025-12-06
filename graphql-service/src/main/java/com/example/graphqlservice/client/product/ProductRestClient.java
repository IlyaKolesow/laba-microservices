package com.example.graphqlservice.client.product;

import com.example.graphqlservice.data.model.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ProductRestClient {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://product-service:8080/api/products")
//            .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
//                if (response.getStatusCode().isError()) {
//                    String message = new String(response.getBody().readAllBytes());
//                    throw new OrderInventoryRequestRuntimeException(message, response.getStatusCode());
//                }
//            })
            .build();

    public Product getProduct(int id) {
        return restClient.get()
                .uri("/{id}", id)
                .retrieve()
                .body(Product.class);
    }

}
