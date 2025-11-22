package com.koerber.order_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateRequest {
    private Long productId;
    private Long quantity;
    private OffsetDateTime updatedTime;
}
