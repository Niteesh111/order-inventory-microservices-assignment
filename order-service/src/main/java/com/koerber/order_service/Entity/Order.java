package com.koerber.order_service.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "OrderDetail",uniqueConstraints = @UniqueConstraint(columnNames = "productId"))
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long orderId;
    @Column(unique = true)
    private Long productId;
    private Long quantity;
    private LocalDate createdDate;
    private String inventoryStatus;

}
