package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.AvailableShiftDTO;
import com.superli.deliveries.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AvailableShiftDAOImpl implements AvailableShiftDAO {

    private final Connection conn;

    public AvailableShiftDAOImpl() throws SQLException {
        this.conn = Database.getConnection();
    }

    @Override
    public AvailableShiftDTO save(AvailableShiftDTO t) throws SQLException {
        String sql = """
            INSERT OR IGNORE INTO available_shifts (employee_id, day_of_week, shift_type)
            VALUES (?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, t.getEmployeeId());
            ps.setString(2, t.getDayOfWeek());
            ps.setString(3, t.getShiftType());
            ps.executeUpdate();
        }

        return t;
    }

    @Override
    public Optional<AvailableShiftDTO> findById(int id) {
        throw new UnsupportedOperationException("Use findByCompositeKey instead.");
    }

    @Override
    public List<AvailableShiftDTO> findAll() throws SQLException {
        List<AvailableShiftDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM available_shifts";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new AvailableShiftDTO(
                        rs.getInt("employee_id"),
                        rs.getString("day_of_week"),
                        rs.getString("shift_type")
                ));
            }
        }

        return list;
    }

    @Override
    public void deleteById(int id) {
        throw new UnsupportedOperationException("Use deleteByCompositeKey instead.");
    }

    public void deleteByCompositeKey(int employeeId, String dayOfWeek, String shiftType) throws SQLException {
        String sql = "DELETE FROM available_shifts WHERE employee_id = ? AND day_of_week = ? AND shift_type = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setString(2, dayOfWeek);
            ps.setString(3, shiftType);
            ps.executeUpdate();
        }
    }

    public boolean exists(int employeeId, String dayOfWeek, String shiftType) throws SQLException {
        String sql = """
            SELECT 1 FROM available_shifts 
            WHERE employee_id = ? AND day_of_week = ? AND shift_type = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setString(2, dayOfWeek);
            ps.setString(3, shiftType);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
