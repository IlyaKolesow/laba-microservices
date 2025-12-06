package com.example.graphqlservice.client.order;

import com.example.graphqlservice.data.Order;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class OrderRestClient {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://order-service:8082/api/orders")
//            .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
//                if (response.getStatusCode().isError()) {
//                    String message = new String(response.getBody().readAllBytes());
//                    throw new OrderInventoryRequestRuntimeException(message, response.getStatusCode());
//                }
//            })
            .build();

    public Order getOrder(String id) {
        return restClient.get()
                .uri("/{id}", id)
                .retrieve()
                .body(Order.class);
    }

    public List<Order> getOrders() {
        return restClient.get()
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

}
