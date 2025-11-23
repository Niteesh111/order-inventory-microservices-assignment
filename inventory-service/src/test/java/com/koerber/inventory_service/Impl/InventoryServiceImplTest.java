package com.koerber.inventory_service.Impl;

import com.koerber.inventory_service.Dto.InventoryUpdateRequest;
import com.koerber.inventory_service.Entity.Inventory;
import com.koerber.inventory_service.ExceptionHandler.ProductNotExistException;
import com.koerber.inventory_service.Repo.InventoryRepo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoBeans;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class InventoryServiceImplTest {
    @MockitoBean
    private InventoryRepo inventoryRepo;

    @Autowired
    private InventoryServiceImpl inventoryService;

    @Test
    void getBatchesByProductIllegalArgumentExceptionTest() {
        assertThrows(IllegalArgumentException.class,() -> inventoryService.getBatchesByProduct(null));

        verifyNoInteractions(inventoryRepo);
    }

    @Test
    void getBatchesByProductTest() {
        Long productId = 1L;
        when(inventoryRepo.findInventoryDetailsByProductId(productId)).thenReturn(new ArrayList<>());
        List<Inventory> result = inventoryService.getBatchesByProduct(productId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(inventoryRepo, times(1)).findInventoryDetailsByProductId(productId);
    }

    @Test
    void getBatchesByProduct_returnsListSortedByExpiryDate() {
        Long productId = 1L;

        Inventory inv1 = new Inventory();
        inv1.setInventoryId(1L);
        inv1.setProductId(productId);
        inv1.setQuantity(10L);
        inv1.setExpiryDate(LocalDate.of(2026, 1, 10));

        Inventory inv2 = new Inventory();
        inv2.setInventoryId(2L);
        inv2.setProductId(productId);
        inv2.setQuantity(5L);
        inv2.setExpiryDate(LocalDate.of(2025, 12, 1));

        Inventory inv3 = new Inventory();
        inv3.setInventoryId(3L);
        inv3.setProductId(productId);
        inv3.setQuantity(8L);
        inv3.setExpiryDate(LocalDate.of(2026, 3, 5));
        when(inventoryRepo.findInventoryDetailsByProductId(productId)).thenReturn(List.of(inv1, inv2, inv3));

        List<Inventory> result = inventoryService.getBatchesByProduct(productId);

        assertEquals(3, result.size());
        assertEquals(inv2.getExpiryDate(), result.get(0).getExpiryDate());
        assertEquals(inv1.getExpiryDate(), result.get(1).getExpiryDate());
        assertEquals(inv3.getExpiryDate(), result.get(2).getExpiryDate());
        verify(inventoryRepo, times(1)).findInventoryDetailsByProductId(productId);
    }

    @Test
    void updateInventoryIllegalArgumentExceptionTest() {
        Long productId = 1L;
        InventoryUpdateRequest request = InventoryUpdateRequest.builder()
                                        .productId(productId)
                                            .quantity(5L)
                .updatedTime(OffsetDateTime.now())
                                                .build();

        when(inventoryRepo.findInventoryDetailsByProductId(productId)).thenReturn(new ArrayList<>());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,() -> inventoryService.updateInventory(request));
        assertTrue(ex.getMessage().contains("No inventory found for product"));
        verify(inventoryRepo, times(1)).findInventoryDetailsByProductId(productId);
        verify(inventoryRepo, never()).saveAll(anyList());
    }

    @Test
    void updateInventoryTest() {
        Long productId = 1L;

        Inventory inv = new Inventory();
        inv.setInventoryId(1L);
        inv.setProductId(productId);
        inv.setQuantity(10L);
        inv.setExpiryDate(LocalDate.of(2025, 12, 1));

        InventoryUpdateRequest request = InventoryUpdateRequest.builder()
                                        .productId(productId)
                                        .quantity(4L)
                                        .updatedTime(OffsetDateTime.now())
                                        .build();

        when(inventoryRepo.findInventoryDetailsByProductId(productId)).thenReturn(List.of(inv));
        when(inventoryRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        inventoryService.updateInventory(request);
        ArgumentCaptor<List<Inventory>> captor = ArgumentCaptor.forClass(List.class);
        verify(inventoryRepo, times(1)).saveAll(captor.capture());

        List<Inventory> savedList = captor.getValue();
        assertEquals(1, savedList.size());
        Inventory savedInv = savedList.get(0);
        assertEquals(6L, savedInv.getQuantity());
        assertNotNull(savedInv.getUpdatedTime());
    }

    @Test
    void updateInventoryTesrt() {
        Long productId = 1L;

        Inventory inv1 = new Inventory();
        inv1.setInventoryId(1L);
        inv1.setProductId(productId);
        inv1.setQuantity(3L);
        inv1.setExpiryDate(LocalDate.of(2025, 12, 1));

        Inventory inv2 = new Inventory();
        inv2.setInventoryId(2L);
        inv2.setProductId(productId);
        inv2.setQuantity(5L);
        inv2.setExpiryDate(LocalDate.of(2026, 1, 10));

        InventoryUpdateRequest request = InventoryUpdateRequest.builder()
                                        .productId(productId)
                                       .quantity(6L)
                                         .updatedTime(OffsetDateTime.now())
                                        .build();

        when(inventoryRepo.findInventoryDetailsByProductId(productId)).thenReturn(List.of(inv2, inv1));
        when(inventoryRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        inventoryService.updateInventory(request);
        ArgumentCaptor<List<Inventory>> captor = ArgumentCaptor.forClass(List.class);
        verify(inventoryRepo, times(1)).saveAll(captor.capture());
        List<Inventory> savedList = captor.getValue();
        assertEquals(2, savedList.size());

        Inventory savedInv1 = savedList.stream()
                            .filter(i -> i.getInventoryId().equals(inv1.getInventoryId()))
                            .findFirst()
                            .orElseThrow();
        Inventory savedInv2 = savedList.stream()
                            .filter(i -> i.getInventoryId().equals(inv2.getInventoryId()))
                            .findFirst()
                            .orElseThrow();

        assertEquals(0L, savedInv1.getQuantity());
        assertEquals(2L, savedInv2.getQuantity());
        assertNotNull(savedInv1.getUpdatedTime());
        assertNotNull(savedInv2.getUpdatedTime());
    }

    @Test
    void getQuantityRest() {
        Long productId = 1L;
        when(inventoryRepo.getQuantityByProductId(productId)).thenReturn(Optional.of(15L));
        Long quantity = inventoryService.getQuantity(productId);
        assertEquals(15L, quantity);
        verify(inventoryRepo, times(1)).getQuantityByProductId(productId);
    }

    @Test
    void getQuantityProductNotExistExceptionTesdtr() {
        Long productId = 1L;
        when(inventoryRepo.getQuantityByProductId(productId)).thenReturn(Optional.of(0L));
        assertThrows(ProductNotExistException.class,() -> inventoryService.getQuantity(productId));

        verify(inventoryRepo, times(1)).getQuantityByProductId(productId);
    }

    @Test
    void getQuantityTestProductNotExistException() {
        Long productId = 1L;
        when(inventoryRepo.getQuantityByProductId(productId)).thenReturn(Optional.empty());
        assertThrows(ProductNotExistException.class,() -> inventoryService.getQuantity(productId));
        verify(inventoryRepo, times(1)).getQuantityByProductId(productId);
    }
}
