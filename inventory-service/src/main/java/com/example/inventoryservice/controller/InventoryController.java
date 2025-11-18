package com.example.inventoryservice.controller;

import com.example.inventoryservice.data.dto.InventoryCreationDto;
import com.example.inventoryservice.data.dto.InventoryDto;
import com.example.inventoryservice.data.dto.ProductDto;
import com.example.inventoryservice.data.dto.ProductIdsDto;
import com.example.inventoryservice.data.dto.ProductInventoryDto;
import com.example.inventoryservice.data.dto.UpdateQuantityDto;
import com.example.inventoryservice.exception.InventoryBadRequestException;
import com.example.inventoryservice.exception.InventoryNotFoundException;
import com.example.inventoryservice.service.InventoryService;
import com.example.inventoryservice.service.ProductInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductInventoryService productInventoryService;
    private final ModelMapper mapper;

    @GetMapping
    @Operation(summary = "Получить все склады")
    public List<InventoryDto> getAllInventories() {
        return inventoryService.findAll().stream()
                .map(product -> mapper.map(product, InventoryDto.class))
                .toList();
    }

    @GetMapping("/{inventoryId}")
    @Operation(summary = "Получить склад по id")
    public InventoryDto getInventoryById(@PathVariable int inventoryId) throws InventoryNotFoundException {
        return mapper.map(inventoryService.findById(inventoryId), InventoryDto.class);
    }

    @GetMapping("/{inventoryId}/products")
    @Operation(summary = "Получить список продуктов на складе")
    public List<ProductDto> getProductsFromInventory(@PathVariable int inventoryId) throws InventoryNotFoundException {
        return productInventoryService.getProductsByInventoryId(inventoryId).stream()
                .map(productInventory -> mapper.map(productInventory, ProductDto.class))
                .toList();
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Получить список складов, где есть данный продукт")
    public List<ProductInventoryDto> getInventoriesByProduct(@PathVariable int productId) {
        return productInventoryService.getInventoriesByProductId(productId).stream()
                .map(productInventory -> mapper.map(productInventory, ProductInventoryDto.class))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новый склад")
    public InventoryDto createInventory(@RequestBody InventoryCreationDto dto) {
        return mapper.map(inventoryService.createInventory(dto), InventoryDto.class);
    }

    @PostMapping("/{inventoryId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить продукты на склад")
    public List<ProductDto> addProductsToInventory(@PathVariable int inventoryId, @RequestBody List<ProductDto> dto) {
        return productInventoryService.addProductsToInventory(inventoryId, dto).stream()
                .map(productInventory -> mapper.map(productInventory, ProductDto.class))
                .toList();
    }

    @PutMapping
    @Operation(summary = "Изменить данные склада")
    public InventoryDto updateInventory(@RequestBody InventoryDto dto) throws InventoryNotFoundException {
        return mapper.map(inventoryService.updateInventory(dto), InventoryDto.class);
    }

    @PatchMapping("/products/quantity")
    @Operation(summary = "Изменить количество продуктов на складе")
    public List<ProductDto> updateProductsQuantity(@RequestBody List<UpdateQuantityDto> dto)
            throws InventoryNotFoundException, InventoryBadRequestException {
        return productInventoryService.updateProductsQuantity(dto).stream()
                .map(productInventory -> mapper.map(productInventory, ProductDto.class))
                .toList();
    }

    @DeleteMapping("/{inventoryId}")
    @Operation(summary = "Удалить склад по id")
    public String deleteInventory(@PathVariable int inventoryId) throws InventoryNotFoundException {
        inventoryService.deleteById(inventoryId);
        return "Склад удален";
    }

    @DeleteMapping("/{inventoryId}/products")
    @Operation(summary = "Удалить продукты со склада")
    public String deleteProductsFromInventory(@PathVariable int inventoryId, @RequestBody ProductIdsDto dto) {
        productInventoryService.deleteProductsFromInventory(inventoryId, dto.getProductIds());
        return "Продукты удалены со склада";
    }

}
