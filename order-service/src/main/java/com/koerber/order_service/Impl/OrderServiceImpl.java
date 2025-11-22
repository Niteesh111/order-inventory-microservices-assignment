package com.koerber.order_service.Impl;

import com.koerber.order_service.Dto.InventoryUpdateRequest;
import com.koerber.order_service.Dto.OrderRequest;
import com.koerber.order_service.Dto.OrderResponse;
import com.koerber.order_service.Entity.Order;
import com.koerber.order_service.ExceptionHandler.InsufficientInventoryException;
import com.koerber.order_service.ExceptionHandler.InventoryServiceException;
import com.koerber.order_service.ExceptionHandler.ProductNotExistException;
import com.koerber.order_service.Repo.OrderRepo;
import com.koerber.order_service.Service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepo orderRepo;
    @Autowired
    RestTemplate restTemplate;

    @Value("${app.inventory.inventoryUrl}")
    private String inventoryUrl;

    @Override
    @Transactional
    public OrderResponse placeOrder(OrderRequest req) {
        Order order = req.getOrder();
        order.setCreatedDate(LocalDate.now());
        Long availableQuantity = 0L;
        //Fetch inventory
        availableQuantity = getQuantityFromInventory(order.getProductId());

        //Validate inventory amount
        handleInsufficientInventory(availableQuantity, order);

        //Inventory update
        handleInventoryUpdate(order);

        //Persist order in DB
        order.setInventoryStatus("UPDATED");
        Order saved = orderRepo.save(order);
        log.info("Order placed successfully. orderId={}, productId={}", saved.getOrderId(), saved.getProductId());

        return OrderResponse.builder()
                .orderId(saved.getOrderId())
                .productId(saved.getProductId())
                .quantity(saved.getQuantity())
                .orderStatus("Order placed succesfully")
                .build();
    }

    private void handleInventoryUpdate(Order order) {
        InventoryUpdateRequest inventoryUpdateRequest = InventoryUpdateRequest.builder()
                                                        .productId(order.getProductId())
                                                            .quantity(order.getQuantity())
                                                        .updatedTime(OffsetDateTime.now())
                                                        .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<InventoryUpdateRequest> entity = new HttpEntity<>(inventoryUpdateRequest, headers);

        //Inventory update
        try {
            log.info("Updating inventory for product: {}", order.getProductId());
            restTemplate.postForEntity(inventoryUrl + "/update", entity, Void.class);
            log.info("Inventory updated successfully for product: {}", order.getProductId());
        } catch (RestClientException e) {
            order.setInventoryStatus("FAILED");
            orderRepo.save(order);
            log.warn("Inventory update failed for product {}: {}", order.getProductId(), e.getMessage());

            throw new InventoryServiceException("Failed to update inventory for product " + order.getProductId(), e);
        }
    }

    private void handleInsufficientInventory(Long availableQuantity, Order order) {
        if (availableQuantity == null || availableQuantity < order.getQuantity()) {
            order.setInventoryStatus("FAILED");
            log.info("Insufficient inventory for product {}. Required: {}, Available: {}",order.getProductId(), order.getQuantity(), availableQuantity);


            throw new InsufficientInventoryException("Insufficient inventory for product " + order.getProductId());
        }
    }

    private Long getQuantityFromInventory(Long productId) {
        try {
            return restTemplate.getForObject(inventoryUrl + "/quantity/{productId}", Long.class, productId);
        } catch (ProductNotExistException | RestClientException e) {
            if (e.getMessage().contains("Product not found with id")) {
                log.warn("Product not available in inventry {}: {}", productId, e.getMessage());
                throw new ProductNotExistException("Product not available currently.", e);
            } else {
                log.warn("Failed to fetch inventory for product {}: {}", productId, e.getMessage());
                throw new InventoryServiceException("Inventory service unavailable. Please try again later.", e);
            }
        }
    }
}
