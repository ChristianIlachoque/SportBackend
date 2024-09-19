package com.universe.controller.dto;

import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

public record PurchaseRequest(
        LocalDate date,
        double totalPrice,
        @Validated
        List<PurchaseDetailRequest> purchaseDetailList
) {
}
