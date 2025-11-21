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
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        try {
            Long quantity = restTemplate.getForObject(inventoryUrl + "/{productId}", Long.class, req.getOrder().getProductId());
            if(quantity<req.getOrder().getQuantity()) {
                throw new IllegalStateException("Insufficient inventory for product: " + req.getOrder().getProductId());
            }

            InventoryUpdateRequest inventoryUpdateRequest = InventoryUpdateRequest.builder()
                    .productId(req.getOrder().getProductId())
                    .quantity(req.getOrder().getQuantity())
                    .updateTime(LocalDateTime.now())
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<>(inventoryUpdateRequest, headers);
            restTemplate.postForEntity(inventoryUrl + "/update", entity, Void.class);

            req.getOrder().setInventoryStatus("CREATED");
            orderRepo.save(req.getOrder());

            return req.getOrder();
        } catch (Exception e) {
            req.getOrder().setInventoryStatus("Failed");
            orderRepo.save(req.getOrder());
            log.info("Inventory updation failed due to inventory service error: " + e.getMessage());
        }
        return req.getOrder();
    }
}
