package com.superli.deliveries.Mappers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;

import com.superli.deliveries.domain.core.Employee;
import com.superli.deliveries.domain.core.Shift;
import com.superli.deliveries.domain.core.ShiftType;
import com.superli.deliveries.dto.ArchivedShiftDTO;

public class ArchivedShiftMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public static ArchivedShiftDTO toDTO(Shift shift) {
        if (shift == null) return null;

        LocalDate localDate = shift.getShiftDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        return new ArchivedShiftDTO(
                shift.getShiftId(),
                formatter.format(localDate),
                shift.getShiftType().name(),
                shift.getShiftDay().name(),
                shift.getShiftManager() != null ? shift.getShiftManager().getId() : null
        );
    }

    public static Shift fromDTO(ArchivedShiftDTO dto, Employee manager) {
        if (dto == null) return null;

        LocalDate localDate = LocalDate.parse(dto.getDate(), formatter);
        Date shiftDate = Date.from(localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());

        return new Shift(
                dto.getId(),
                shiftDate,
                ShiftType.valueOf(dto.getType()),
                DayOfWeek.valueOf(dto.getDayOfWeek()),
                Collections.emptyList(),     // required roles (not in DTO)
                Collections.emptyMap(),      // employees (not in DTO)
                manager
        );
    }
}
