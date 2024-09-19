package com.universe.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record PurchaseResponse(
        UUID id,
        LocalDate date,
        double totalPrice,
        List<PurchaseDetailResponse> purchaseDetailList
) {
}
