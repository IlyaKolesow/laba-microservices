package com.example.inventoryservice.repository;

import com.example.inventoryservice.data.model.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Integer> {

    List<ProductInventory> findByInventoryId(int id);
    List<ProductInventory> findByProductId(int id);
    List<ProductInventory> findAllByInventoryIdAndProductIdIn(int inventoryId, List<Integer> productIds);
    void deleteAllByInventoryIdAndProductIdIn(int inventoryId, List<Integer> productIds);
    void deleteAllByInventoryId(int inventoryId);

}
