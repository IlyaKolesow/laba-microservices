package com.example.orderservice.service;

import com.example.orderservice.data.dto.OrderCreationDto;
import com.example.orderservice.data.model.Order;
import com.example.orderservice.data.model.ProductItem;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.util.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order findById(String id) throws OrderNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Не найден заказ с id = " + id));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order createOrder(OrderCreationDto dto) {
        double totalPrice = dto.getProducts().stream()
                .mapToDouble(productDto -> productDto.getPrice() * productDto.getQuantity())
                .sum();

        List<ProductItem> products = dto.getProducts().stream()
                .map(OrderMapper::mapProduct)
                .toList();

        return orderRepository.save(Order.builder()
                .customerName(dto.getCustomerName())
                .totalPrice(totalPrice)
                .createdAt(LocalDateTime.now())
                .products(products)
                .build());
    }

    public void cancelOrder(String id) throws OrderNotFoundException {
        findById(id);
        orderRepository.deleteById(id);
    }

}
