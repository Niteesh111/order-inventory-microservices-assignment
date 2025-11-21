package com.koerber.order_service.Dto;

import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@Builder
public class InventoryUpdateRequest {
    private Long productId;
    private Long quantity;
    private OffsetDateTime updatedTime;
}
