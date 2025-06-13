package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.EmployeeDTO;
import com.superli.deliveries.dto.HR.ShiftDTO;
import com.superli.deliveries.exceptions.DataAccessException;
import com.superli.deliveries.util.Database;
import com.superli.deliveries.domain.core.ShiftType;

import java.sql.*;
import java.time.DayOfWeek;
import java.util.*;

public class ShiftDAOImpl implements ShiftDAO {
    private final Connection conn;

    public ShiftDAOImpl() {
        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    public ShiftDAOImpl(Connection testConnection) {
        this.conn = testConnection;
    }

    @Override
    public List<ShiftDTO> findAll() throws SQLException {
        List<ShiftDTO> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shifts";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                shifts.add(mapResultSetToShiftDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all shifts", e);
        }
        return shifts;
    }

    @Override
    public void save(ShiftDTO shift) throws SQLException {
        String sql = "INSERT INTO shift_assignments (employee_id, day_of_week, shift_type, date, role_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Map.Entry<Integer, Integer> entry : shift.getAssignedEmployees().entrySet()) {
                ps.setString(1, String.valueOf(entry.getKey())); // employee_id
                ps.setString(2, shift.getShiftDay().toUpperCase());
                ps.setString(3, shift.getShiftType().toUpperCase());
                ps.setString(4, shift.getShiftDate().toString());
                ps.setInt(5, entry.getValue()); // role_id
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }


    public void saveAssignment(String employeeId, String dayOfWeek, String shiftType, String date, int roleId) throws SQLException {
        String sql = "INSERT INTO shift_assignments (employee_id, day_of_week, shift_type, date, role_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ps.setString(2, dayOfWeek);
            ps.setString(3, shiftType.toUpperCase());
            ps.setString(4, date);
            ps.setInt(5, roleId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<ShiftDTO> findByDayOfWeekAndShiftType(DayOfWeek day, ShiftType st) throws SQLException {
        List<ShiftDTO> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shifts WHERE shift_day = ? AND shift_type = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, day.name());
            ps.setString(2, st.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                shifts.add(mapResultSetToShiftDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding shifts by day and type", e);
        }
        return shifts;
    }

    @Override
    public void clearAllAndArchive() throws SQLException {
        conn.setAutoCommit(false);
        try {
            String archiveSql = "INSERT INTO archived_shifts SELECT * FROM shifts";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(archiveSql);
            }

            String deleteSql = "DELETE FROM shifts";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(deleteSql);
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new SQLException("Failed to clear and archive shifts", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public List<ShiftDTO> findByEmployeeId(String employeeId) throws SQLException {
        List<ShiftDTO> shifts = new ArrayList<>();
        String sql = "SELECT s.* FROM shifts s " +
                     "JOIN shift_assignments sa ON s.id = sa.shift_id " +
                     "WHERE sa.employee_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                shifts.add(mapResultSetToShiftDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding shifts by employee id", e);
        }
        return shifts;
    }

    private ShiftDTO mapResultSetToShiftDTO(ResultSet rs) throws SQLException {
        ShiftDTO dto = new ShiftDTO();
        dto.setId(rs.getInt("id"));
        dto.setShiftDate(rs.getDate("shift_date"));
        dto.setShiftType(rs.getString("shift_type"));
        dto.setShiftDay(rs.getString("shift_day"));
        // requiredRoleIds and assignedEmployees would need to be populated from other tables if they exist
        return dto;
    }


    @Override
    public List<String> getEmployeeIdsByRoleAndDate(String roleName, String date) throws SQLException {
        String sql = """
        SELECT e.id
        FROM shift_assignments sa
        JOIN employees e ON sa.employee_id = e.id
        JOIN roles r ON sa.role_id = r.id
        WHERE sa.date = ? AND r.name = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, date);
            ps.setString(2, roleName);

            ResultSet rs = ps.executeQuery();
            List<String> employeeIds = new ArrayList<>();

            while (rs.next()) {
                employeeIds.add(rs.getString("id"));
            }

            return employeeIds;
        }
    }

}
