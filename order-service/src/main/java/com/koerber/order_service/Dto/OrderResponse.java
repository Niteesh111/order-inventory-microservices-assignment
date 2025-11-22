package com.koerber.order_service.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private Long productId;
    private Long quantity;
    private String orderStatus;
}
