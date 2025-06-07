package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.ArchivedShiftDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.Date;

public class ArchivedShiftMapper {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public static ArchivedShiftDTO toDTO(Shift shift) {
        if (shift == null) return null;

        return new ArchivedShiftDTO(
                shift.getShiftId(),
                formatter.format(shift.getShiftDate()),
                shift.getShiftType().name(),
                shift.getShiftDay().name(),
                shift.getShiftManager() != null ? Integer.parseInt(shift.getShiftManager().getId()) : -1
        );
    }

    public static Shift fromDTO(ArchivedShiftDTO dto, Employee manager) {
        if (dto == null) return null;

        Date shiftDate = null;
        try {
            shiftDate = formatter.parse(dto.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
