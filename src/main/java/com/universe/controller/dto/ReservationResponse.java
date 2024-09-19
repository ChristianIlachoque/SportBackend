package com.universe.controller.dto;

import com.universe.persistence.entity.ReservationStateEnum;
import com.universe.persistence.entity.SportSpaceEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        LocalDate date,
        LocalTime startHour,
        LocalTime endHour,
        double price,
        ReservationStateEnum state,
        String clientName,
        String clientLastName,
        String dni,
        String phone,
        SportSpaceEntity sportSpace
) {
}
