package com.koerber.inventory_service.Repo;

import com.koerber.inventory_service.Entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, Integer> {
    List<Inventory> findInventoryDetailsByProductId(Long productId);

    @Query(value = "SELECT SUM(quantity) FROM Inventory  WHERE productId = :productId", nativeQuery = true)
    Optional<Long> getQuantityByProductId(Long productId);
}
