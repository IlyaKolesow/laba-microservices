package com.example.orderservice.util;

import com.example.orderservice.data.dto.OrderDto;
import com.example.orderservice.data.dto.ProductItemDto;
import com.example.orderservice.data.model.Order;
import com.example.orderservice.data.model.ProductItem;

import java.util.List;

public class OrderMapper {

    private OrderMapper() {
    }

    public static OrderDto mapOrder(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .products(mapProduct(order.getProducts()))
                .build();
    }

    public static List<OrderDto> mapOrder(List<Order> orders) {
        return orders.stream()
                .map(OrderMapper::mapOrder)
                .toList();
    }

    public static ProductItemDto mapProduct(ProductItem product) {
        return ProductItemDto.builder()
                .productId(product.getProductId())
                .price(product.getPrice())
                .name(product.getName())
                .quantity(product.getQuantity())
                .build();
    }

    public static ProductItem mapProduct(ProductItemDto dto) {
        return ProductItem.builder()
                .productId(dto.getProductId())
                .price(dto.getPrice())
                .name(dto.getName())
                .quantity(dto.getQuantity())
                .build();
    }

    public static List<ProductItemDto> mapProduct(List<ProductItem> products) {
        return products.stream()
                .map(OrderMapper::mapProduct)
                .toList();
    }

}