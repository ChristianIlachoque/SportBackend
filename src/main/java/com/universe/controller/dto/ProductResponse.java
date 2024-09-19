package com.universe.controller.dto;

import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        double price,
        String image,
        CategoryResponse category,
        int stock
) {
    public ProductResponse(UUID id, String name, String description, double price, String image, CategoryResponse category) {
        this(id, name, description, price, image, category, 0);
    }
}
