package com.example.productservice.service;

import com.example.productservice.data.dto.ProductCreationDto;
import com.example.productservice.data.dto.ProductDto;
import com.example.productservice.data.model.Product;
import com.example.productservice.exception.ProductNotFoundException;
import com.example.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product findById(int id) throws ProductNotFoundException {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Не найдена сущность Product с id = " + id));
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product createProduct(ProductCreationDto dto) {
        return productRepository.save(
                Product.builder()
                        .name(dto.getName())
                        .description(dto.getDescription())
                        .weight(dto.getWeight())
                        .build()
        );
    }

    public Product updateProduct(ProductDto dto) throws ProductNotFoundException {
        Product product = findById(dto.getId());

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setWeight(dto.getWeight());

        return productRepository.save(product);
    }

}
