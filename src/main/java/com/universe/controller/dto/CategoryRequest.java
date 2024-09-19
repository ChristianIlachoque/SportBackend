package com.universe.controller.dto;

import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        @NotNull(message = "Nombre de categoria es requerido")
        String name
) {
}
