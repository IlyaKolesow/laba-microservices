package com.example.orderservice.util;

import com.example.orderservice.data.dto.OrderDto;
import com.example.orderservice.data.dto.ProductQuantityDto;
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
                .createdAt(order.getCreatedAt())
                .products(mapProduct(order.getProducts()))
                .build();
    }

    public static List<OrderDto> mapOrder(List<Order> orders) {
        return orders.stream()
                .map(OrderMapper::mapOrder)
                .toList();
    }

    public static ProductQuantityDto mapProduct(ProductItem product) {
        return ProductQuantityDto.builder()
                .productId(product.getProductId())
                .quantity(product.getQuantity())
                .build();
    }

    public static ProductItem mapProduct(ProductQuantityDto dto) {
        return ProductItem.builder()
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .build();
    }

    public static List<ProductQuantityDto> mapProduct(List<ProductItem> products) {
        return products.stream()
                .map(OrderMapper::mapProduct)
                .toList();
    }

}