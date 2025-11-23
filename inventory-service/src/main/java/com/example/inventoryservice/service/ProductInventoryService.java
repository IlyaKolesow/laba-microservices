package com.example.inventoryservice.service;

import com.example.inventoryservice.data.dto.ProductQuantityDto;
import com.example.inventoryservice.data.dto.UpdateQuantityDto;
import com.example.inventoryservice.data.model.ProductInventory;
import com.example.inventoryservice.exception.InventoryBadRequestException;
import com.example.inventoryservice.exception.InventoryNotEnoughProducts;
import com.example.inventoryservice.exception.InventoryNotFoundException;
import com.example.inventoryservice.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductInventoryService {

    private final ProductInventoryRepository productInventoryRepository;
    private final InventoryService inventoryService;

    public List<ProductInventory> getProductsByInventoryId(int id) throws InventoryNotFoundException {
        inventoryService.findById(id);
        return productInventoryRepository.findByInventoryId(id);
    }

    public List<ProductInventory> getInventoriesByProductId(int id) {
        return productInventoryRepository.findByProductId(id);
    }

    public List<ProductInventory> addProductsToInventory(int inventoryId, List<ProductQuantityDto> products) {
        return productInventoryRepository.saveAll(products.stream()
                .map(product -> ProductInventory.builder()
                        .inventoryId(inventoryId)
                        .productId(product.getProductId())
                        .quantity(product.getQuantity())
                        .build())
                .toList());
    }

    public List<ProductInventory> updateProductsQuantity(List<UpdateQuantityDto> updateQuantityDto)
            throws InventoryNotFoundException, InventoryBadRequestException {
        Map<Integer, List<UpdateQuantityDto>> groupedByInventoryId = updateQuantityDto.stream()
                .collect(Collectors.groupingBy(UpdateQuantityDto::getInventoryId));

        List<ProductInventory> entitiesToSave = new ArrayList<>();
        List<ProductInventory> entitiesToDelete = new ArrayList<>();

        for (Map.Entry<Integer, List<UpdateQuantityDto>> entry : groupedByInventoryId.entrySet()) {
            int inventoryId = entry.getKey();
            List<UpdateQuantityDto> dtoList = entry.getValue();

            List<Integer> productIds = dtoList.stream()
                    .map(UpdateQuantityDto::getProductId)
                    .toList();

            List<ProductInventory> productInventoryList = productInventoryRepository
                    .findAllByInventoryIdAndProductIdIn(inventoryId, productIds);

            Map<Integer, ProductInventory> productIdToProductInventoryMap = productInventoryList.stream()
                    .collect(Collectors.toMap(ProductInventory::getProductId, productInventory -> productInventory));

            for (UpdateQuantityDto dto : dtoList) {
                int productId = dto.getProductId();
                ProductInventory productInventory = productIdToProductInventoryMap.get(productId);
                if (productInventory == null) {
                    throw new InventoryNotFoundException(
                            "Не найдены данные продукта с inventoryId = " + inventoryId + " и productId = " + productId
                    );
                }

                int newQuantity = productInventory.getQuantity() + dto.getDelta();

                if (newQuantity < 0) {
                    throw new InventoryBadRequestException(
                            "Недостаточно продукта на складе inventoryId = " + inventoryId + ", productId = " + productId
                    );
                } else if (newQuantity == 0) {
                    entitiesToDelete.add(productInventory);
                } else {
                    productInventory.setQuantity(newQuantity);
                    entitiesToSave.add(productInventory);
                }
            }
        }
        productInventoryRepository.deleteAll(entitiesToDelete);
        return productInventoryRepository.saveAll(entitiesToSave);
    }

    public void deleteProductsFromInventory(int inventoryId, List<Integer> productIds) {
        productInventoryRepository.deleteAllByInventoryIdAndProductIdIn(inventoryId, productIds);
    }

    public List<UpdateQuantityDto> takeProductsFromInventories(List<ProductQuantityDto> products)
            throws InventoryNotFoundException, InventoryBadRequestException, InventoryNotEnoughProducts {
        List<UpdateQuantityDto> updateQuantityDtoList = new ArrayList<>();
        for (ProductQuantityDto product : products) {
            List<ProductInventory> inventories = getInventoriesByProductId(product.getProductId())
                    .stream()
                    .sorted(Comparator.comparing(ProductInventory::getQuantity).reversed())
                    .toList();

            int requiredQuantity = product.getQuantity();

            if (!isEnoughProductInInventories(inventories.stream().map(ProductInventory::getQuantity).toList(),
                    requiredQuantity)) {
                throw new InventoryNotEnoughProducts(
                        "Недостаточно продукта на складах (productId = " + product.getProductId() + ")"
                );
            }

            int takenQuantity = 0;
            int remainingQuantity;

            for (ProductInventory inventory : inventories) {
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
        updateProductsQuantity(updateQuantityDtoList);
        return updateQuantityDtoList;
    }

    private boolean isEnoughProductInInventories(List<Integer> inventoriesQuantity, int requiredQuantity) {
        int quantityInInventories = 0;
        for (int inventoryQuantity : inventoriesQuantity) {
            quantityInInventories += inventoryQuantity;
            if (quantityInInventories >= requiredQuantity) {
                return true;
            }
        }
        return false;
    }

}
