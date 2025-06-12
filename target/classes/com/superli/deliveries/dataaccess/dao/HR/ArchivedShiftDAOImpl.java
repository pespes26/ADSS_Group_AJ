package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.ArchivedShiftDTO;
import com.superli.deliveries.domain.core.ShiftType;

import java.sql.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArchivedShiftDAOImpl implements ArchivedShiftDAO {

    private final Connection connection;

    public ArchivedShiftDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<ArchivedShiftDTO> findAllArchivedShifts() throws SQLException {
        List<ArchivedShiftDTO> shifts = new ArrayList<>();

        String sql = "SELECT employee_id, day_of_week, shift_type, date, role_id FROM archived_shifts";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String employeeId = rs.getString("employee_id");
                DayOfWeek dayOfWeek = DayOfWeek.valueOf(rs.getString("day_of_week"));
                ShiftType shiftType = ShiftType.valueOf(rs.getString("shift_type"));
                Date date = java.sql.Date.valueOf(rs.getString("date")); // <-- תיקון
                int roleId = rs.getInt("role_id");

                ArchivedShiftDTO shift = new ArchivedShiftDTO(employeeId, dayOfWeek, shiftType, date, roleId);
                shifts.add(shift);
            }
        }

        return shifts;
    }
}
