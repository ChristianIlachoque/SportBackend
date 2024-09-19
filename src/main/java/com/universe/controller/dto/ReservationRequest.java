package com.universe.controller.dto;

import com.universe.persistence.entity.ReservationStateEnum;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReservationRequest(
        LocalDate date,
        LocalTime startHour,
        LocalTime endHour,
        double price,
        ReservationStateEnum state,
        String clientName,
        String clientLastName,
        String dni,
        String phone,
        UUID sportSpaceId
) {
}
