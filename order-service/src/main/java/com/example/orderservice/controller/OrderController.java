package com.example.orderservice.controller;

import com.example.orderservice.data.dto.OrderCreationDto;
import com.example.orderservice.data.dto.OrderDto;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.util.OrderMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Получить все заказы")
    public List<OrderDto> getAllOrders() {
        return OrderMapper.mapOrder(orderService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить заказ по id")
    public OrderDto getOrder(@PathVariable String id) throws OrderNotFoundException {
        return OrderMapper.mapOrder(orderService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новый заказ")
    public OrderDto createOrder(@RequestBody OrderCreationDto dto) {
        return OrderMapper.mapOrder(orderService.createOrder(dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Отменить заказ")
    public String cancelOrder(@PathVariable String id) throws OrderNotFoundException {
        orderService.cancelOrder(id);
        return "Отменен заказ с id = " + id;
    }

}
