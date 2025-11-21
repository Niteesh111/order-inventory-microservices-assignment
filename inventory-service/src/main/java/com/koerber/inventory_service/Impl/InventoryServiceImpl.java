package com.koerber.inventory_service.Impl;

import ch.qos.logback.core.util.StringUtil;
import com.koerber.inventory_service.Entity.Inventory;
import com.koerber.inventory_service.Repo.InventoryRepo;
import com.koerber.inventory_service.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {
    @Autowired
    InventoryService inventoryService;
    @Autowired
    InventoryRepo inventoryRepo;

    @Override
    public List<Inventory> getBatchesByProduct(String productId) {
        if(StringUtil.notNullNorEmpty(productId)) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        Optional<Inventory> inventory = inventoryRepo.findInventoryDetailsByProductId(productId);

        if (Optional.empty().isEmpty()){
            return new ArrayList<>();
        }
              return   inventory.stream()
                .map(inv -> new Inventory(inv.getInventoryId(), inv.getProductId(), inv.getQuantity(), inv.getExpiryDate()))
                .toList();
    }

    @Override
    @Transactional
    public void updateInventory(Inventory inventory) {

    }
}
