package com.koerber.order_service.Impl;

import com.koerber.order_service.Dto.InventoryUpdateRequest;
import com.koerber.order_service.Dto.OrderRequest;
import com.koerber.order_service.Entity.Order;
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
    public Order placeOrder(OrderRequest req) {
        req.getOrder().setCreatedDate(LocalDate.now());
        Long quantity;
        try {
            log.info("Fetching inventory for product: {}", req.getOrder().getProductId());
            quantity = restTemplate.getForObject(inventoryUrl + "/{productId}", Long.class, req.getOrder().getProductId());
            log.info("Available inventory for product {}: {}", req.getOrder().getProductId(), quantity);
            if (quantity == null) {
                req.getOrder().setInventoryStatus("Failed");
                orderRepo.save(req.getOrder());
                log.info("Inventory service returned null for product: {}", req.getOrder().getProductId());
                return req.getOrder();
            }
        } catch (RestClientException e) {
            req.getOrder().setInventoryStatus("Failed");
            orderRepo.save(req.getOrder());
            log.info("Failed to fetch inventory for product {}: {}", req.getOrder().getProductId(), e.getMessage());
            return req.getOrder();
        }

        if (quantity < req.getOrder().getQuantity()) {
            throw new IllegalStateException("Insufficient inventory for product: " + req.getOrder().getProductId());
        }

        InventoryUpdateRequest inventoryUpdateRequest = InventoryUpdateRequest.builder()
                                                        .productId(req.getOrder().getProductId())
                                                        .quantity(req.getOrder().getQuantity())
                                                        .updatedTime(OffsetDateTime.now())
                                                        .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> entity = new HttpEntity<>(inventoryUpdateRequest, headers);
        try {
            log.info("Updating inventory for product: {}", req.getOrder().getProductId());
            restTemplate.postForEntity(inventoryUrl + "/update", entity, Void.class);
            log.info("Inventory updated successfully for product: {}", req.getOrder().getProductId());
        } catch (RestClientException e) {
            req.getOrder().setInventoryStatus("Failed");
            orderRepo.save(req.getOrder());
            log.info("Inventory updation failed due to inventory service error: {}", e.getMessage());
            return req.getOrder();
        }

        req.getOrder().setInventoryStatus("CREATED");
        orderRepo.save(req.getOrder());
        log.info("Order placed successfully for product: {}", req.getOrder().getProductId());
        return req.getOrder();
    }
}
