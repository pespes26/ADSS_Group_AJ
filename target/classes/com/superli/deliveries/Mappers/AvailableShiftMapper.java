package com.superli.deliveries.Mappers;

import com.superli.deliveries.dto.*;
import com.superli.deliveries.domain.core.*;

import java.time.DayOfWeek;

public class AvailableShiftMapper {

    public static AvailableShifts fromDTO(AvailableShiftDTO dto) {
        if (dto == null) return null;
        return new AvailableShifts(
                DayOfWeek.valueOf(dto.getDayOfWeek()),
                ShiftType.valueOf(dto.getShiftType())
        );
    }

    public static AvailableShiftDTO toDTO(AvailableShifts shift, int employeeId) {
        if (shift == null) return null;
        return new AvailableShiftDTO(
                employeeId,
                shift.getDay().name(),
                shift.getShift().name()
        );
    }
}
