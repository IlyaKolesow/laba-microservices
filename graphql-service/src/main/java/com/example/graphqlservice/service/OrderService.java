package com.example.graphqlservice.service;

import com.example.graphqlservice.client.order.OrderRestClient;
import com.example.graphqlservice.client.product.ProductRestClient;
import com.example.graphqlservice.data.Order;
import com.example.graphqlservice.data.Product;
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
        setProducts(order);
        return order;
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderRestClient.getOrders();
        orders.forEach(this::setProducts);
        return orders;
    }

    private void setProducts(Order order) {
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
