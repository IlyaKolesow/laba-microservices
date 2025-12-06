package com.example.graphqlservice.controller;

import com.example.graphqlservice.data.Order;
import com.example.graphqlservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @QueryMapping
    public Order order(@Argument String id) {
        return orderService.getOrderById(id);
    }

    @QueryMapping
    public List<Order> orders() {
        return orderService.getAllOrders();
    }

}
