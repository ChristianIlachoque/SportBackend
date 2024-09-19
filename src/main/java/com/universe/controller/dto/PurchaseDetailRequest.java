package com.universe.controller.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PurchaseDetailRequest(
        @NotNull(message = "Product id es requerido")
        UUID product,
        @NotNull(message = "Cantidad es requerido")
        int quantity,
        @NotNull(message = "Precio unitario es requerido")
        double unitPrice,
        double subtotal
) {
}
