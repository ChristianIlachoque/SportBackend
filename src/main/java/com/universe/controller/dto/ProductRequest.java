package com.universe.controller.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductRequest(
        @NotNull(message = "Nombre de producto es requerido")
        String name,
        String description,
        @NotNull(message = "Precio de producto es requerido")
        double price,
        String image,
        UUID categoryId
){}