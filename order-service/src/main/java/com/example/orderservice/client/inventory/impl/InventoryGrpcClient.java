package com.example.orderservice.client.inventory.impl;

import com.example.inventoryservice.InventoryServiceGrpc;
import com.example.inventoryservice.InventoryServiceOuterClass;
import com.example.orderservice.client.inventory.InventoryClient;
import com.example.orderservice.data.dto.ProductQuantityDto;
import com.example.orderservice.data.model.InventoryQuantityChange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("grpc")
@RequiredArgsConstructor
@Slf4j
public class InventoryGrpcClient implements InventoryClient {

    @GrpcClient("inventoryService")
    private InventoryServiceGrpc.InventoryServiceBlockingStub stub;
    private final ModelMapper mapper;

    @Override
    public List<InventoryQuantityChange> takeProductsFromInventories(List<ProductQuantityDto> products) {
        InventoryServiceOuterClass.TakeProductsRequest request = InventoryServiceOuterClass.TakeProductsRequest
                .newBuilder()
                .addAllProducts(products.stream()
                        .map(product -> InventoryServiceOuterClass.ProductQuantityDto
                                .newBuilder()
                                .setProductId(product.getProductId())
                                .setQuantity(product.getQuantity())
                                .build())
                        .toList())
                .build();
        return stub.takeProductsFromInventories(request).getChangesList().stream()
                .map(change -> mapper.map(change, InventoryQuantityChange.class))
                .toList();
    }

    @Override
    public void updateProductsQuantityInInventories(List<InventoryQuantityChange> inventoryQuantityChanges) {
        InventoryServiceOuterClass.UpdateQuantityRequest request = InventoryServiceOuterClass.UpdateQuantityRequest
                .newBuilder()
                .addAllChanges(inventoryQuantityChanges.stream()
                        .map(change -> InventoryServiceOuterClass.UpdateQuantityDto
                                .newBuilder()
                                .setInventoryId(change.getInventoryId())
                                .setProductId(change.getProductId())
                                .setDelta(change.getDelta())
                                .build())
                        .toList())
                .build();
        stub.updateProductsQuantityInInventories(request);
    }

}
