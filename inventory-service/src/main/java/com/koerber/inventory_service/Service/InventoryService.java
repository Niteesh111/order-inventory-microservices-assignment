package com.koerber.inventory_service.Service;

import com.koerber.inventory_service.Entity.Inventory;

import java.util.List;

public interface InventoryService {
    List<Inventory> getBatchesByProduct(String productId);
    void updateInventory(Inventory inventory);
}
