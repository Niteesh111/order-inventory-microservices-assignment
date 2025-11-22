package com.koerber.order_service.Service;

import com.koerber.order_service.Dto.OrderRequest;
import com.koerber.order_service.Dto.OrderResponse;
import com.koerber.order_service.Entity.Order;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    OrderResponse placeOrder(OrderRequest req);
}
