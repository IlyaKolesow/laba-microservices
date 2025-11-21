package com.example.orderservice.service;

import com.example.orderservice.client.inventory.InventoryRestClient;
import com.example.orderservice.data.dto.OrderCreationDto;
import com.example.orderservice.data.dto.ProductInventoryDto;
import com.example.orderservice.data.dto.ProductItemDto;
import com.example.orderservice.data.dto.UpdateQuantityDto;
import com.example.orderservice.data.model.Order;
import com.example.orderservice.exception.OrderNotEnoughProducts;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.util.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryRestClient inventoryRestClient;

    public Order findById(String id) throws OrderNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Не найден заказ с id = " + id));
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order createOrder(OrderCreationDto dto) throws OrderNotEnoughProducts {
        takeProductsFromInventories(dto);

        double totalPrice = dto.getProducts().stream()
                .mapToDouble(productDto -> productDto.getPrice() * productDto.getQuantity())
                .sum();

        return orderRepository.save(Order.builder()
                .customerName(dto.getCustomerName())
                .totalPrice(totalPrice)
                .createdAt(LocalDateTime.now())
                .products(dto.getProducts().stream().map(OrderMapper::mapProduct).toList())
                .build());
    }

    public void cancelOrder(String id) throws OrderNotFoundException {
        findById(id);
        orderRepository.deleteById(id);
    }

    private void takeProductsFromInventories(OrderCreationDto orderCreation) throws OrderNotEnoughProducts {
        List<UpdateQuantityDto> updateQuantityDtoList = new ArrayList<>();
        for (ProductItemDto product : orderCreation.getProducts()) {
            List<ProductInventoryDto> inventories = inventoryRestClient.getInventoriesByProduct(product.getProductId())
                    .stream()
                    .sorted(Comparator.comparing(ProductInventoryDto::getQuantity).reversed())
                    .toList();

            int requiredQuantity = product.getQuantity();

            if (!isEnoughProductInInventories(inventories, requiredQuantity)) {
                throw new OrderNotEnoughProducts(
                        "Недостаточно продукта на складах (productId = " + product.getProductId() + ")"
                );
            }

            int takenQuantity = 0;
            int remainingQuantity;

            for (ProductInventoryDto inventory : inventories) {
                UpdateQuantityDto dto = UpdateQuantityDto.builder()
                        .inventoryId(inventory.getInventoryId())
                        .productId(product.getProductId())
                        .build();
                updateQuantityDtoList.add(dto);

                remainingQuantity = requiredQuantity - takenQuantity;
                if (remainingQuantity <= inventory.getQuantity()) {
                    dto.setDelta(-remainingQuantity);
                    break;
                }
                dto.setDelta(-inventory.getQuantity());
                takenQuantity += inventory.getQuantity();
            }
        }
        inventoryRestClient.updateProductsQuantity(updateQuantityDtoList);
    }

    private boolean isEnoughProductInInventories(List<ProductInventoryDto> inventories, int requiredQuantity) {
        int quantityInInventories = 0;
        for (ProductInventoryDto inventory : inventories) {
            quantityInInventories += inventory.getQuantity();
            if (quantityInInventories >= requiredQuantity) {
                return true;
            }
        }
        return false;
    }

}
