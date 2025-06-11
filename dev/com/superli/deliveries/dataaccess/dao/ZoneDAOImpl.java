package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.ZoneDTO;
import com.superli.deliveries.util.Database;
import com.superli.deliveries.exceptions.DataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ZoneDAOImpl implements ZoneDAO {
    private final Connection conn;

    public ZoneDAOImpl() throws SQLException {
        this.conn = Database.getConnection();
    }

    @Override
    public List<ZoneDTO> findAll() throws SQLException {
        List<ZoneDTO> zones = new ArrayList<>();
        String sql = "SELECT * FROM zones";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                zones.add(mapResultSetToZoneDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all zones", e);
        }
        return zones;
    }

    @Override
    public Optional<ZoneDTO> findById(String id) throws SQLException {
        String sql = "SELECT * FROM zones WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToZoneDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding zone by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public ZoneDTO save(ZoneDTO zone) throws SQLException {
        String sql = "INSERT INTO zones (id, name, capacity, active) " +
                    "VALUES (?, ?, ?, ?) " +
                    "ON CONFLICT(id) DO UPDATE SET " +
                    "name = ?, capacity = ?, active = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, zone.getId());
            stmt.setString(2, zone.getName());
            stmt.setFloat(3, zone.getCapacity());
            stmt.setBoolean(4, zone.isActive());
            
            // Values for UPDATE
            stmt.setString(5, zone.getName());
            stmt.setFloat(6, zone.getCapacity());
            stmt.setBoolean(7, zone.isActive());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error saving zone", e);
        }
        
        return zone;
    }

    @Override
    public void deleteById(String id) throws SQLException {
        String sql = "DELETE FROM zones WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting zone with id: " + id, e);
        }
    }

    @Override
    public List<ZoneDTO> findActiveZones() throws SQLException {
        List<ZoneDTO> zones = new ArrayList<>();
        String sql = "SELECT * FROM zones WHERE active = true";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                zones.add(mapResultSetToZoneDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding active zones", e);
        }
        return zones;
    }

    @Override
    public Optional<ZoneDTO> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM zones WHERE name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToZoneDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding zone by name: " + name, e);
        }
        return Optional.empty();
    }

    @Override
    public List<ZoneDTO> findByCapacityRange(float minCapacity, float maxCapacity) throws SQLException {
        List<ZoneDTO> zones = new ArrayList<>();
        String sql = "SELECT * FROM zones WHERE capacity BETWEEN ? AND ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, minCapacity);
            stmt.setFloat(2, maxCapacity);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                zones.add(mapResultSetToZoneDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding zones in capacity range", e);
        }
        return zones;
    }

    private ZoneDTO mapResultSetToZoneDTO(ResultSet rs) throws SQLException {
        ZoneDTO dto = new ZoneDTO();
        dto.setId(rs.getString("id"));
        dto.setName(rs.getString("name"));
        dto.setCapacity(rs.getFloat("capacity"));
        dto.setActive(rs.getBoolean("active"));
        return dto;
    }
} 