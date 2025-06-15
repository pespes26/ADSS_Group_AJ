package com.superli.deliveries.dataaccess.dao.del;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.superli.deliveries.dto.del.ZoneDTO;
import com.superli.deliveries.exceptions.DataAccessException;
import com.superli.deliveries.util.Database;

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
        String sql = "SELECT * FROM zones WHERE zone_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToZoneDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding zone by zone_id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public ZoneDTO save(ZoneDTO zone) throws SQLException {
        String sql = "INSERT INTO zones (zone_id, name) " +
                    "VALUES (?, ?) " +
                    "ON CONFLICT(zone_id) DO UPDATE SET " +
                    "name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, zone.getId());
            stmt.setString(2, zone.getName());
            stmt.setString(3, zone.getName());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error saving zone", e);
        }
        
        return zone;
    }

    @Override
    public void deleteById(String id) throws SQLException {
        String sql = "DELETE FROM zones WHERE zone_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting zone with zone_id: " + id, e);
        }
    }

    @Override
    public List<ZoneDTO> findActiveZones() throws SQLException {
        // Since we no longer track active status, return all zones
        return findAll();
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
        // Since we no longer track capacity, return empty list
        return new ArrayList<>();
    }

    private ZoneDTO mapResultSetToZoneDTO(ResultSet rs) throws SQLException {
        ZoneDTO dto = new ZoneDTO();
        dto.setId(rs.getString("zone_id"));
        dto.setName(rs.getString("name"));
        return dto;
    }
} 