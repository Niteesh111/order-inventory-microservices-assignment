package com.koerber.order_service.Service;

import com.koerber.order_service.Dto.OrderRequest;
import com.koerber.order_service.Entity.Order;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    Order placeOrder(OrderRequest req);
}
