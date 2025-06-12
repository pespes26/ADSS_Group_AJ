package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.RoleDTO;
import com.superli.deliveries.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleDAOImpl implements RoleDAO {
    private final Connection conn;

    public RoleDAOImpl(Connection conn) {
        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    @Override
    public RoleDTO save(RoleDTO role) throws SQLException {
        String sql = """
            INSERT INTO roles (name)
            VALUES (?)
            ON CONFLICT(name) DO NOTHING
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, role.getName());
            ps.executeUpdate();

            // Try to get the generated ID if it's new
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    role.setId(rs.getInt(1));
                } else {
                    // Role already exists, fetch its ID
                    Optional<RoleDTO> existing = findByName(role.getName());
                    existing.ifPresent(value -> role.setId(value.getId()));
                }
            }
        }
        return role;
    }

    @Override
    public Optional<RoleDTO> findById(int id) throws SQLException {
        String sql = "SELECT * FROM roles WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(fromResultSet(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<RoleDTO> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM roles WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(fromResultSet(rs));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<RoleDTO> findAll() throws SQLException {
        List<RoleDTO> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles";

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                roles.add(fromResultSet(rs));
            }
        }
        return roles;
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM roles WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Utility method
    private RoleDTO fromResultSet(ResultSet rs) throws SQLException {
        return new RoleDTO(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
