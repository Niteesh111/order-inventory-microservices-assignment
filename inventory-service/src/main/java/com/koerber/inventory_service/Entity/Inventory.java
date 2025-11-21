package com.koerber.inventory_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "productbatch",uniqueConstraints = @UniqueConstraint(columnNames = "productId"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;
    private Long productId;
    private Long batchId;
    private Long quantity;
    private LocalDate expiryDate;
    private OffsetDateTime updatedTime;


}
