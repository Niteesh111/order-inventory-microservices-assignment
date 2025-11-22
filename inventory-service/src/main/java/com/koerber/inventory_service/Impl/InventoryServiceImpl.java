package com.koerber.inventory_service.Impl;

import ch.qos.logback.core.util.StringUtil;
import com.koerber.inventory_service.Dto.InventoryUpdateRequest;
import com.koerber.inventory_service.Entity.Inventory;
import com.koerber.inventory_service.Repo.InventoryRepo;
import com.koerber.inventory_service.Service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    @Autowired
    InventoryRepo inventoryRepo;

    @Override
    public List<Inventory> getBatchesByProduct(Long productId) {
        if(Objects.nonNull(productId)) {
            throw new IllegalArgumentException("Product ID cannot be null or empty");
        }
        Optional<Inventory> inventory = inventoryRepo.findInventoryDetailsByProductId(productId);

        if (inventory.isEmpty()){
            return new ArrayList<>();
        }
        return   inventory.stream().sorted(Comparator.comparing(Inventory::getExpiryDate)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(InventoryUpdateRequest inventoryUpdateRequest) {
        log.info("Updating inventory for product: {} ", inventoryUpdateRequest.getProductId());
        Optional<Inventory> inventoryListDB = inventoryRepo.findInventoryDetailsByProductId(inventoryUpdateRequest.getProductId());
        if (inventoryListDB.isEmpty()){
            log.info("No inventory found for product: {} ", inventoryUpdateRequest.getProductId());
            throw new IllegalArgumentException("No inventory found for product: " + inventoryUpdateRequest.getProductId());
        }
        List<Inventory> sortedInventoryList = inventoryListDB.stream().filter(inv-> inv.getQuantity()>0)
                                              .sorted(Comparator.comparing(Inventory::getExpiryDate))
                                              .collect(Collectors.toList());
        Long quantityToDeduct = inventoryUpdateRequest.getQuantity();
        log.info("Total quantity to deduct: {} ", quantityToDeduct);
        for(Inventory inventory : sortedInventoryList){
            if(inventory.getQuantity() >= quantityToDeduct){
                inventory.setQuantity(inventory.getQuantity() - quantityToDeduct);
                quantityToDeduct =0L;
            } else {
                quantityToDeduct -= inventory.getQuantity();
                inventory.setQuantity(0L);
            }
            inventoryRepo.save(inventory);
        }

    }
}
