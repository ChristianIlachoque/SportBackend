package com.universe.controller.dto;

import java.util.UUID;

public record SaleDetailResponse(
        UUID id,
        ProductResponse product,
        int quantity,
        double unitPrice,
        double subtotal
) {
}
