package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.*;
import com.superli.deliveries.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShiftRoleDAOImpl implements ShiftRoleDAO {

    @Override
    public void save(ShiftRoleDTO dto) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO shift_roles (day_of_week, shift_type, role_id, required_count)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, dto.getDayOfWeek());
            ps.setString(2, dto.getShiftType());
            ps.setInt(3, dto.getRoleId());
            ps.setInt(4, dto.getRequiredCount());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String dayOfWeek, String shiftType, int roleId) throws SQLException {
        String sql = "DELETE FROM shift_roles WHERE day_of_week = ? AND shift_type = ? AND role_id = ?";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, dayOfWeek);
            ps.setString(2, shiftType);
            ps.setInt(3, roleId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<ShiftRoleDTO> findAll() throws SQLException {
        List<ShiftRoleDTO> results = new ArrayList<>();
        String sql = "SELECT * FROM shift_roles";

        try (Statement st = Database.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                results.add(new ShiftRoleDTO(
                        rs.getString("day_of_week"),
                        rs.getString("shift_type"),
                        rs.getInt("role_id"),
                        rs.getInt("required_count")
                ));
            }
        }

        return results;
    }

    @Override
    public ShiftRoleDTO findByKey(String dayOfWeek, String shiftType, int roleId) throws SQLException {
        String sql = "SELECT * FROM shift_roles WHERE day_of_week = ? AND shift_type = ? AND role_id = ?";

        try (PreparedStatement ps = Database.getConnection().prepareStatement(sql)) {
            ps.setString(1, dayOfWeek);
            ps.setString(2, shiftType);
            ps.setInt(3, roleId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ShiftRoleDTO(
                            rs.getString("day_of_week"),
                            rs.getString("shift_type"),
                            rs.getInt("role_id"),
                            rs.getInt("required_count")
                    );
                } else {
                    return null;
                }
            }
        }
    }
}
