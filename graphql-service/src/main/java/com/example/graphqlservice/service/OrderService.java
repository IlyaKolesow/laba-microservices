package com.example.graphqlservice.service;

import com.example.graphqlservice.client.order.OrderRestClient;
import com.example.graphqlservice.client.product.ProductRestClient;
import com.example.graphqlservice.data.dto.OrderCreationDto;
import com.example.graphqlservice.data.model.Order;
import com.example.graphqlservice.data.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRestClient orderRestClient;
    private final ProductRestClient productRestClient;

    public Order getOrderById(String id) {
        Order order = orderRestClient.getOrder(id);
        populateOrder(order);
        return order;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderRestClient.getOrders();
        orders.forEach(this::populateOrder);
        return orders;
    }

    public Order createOrder(OrderCreationDto dto) {
        return orderRestClient.createOrder(dto);
    }

    public String cancelOrder(String id) {
        return orderRestClient.cancelOrder(id);
    }

    private void populateOrder(Order order) {
        List<Product> orderProducts = order.getProducts();
        double totalPrice = 0;
        for (Product orderProduct : orderProducts) {
            Product product = productRestClient.getProduct(orderProduct.getProductId());
            orderProduct.setName(product.getName());
            orderProduct.setPrice(product.getPrice());
            orderProduct.setDescription(product.getDescription());
            orderProduct.setWeight(product.getWeight());
            totalPrice += product.getPrice() * orderProduct.getQuantity();
        }
        order.setTotalPrice(totalPrice);
    }

}
