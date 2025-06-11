package com.superli.deliveries.Mappers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.superli.deliveries.domain.core.Shift;
import com.superli.deliveries.domain.core.ShiftType;
import com.superli.deliveries.dto.ShiftDTO;

public class ShiftMapper {
    public static Shift toDomain(ShiftDTO dto) {
        if (dto == null) return null;

        // Create a new Shift with default values since ShiftDTO doesn't have all the fields
        return new Shift(
                dto.getId(),
                new Date(dto.getShiftDate().toEpochDay()),
                ShiftType.MORNING, // Default to MORNING since DTO doesn't have shift type
                DayOfWeek.MONDAY, // Default to MONDAY since DTO doesn't have shift day
                new ArrayList<>(), // Empty required roles list
                new HashMap<>(), // Empty assigned employees map
                null // No shift manager
        );
    }

    public static ShiftDTO toDTO(Shift shift) {
        if (shift == null) return null;

        // Convert Date to LocalDate
        LocalDate localDate = shift.getShiftDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

        // Default to 9 AM - 5 PM for start and end times
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);

        // Determine status based on shift state
        String status = shift.isPastShift() ? "COMPLETED" : 
                       shift.isShiftFullyAssigned() ? "FULLY_ASSIGNED" : "PENDING";

        return new ShiftDTO(
                shift.getShiftId(),
                "0", // No employee ID in the domain model
                localDate,
                startTime,
                endTime,
                status
        );
    }
}
