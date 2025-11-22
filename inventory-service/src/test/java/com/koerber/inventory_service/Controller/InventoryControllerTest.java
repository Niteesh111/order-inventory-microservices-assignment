package com.koerber.inventory_service.Controller;

import com.koerber.inventory_service.Dto.InventoryUpdateRequest;
import com.koerber.inventory_service.Entity.Inventory;
import com.koerber.inventory_service.ExceptionHandler.ProductNotExistException;
import com.koerber.inventory_service.Service.InventoryService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class InventoryControllerTest {
    @MockitoBean
    private InventoryService inventoryService;
    @Autowired
    private InventoryController inventoryController;

    @Test
    void validProductId() {
        Long productId = 1L;
        Inventory inv1 = new Inventory();
        inv1.setInventoryId(1L);
        inv1.setProductId(productId);
        inv1.setBatchId(101L);
        inv1.setQuantity(10L);
        inv1.setExpiryDate(LocalDate.now().plusDays(10));
        inv1.setUpdatedTime(OffsetDateTime.now());

        when(inventoryService.getBatchesByProduct(productId)).thenReturn(List.of(inv1));
        ResponseEntity<List<Inventory>> response = inventoryController.getBatches(productId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(productId, response.getBody().get(0).getProductId());
        verify(inventoryService, times(1)).getBatchesByProduct(productId);
    }

    @Test
    void productIdIsNull() {
        Long productId = null;
        assertThrows(ProductNotExistException.class,() -> inventoryController.getBatches(productId));
        verify(inventoryService, times(0)).getBatchesByProduct(productId);
    }

    @Test
    void productIdLessThanOrEqualZero() {
        Long productId = 0L;
        assertThrows(ProductNotExistException.class,() -> inventoryController.getBatches(productId));
        verify(inventoryService, times(0)).getBatchesByProduct(productId);
    }

    @Test
    void productNotExistException() {
        Long productId = 1L;
        when(inventoryService.getBatchesByProduct(productId)).thenReturn(null);
        assertThrows(ProductNotExistException.class,() -> inventoryController.getBatches(productId));
        verify(inventoryService, times(1)).getBatchesByProduct(productId);
    }
    @Test
    void updateInventory() {
        InventoryUpdateRequest req = InventoryUpdateRequest.builder()
                                    .productId(1L)
                            .quantity(10L)
                                        .updatedTime(OffsetDateTime.now())
                                            .build();
        ResponseEntity<Void> response = inventoryController.updateInventory(req);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(inventoryService, times(1)).updateInventory(req);
    }
    @Test
    void getQuantity() {
        Long productId = 1L;
        Long quantity = 20L;
        when(inventoryService.getQuantity(productId)).thenReturn(quantity);
        ResponseEntity<Long> response = inventoryController.getQuantity(productId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(quantity, response.getBody());
        verify(inventoryService, times(1)).getQuantity(productId);
    }

    @Test
    void getQuantityiFproductIdIsNull() {
        Long productId = null;
        assertThrows(ProductNotExistException.class,() -> inventoryController.getQuantity(productId));
        verify(inventoryService, times(0)).getQuantity(productId);
    }

    @Test
    void getQuantityIfProductIdIsZero() {
        Long productId = 0L;
        ProductNotExistException ex = assertThrows(ProductNotExistException.class,() -> inventoryController.getQuantity(productId));
        assertEquals("Product not found with id: 0", ex.getMessage());
        verify(inventoryService, times(0)).getQuantity(productId);
    }
}
