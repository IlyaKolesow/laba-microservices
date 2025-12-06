package com.example.graphqlservice.controller;

import com.example.graphqlservice.data.dto.OrderCreationDto;
import com.example.graphqlservice.data.model.Order;
import com.example.graphqlservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
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

    @MutationMapping
    public Order createOrder(@Argument OrderCreationDto dto) {
        return orderService.createOrder(dto);
    }

    @MutationMapping
    public String cancelOrder(@Argument String id) {
        return orderService.cancelOrder(id);
    }

}
