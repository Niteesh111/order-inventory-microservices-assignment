package com.koerber.inventory_service.Repo;

import com.koerber.inventory_service.Entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findInventoryDetailsByProductId(Long productId);
}
