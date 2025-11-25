package com.example.inventoryservice.service;

import com.example.inventoryservice.InventoryServiceGrpc;
import com.example.inventoryservice.InventoryServiceOuterClass;
import com.example.inventoryservice.data.dto.ProductQuantityDto;
import com.example.inventoryservice.data.dto.UpdateQuantityDto;
import com.example.inventoryservice.exception.InventoryBadRequestException;
import com.example.inventoryservice.exception.InventoryNotEnoughProducts;
import com.example.inventoryservice.exception.InventoryNotFoundException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Map;

@GrpcService
@RequiredArgsConstructor
public class InventoryServiceGrpcImpl extends InventoryServiceGrpc.InventoryServiceImplBase {

    private static final Map<Class<? extends Exception>, Status> exceptionToStatusMap = Map.of(
            InventoryNotFoundException.class, Status.NOT_FOUND,
            InventoryBadRequestException.class, Status.INVALID_ARGUMENT,
            InventoryNotEnoughProducts.class, Status.FAILED_PRECONDITION
    );
    private final ProductInventoryService productInventoryService;
    private final ModelMapper mapper;

    @Override
    public void takeProductsFromInventories(
            InventoryServiceOuterClass.TakeProductsRequest request,
            StreamObserver<InventoryServiceOuterClass.TakeProductsResponse> responseObserver) {
        try {
            List<UpdateQuantityDto> updateQuantityDtoList = productInventoryService
                    .takeProductsFromInventories(request.getProductsList().stream()
                            .map(p -> mapper.map(p, ProductQuantityDto.class))
                            .toList());

            InventoryServiceOuterClass.TakeProductsResponse response = InventoryServiceOuterClass.TakeProductsResponse
                    .newBuilder()
                    .addAllChanges(updateQuantityDtoList.stream().map(this::map).toList())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (InventoryNotFoundException | InventoryBadRequestException | InventoryNotEnoughProducts e) {
            responseObserver.onError(exceptionToStatusMap.get(e.getClass())
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateProductsQuantityInInventories(
            InventoryServiceOuterClass.UpdateQuantityRequest request,
            StreamObserver<InventoryServiceOuterClass.UpdateQuantityResponse> responseObserver) {
        try {
            productInventoryService.updateProductsQuantity(request.getChangesList().stream().map(this::map).toList());
            responseObserver.onNext(InventoryServiceOuterClass.UpdateQuantityResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (InventoryNotFoundException | InventoryBadRequestException e) {
            responseObserver.onError(exceptionToStatusMap.get(e.getClass())
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    private InventoryServiceOuterClass.UpdateQuantityDto map(UpdateQuantityDto source) {
        return InventoryServiceOuterClass.UpdateQuantityDto.newBuilder()
                .setInventoryId(source.getInventoryId())
                .setProductId(source.getProductId())
                .setDelta(source.getDelta())
                .build();
    }

    private UpdateQuantityDto map(InventoryServiceOuterClass.UpdateQuantityDto source) {
        return UpdateQuantityDto.builder()
                .inventoryId(source.getInventoryId())
                .productId(source.getProductId())
                .delta(source.getDelta())
                .build();
    }

}
