package com.example.productservice.controller;

import com.example.productservice.data.dto.ProductCreationDto;
import com.example.productservice.data.dto.ProductDto;
import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ModelMapper mapper;

    @GetMapping
    @Operation(summary = "Получить все продукты")
    public List<ProductDto> getAllProducts() {
        return productService.findAll().stream()
                .map(product -> mapper.map(product, ProductDto.class))
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить продукт по id")
    public ProductDto getProductById(@PathVariable int id) throws ProductNotFoundException {
        return mapper.map(productService.findById(id), ProductDto.class);
    }

    @PostMapping
    @Operation(summary = "Создать новый продукт")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductCreationDto dto) {
        return new ResponseEntity<>(mapper.map(productService.createProduct(dto), ProductDto.class), HttpStatus.CREATED);
    }

    @PutMapping
    @Operation(summary = "Изменить данные продукта")
    public ProductDto updateProduct(@RequestBody ProductDto dto) throws ProductNotFoundException {
        return mapper.map(productService.updateProduct(dto), ProductDto.class);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить продукт по id")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) throws ProductNotFoundException {
        productService.deleteById(id);
        return ResponseEntity.ok("Продукт удален");
    }

}
