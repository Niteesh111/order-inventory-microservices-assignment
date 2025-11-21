package com.koerber.inventory_service.Service;

import com.koerber.inventory_service.Dto.InventoryUpdateRequest;
import com.koerber.inventory_service.Entity.Inventory;

import java.util.List;

public interface InventoryService {
    List<Inventory> getBatchesByProduct(Long productId);
    void updateInventory(InventoryUpdateRequest inventoryUpdateRequest);
}
