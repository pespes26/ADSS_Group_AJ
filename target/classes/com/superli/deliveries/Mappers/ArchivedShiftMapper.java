package com.superli.deliveries.Mappers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.superli.deliveries.dto.HR.ArchivedShiftDTO;
import com.superli.deliveries.domain.core.ShiftType;

public class ArchivedShiftMapper {

    public static ArchivedShiftDTO toDTO(String employeeId, DayOfWeek dayOfWeek, ShiftType shiftType, Date date, int roleId) {
        return new ArchivedShiftDTO(
                employeeId,
                dayOfWeek,
                shiftType,
                date,
                roleId
        );
    }

    public static ArchivedShiftDTO fromResultSet(java.sql.ResultSet rs) throws java.sql.SQLException {
        String employeeId = rs.getString("employee_id");
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(rs.getString("day_of_week"));
        ShiftType shiftType = ShiftType.valueOf(rs.getString("shift_type"));
        Date date = java.sql.Date.valueOf(rs.getString("date"));
        int roleId = rs.getInt("role_id");

        return new ArchivedShiftDTO(employeeId, dayOfWeek, shiftType, date, roleId);
    }

    public static String getDateAsString(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.toString(); // YYYY-MM-DD
    }
}
