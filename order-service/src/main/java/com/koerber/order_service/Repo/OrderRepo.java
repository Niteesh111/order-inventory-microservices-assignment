package com.koerber.order_service.Repo;

import com.koerber.order_service.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
}
