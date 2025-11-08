package com.example.inventoryservice.repository;

import com.example.inventoryservice.data.model.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Integer> {
}
