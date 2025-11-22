package com.koerber.inventory_service.Controller;

import com.koerber.inventory_service.Dto.InventoryUpdateRequest;
import com.koerber.inventory_service.Entity.Inventory;
import com.koerber.inventory_service.Service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<List<Inventory>> getBatches(@PathVariable Long productId) {
        if (productId == null || productId <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<Inventory> batches = inventoryService.getBatchesByProduct(productId);
        if (batches == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (batches.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(batches);
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateInventory(@RequestBody InventoryUpdateRequest inventoryUpdateRequest) {
        if (inventoryUpdateRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        inventoryService.updateInventory(inventoryUpdateRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/quantity/{productId}")
    public ResponseEntity<Long> getQuantity(@PathVariable Long productId) {
        if (productId == null || productId == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Long quantity = inventoryService.getQuantity(productId);
        return ResponseEntity.ok(quantity);
    }
}
