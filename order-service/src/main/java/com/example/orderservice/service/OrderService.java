package com.example.orderservice.service;

import com.example.orderservice.client.inventory.InventoryRestClient;
import com.example.orderservice.data.dto.NotificationCreationDto;
import com.example.orderservice.data.dto.OrderCreationDto;
import com.example.orderservice.data.dto.ProductQuantityDto;
import com.example.orderservice.data.model.Order;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.util.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryRestClient inventoryRestClient;
    private final NotificationService notificationService;
    private final ModelMapper mapper;

    public Order findById(String id) throws OrderNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Не найден заказ с id = " + id));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order createOrder(OrderCreationDto dto) {
        inventoryRestClient.takeProductsFromInventories(dto.getProducts().stream()
                .map(product -> mapper.map(product, ProductQuantityDto.class))
                .toList());
        double totalPrice = dto.getProducts().stream()
                .mapToDouble(productDto -> productDto.getPrice() * productDto.getQuantity())
                .sum();
        Order order = orderRepository.save(Order.builder()
                .customerName(dto.getCustomerName())
                .totalPrice(totalPrice)
                .createdAt(LocalDateTime.now())
                .products(dto.getProducts().stream().map(OrderMapper::mapProduct).toList())
                .build());
        notificationService.send(NotificationCreationDto.builder()
                .type("ORDER_CREATED")
                .message("Заказ создан (orderId = " + order.getId() + ", customerName = " + order.getCustomerName() + ")")
                .build());
        return order;
    }

    public void cancelOrder(String id) throws OrderNotFoundException {
        Order order = findById(id);
        orderRepository.deleteById(id);
        notificationService.send(NotificationCreationDto.builder()
                .type("ORDER_CANCELED")
                .message("Заказ отменен (orderId = " + order.getId() + ", customerName = " + order.getCustomerName() + ")")
                .build());
    }

}
