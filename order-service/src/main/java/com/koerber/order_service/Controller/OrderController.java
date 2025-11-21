package com.koerber.order_service.Controller;

import com.koerber.order_service.Dto.OrderRequest;
import com.koerber.order_service.Entity.Order;
import com.koerber.order_service.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("/create")
    private void createOrder(@RequestBody OrderRequest req) {
         orderService.placeOrder(req);
    }
}
