package com.koerber.order_service.Controller;

import com.koerber.order_service.Dto.OrderRequest;
import com.koerber.order_service.Dto.OrderResponse;
import com.koerber.order_service.Entity.Order;
import com.koerber.order_service.Service.OrderService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderControllerTest {
    @MockitoBean
    OrderService orderService;
    @Autowired
    OrderController orderController;

    @Test
    public void createOrderTest(){
        OrderRequest req = new OrderRequest();
        req.setCustromerId("KOR-25200");

        Order order = new Order();
        order.setQuantity(30L);
        order.setProductId(100L);

        req.setOrder(order);

        OrderResponse res = new OrderResponse();
        res.setOrderStatus("CREATED");
        res.setOrderId(100L);
        res.setQuantity(30L);
        res.setProductId(100L);

        when(orderService.placeOrder(req)).thenReturn(res);

        ResponseEntity<OrderResponse> orderResponse = orderController.createOrder(req);
        assertEquals(HttpStatus.CREATED, orderResponse.getStatusCode());
    }

}
