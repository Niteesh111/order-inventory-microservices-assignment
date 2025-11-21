package com.koerber.inventory_service.Controller;

import com.koerber.inventory_service.Entity.Inventory;
import com.koerber.inventory_service.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    InventoryService inventoryService;

    @GetMapping("/{productId}")
    public List<Inventory> getBatches(@PathVariable String productId) {
        return inventoryService.getBatchesByProduct(productId);
    }

    @PostMapping("/update")
    public void updateInventory(@RequestBody Inventory inventory) {
        inventoryService.updateInventory(inventory);
    }
}
