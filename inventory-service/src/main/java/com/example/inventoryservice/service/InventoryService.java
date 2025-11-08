package com.example.inventoryservice.service;

import com.example.inventoryservice.data.dto.InventoryCreationDto;
import com.example.inventoryservice.data.dto.InventoryDto;
import com.example.inventoryservice.data.model.Inventory;
import com.example.inventoryservice.exception.InventoryNotFoundException;
import com.example.inventoryservice.repository.InventoryRepository;
import com.example.inventoryservice.repository.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductInventoryRepository productInventoryRepository;

    public Inventory findById(int id) throws InventoryNotFoundException {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new InventoryNotFoundException("Не найден склад с id = " + id));
    }

    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    public Inventory createInventory(InventoryCreationDto dto) {
        return inventoryRepository.save(
                Inventory.builder()
                        .name(dto.getName())
                        .city(dto.getCity())
                        .build()
        );
    }

    public Inventory updateInventory(InventoryDto dto) throws InventoryNotFoundException {
        Inventory inventory = findById(dto.getId());

        inventory.setName(dto.getName());
        inventory.setCity(dto.getCity());

        return inventoryRepository.save(inventory);
    }

    @Transactional
    public void deleteById(int id) throws InventoryNotFoundException {
        findById(id);
        inventoryRepository.deleteById(id);
        productInventoryRepository.deleteAllByInventoryId(id);
    }

}
