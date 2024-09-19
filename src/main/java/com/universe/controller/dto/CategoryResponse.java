package com.universe.controller.dto;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name
) { }
