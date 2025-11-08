package com.example.inventoryservice.controller;

import com.example.inventoryservice.data.dto.InventoryCreationDto;
import com.example.inventoryservice.data.dto.InventoryDto;
import com.example.inventoryservice.exception.InventoryNotFoundException;
import com.example.inventoryservice.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inventories")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ModelMapper mapper;

    @GetMapping
    @Operation(summary = "Получить все склады")
    public List<InventoryDto> getAllProducts() {
        return inventoryService.findAll().stream()
                .map(product -> mapper.map(product, InventoryDto.class))
                .toList();
    }

    @GetMapping("/{inventoryId}")
    @Operation(summary = "Получить склад по id")
    public InventoryDto getProductById(@PathVariable String inventoryId) throws InventoryNotFoundException {
        return mapper.map(inventoryService.findById(Integer.parseInt(inventoryId)), InventoryDto.class);
    }

    @PostMapping
    @Operation(summary = "Создать новый склад")
    public ResponseEntity<InventoryDto> createProduct(@RequestBody InventoryCreationDto dto) {
        return new ResponseEntity<>(mapper.map(inventoryService.createInventory(dto), InventoryDto.class), HttpStatus.CREATED);
    }

    @PutMapping
    @Operation(summary = "Изменить данные склада")
    public InventoryDto updateProduct(@RequestBody InventoryDto dto) throws InventoryNotFoundException {
        return mapper.map(inventoryService.updateInventory(dto), InventoryDto.class);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить склад по id")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) throws InventoryNotFoundException {
        inventoryService.deleteById(Integer.parseInt(id));
        return ResponseEntity.ok("Склад удален");
    }

}
