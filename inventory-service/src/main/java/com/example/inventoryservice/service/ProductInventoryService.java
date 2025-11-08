package com.example.inventoryservice.service;

import com.example.inventoryservice.data.dto.ProductDto;
import com.example.inventoryservice.data.dto.UpdateQuantityDto;
import com.example.inventoryservice.data.model.ProductInventory;
import com.example.inventoryservice.exception.InventoryNotFoundException;
import com.example.inventoryservice.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<ProductInventory> addProductsToInventory(int inventoryId, List<ProductDto> products) {
        return productInventoryRepository.saveAll(products.stream()
                .map(product -> ProductInventory.builder()
                        .inventoryId(inventoryId)
                        .productId(product.getProductId())
                        .quantity(product.getQuantity())
                        .build())
                .toList());
    }

    public List<ProductInventory> updateProductsQuantity(int inventoryId, List<UpdateQuantityDto> dtoList)
            throws InventoryNotFoundException {
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
            if (newQuantity < 1) {
                productInventoryRepository.delete(productInventory);
            } else {
                productInventory.setQuantity(newQuantity);
            }
        }
        return productInventoryRepository.saveAll(productInventoryList);
    }

    public void deleteProductsFromInventory(int inventoryId, List<Integer> productIds) {
        productInventoryRepository.deleteAllByInventoryIdAndProductIdIn(inventoryId, productIds);
    }

}
