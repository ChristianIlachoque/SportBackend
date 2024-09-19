package com.universe.controller.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record SaleResponse(
        UUID id,
        LocalDate date,
        double totalPrice,
        List<SaleDetailResponse> saleDetailList
) {
}
