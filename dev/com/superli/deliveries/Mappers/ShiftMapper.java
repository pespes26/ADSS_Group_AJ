package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.*;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ShiftMapper {

    public static Shift fromDTO(ShiftDTO dto, List<Role> requiredRoles, Map<Employee, Role> assignedEmployees, Employee manager) {
        if (dto == null) return null;

        return new Shift(
                dto.getId(),
                dto.getShiftDate(),
                ShiftType.valueOf(dto.getShiftType()),
                DayOfWeek.valueOf(dto.getShiftDay()),
                requiredRoles,
                assignedEmployees,
                manager
        );
    }

    public static ShiftDTO toDTO(Shift shift) {
        if (shift == null) return null;

        Map<Integer, Integer> assignedMap = new HashMap<>();
        for (Map.Entry<Employee, Role> entry : shift.getShiftEmployees().entrySet()) {
            assignedMap.put(Integer.parseInt(entry.getKey().getId()), Integer.valueOf(entry.getValue().getRoleName()));
        }

        List<Integer> requiredIds = new ArrayList<>();
        for (Role role : shift.getShiftRequiredRoles()) {
            requiredIds.add(Integer.valueOf(role.getRoleName())); // placeholder, אין ID במחלקת Role
        }

        return new ShiftDTO(
                shift.getShiftId(),
                shift.getShiftDate(),
                shift.getShiftType().name(),
                shift.getShiftDay().name(),
                requiredIds,
                assignedMap,
                shift.getShiftManager() != null ? Integer.parseInt(shift.getShiftManager().getId()) : null
        );
    }
}
