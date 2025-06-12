package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.ShiftType;
import com.superli.deliveries.dto.HR.ShiftRoleDTO;

import java.time.DayOfWeek;
import java.util.Map;

public class ShiftRoleMapper {

    public static ShiftRoleDTO toDTO(DayOfWeek day, ShiftType shiftType, int roleId, int requiredCount) {
        return new ShiftRoleDTO(
                day.name(),
                shiftType.name(),
                roleId,
                requiredCount
        );
    }

}
