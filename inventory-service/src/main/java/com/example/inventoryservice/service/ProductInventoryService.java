package com.example.inventoryservice.service;

import com.example.inventoryservice.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductInventoryService {

    private final ProductInventoryRepository productInventoryRepository;


}
