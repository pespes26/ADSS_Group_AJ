package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.*;
import com.superli.deliveries.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShiftRoleDAOImpl implements ShiftRoleDAO {
    private final Connection conn;

    public ShiftRoleDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void save(ShiftRoleDTO dto) throws SQLException {
        String sql = """
        INSERT INTO shift_required_roles 
        (day_of_week, shift_type, role_id, site_id, required_count)
        VALUES (?, ?, ?, ?, ?)
        ON CONFLICT(day_of_week, shift_type, role_id, site_id) 
        DO UPDATE SET required_count = excluded.required_count
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dto.getDayOfWeek().toUpperCase());
            ps.setString(2, dto.getShiftType().toUpperCase());
            ps.setInt(3, dto.getRoleId());
            ps.setInt(4, dto.getSiteId());
            ps.setInt(5, dto.getRequiredCount());
            ps.executeUpdate();
        }
    }


    @Override
    public void delete(String dayOfWeek, String shiftType, int roleId, int siteId) throws SQLException {
        String sql = """
        DELETE FROM shift_required_roles
        WHERE day_of_week = ? AND shift_type = ? AND role_id = ? AND site_id = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dayOfWeek.toUpperCase());
            ps.setString(2, shiftType.toUpperCase());
            ps.setInt(3, roleId);
            ps.setInt(4, siteId);
            ps.executeUpdate();
        }
    }


    @Override
    public List<ShiftRoleDTO> findAll() throws SQLException {
        List<ShiftRoleDTO> results = new ArrayList<>();
        String sql = "SELECT * FROM shift_required_roles";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                results.add(new ShiftRoleDTO(
                        rs.getString("day_of_week"),
                        rs.getString("shift_type"),
                        rs.getInt("role_id"),
                        rs.getInt("site_id"),
                        rs.getInt("required_count")
                ));
            }
        }

        return results;
    }

    @Override
    public ShiftRoleDTO findByKey(String dayOfWeek, String shiftType, int roleId, int siteId) throws SQLException {
        String sql = """
        SELECT * FROM shift_required_roles
        WHERE day_of_week = ? AND shift_type = ? AND role_id = ? AND site_id = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dayOfWeek.toUpperCase());
            ps.setString(2, shiftType.toUpperCase());
            ps.setInt(3, roleId);
            ps.setInt(4, siteId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ShiftRoleDTO(
                            rs.getString("day_of_week"),
                            rs.getString("shift_type"),
                            rs.getInt("role_id"),
                            rs.getInt("required_count"),
                            rs.getInt("site_id")
                    );
                } else {
                    return null;
                }
            }
        }
    }

    @Override
    public Map<Integer, Integer> getRequiredRolesForShift(String dayOfWeek, String shiftType, int siteId) throws SQLException {
        Map<Integer, Integer> requiredRoles = new HashMap<>();

        String sql = """
        SELECT role_id, required_count
        FROM shift_required_roles
        WHERE day_of_week = ? AND shift_type = ? AND site_id = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dayOfWeek.toUpperCase());
            ps.setString(2, shiftType.toUpperCase());
            ps.setInt(3, siteId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int roleId = rs.getInt("role_id");
                    int count = rs.getInt("required_count");
                    requiredRoles.put(roleId, count);
                }
            }
        }

        return requiredRoles;
    }

    @Override
    public void deleteAllForShift(String dayOfWeek, String shiftType, int siteId) throws SQLException {
        String sql = "DELETE FROM shift_required_roles WHERE day_of_week = ? AND shift_type = ? AND site_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dayOfWeek.toUpperCase());
            ps.setString(2, shiftType.toUpperCase());
            ps.setInt(3, siteId);
            ps.executeUpdate();
        }
    }


}