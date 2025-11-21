package com.koerber.order_service.Dto;

import com.koerber.order_service.Entity.Order;
import lombok.Data;

@Data
public class OrderRequest {
    private String custromerId;
    private Order order;
}
