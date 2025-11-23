package com.koerber.order_service.Impl;

import com.koerber.order_service.Dto.OrderRequest;
import com.koerber.order_service.Dto.OrderResponse;
import com.koerber.order_service.Entity.Order;
import com.koerber.order_service.ExceptionHandler.InsufficientInventoryException;
import com.koerber.order_service.ExceptionHandler.InventoryServiceException;
import com.koerber.order_service.ExceptionHandler.ProductNotExistException;
import com.koerber.order_service.Repo.OrderRepo;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {
    @Autowired
    private OrderServiceImpl orderService;
    @MockitoBean
    private OrderRepo orderRepo;
    @MockitoBean
    private RestTemplate restTemplate;

    @Test
    void orderSuccessTestr() {
        Long productId = 1L;
        Long requestedQuantity = 5L;

        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(requestedQuantity);

        OrderRequest req = new OrderRequest();
        req.setOrder(order);
        String url = "http://localhost:8082/inventory/quantity/{productId}";
        when(restTemplate.getForObject(url, Long.class, productId)).thenReturn(10L);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class))).thenReturn(ResponseEntity.noContent().build());

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepo.save(orderCaptor.capture())).thenReturn(order);

        OrderResponse response = orderService.placeOrder(req);
        assertEquals(null, response.getOrderId());
        assertEquals("Order placed succesfully", response.getOrderStatus());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Long.class), eq(productId));
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
        verify(orderRepo, times(1)).save(orderCaptor.capture());
        verify(orderRepo, atLeastOnce()).save(orderCaptor.capture());
        Order saved = orderCaptor.getValue();
        assertEquals("UPDATED", saved.getInventoryStatus());
        assertEquals(LocalDate.now(), saved.getCreatedDate());
    }

    @Test
    void insufficientInventoryExceptionTestr() {
        Long productId = 1L;
        Long requestedQuantity = 10L;

        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(requestedQuantity);

        OrderRequest req = new OrderRequest();
        req.setOrder(order);
        when(restTemplate.getForObject(anyString(), eq(Long.class), eq(productId))).thenReturn(3L);

        InsufficientInventoryException ex = assertThrows(InsufficientInventoryException.class,() -> orderService.placeOrder(req));
        assertTrue(ex.getMessage().contains("Insufficient inventory for product " + productId));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Long.class), eq(productId));
        verify(restTemplate, never()).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
        verify(orderRepo, never()).save(any(Order.class));
        assertEquals("FAILED", order.getInventoryStatus());
    }

    @Test
    void productNotExistExceptionTestr() {
        Long productId = 1L;

        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(5L);

        OrderRequest req = new OrderRequest();
        req.setOrder(order);
        when(restTemplate.getForObject(anyString(), eq(Long.class), eq(productId))).thenThrow(new RestClientException("Product not found with id: " + productId));
        ProductNotExistException ex = assertThrows(ProductNotExistException.class,() -> orderService.placeOrder(req));
        assertTrue(ex.getMessage().contains("Product not available currently"));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Long.class), eq(productId));
    }

    @Test
    void inventoryServiceExceptionTestr() {
        Long productId = 1L;

        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(5L);

        OrderRequest req = new OrderRequest();
        req.setOrder(order);
        when(restTemplate.getForObject(anyString(), eq(Long.class), eq(productId))).thenThrow(new RestClientException("Connection timeout"));

        InventoryServiceException ex = assertThrows(InventoryServiceException.class,() -> orderService.placeOrder(req));
        assertTrue(ex.getMessage().contains("Inventory service unavailable"));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Long.class), eq(productId));
    }


    @Test
    void inventoryServiceExceptionTestrNegativeTest() {
        Long productId = 1L;
        Long requestedQuantity = 5L;

        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(requestedQuantity);

        OrderRequest req = new OrderRequest();
        req.setOrder(order);
        when(restTemplate.getForObject(anyString(), eq(Long.class), eq(productId))).thenReturn(10L);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Void.class))).thenThrow(new RestClientException("Inventory service down"));
        when(orderRepo.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryServiceException ex = assertThrows(InventoryServiceException.class,() -> orderService.placeOrder(req));
        assertTrue(ex.getMessage().contains("Failed to update inventory for product " + productId));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(Long.class), eq(productId));
        verify(restTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
        verify(orderRepo, times(1)).save(any(Order.class));
        assertEquals("FAILED", order.getInventoryStatus());
    }

}
