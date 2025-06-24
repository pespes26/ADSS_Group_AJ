package com.superli.deliveries.dataaccess.dao.del;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.superli.deliveries.domain.core.TransportStatus;
import com.superli.deliveries.dto.del.TransportDTO;
import com.superli.deliveries.exceptions.DataAccessException;
import com.superli.deliveries.util.Database;

public class TransportDAOImpl implements TransportDAO {
    private final Connection conn;

    public TransportDAOImpl() {
        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error getting database connection", e);
        }
    }

    @Override
    public List<TransportDTO> findAll() throws SQLException {
        List<TransportDTO> transports = new ArrayList<>();
        String sql = "SELECT * FROM transports";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transports.add(mapResultSetToTransportDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all transports", e);
        }
        return transports;
    }

    @Override
    public Optional<TransportDTO> findById(String id) throws SQLException {
        String sql = "SELECT * FROM transports WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToTransportDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding transport by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public TransportDTO save(TransportDTO transport) throws SQLException {
        String sql = "INSERT INTO transports (id, departure_datetime, truck_id, driver_id, " +
                    "origin_site_id, departure_weight, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT(id) DO UPDATE SET " +
                    "departure_datetime = ?, truck_id = ?, driver_id = ?, " +
                    "origin_site_id = ?, departure_weight = ?, status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transport.getTransportId());
            stmt.setString(2, transport.getDepartureDateTime());
            stmt.setString(3, transport.getTruckId());
            stmt.setString(4, transport.getDriverId());
            stmt.setString(5, transport.getOriginSiteId());
            stmt.setFloat(6, transport.getDepartureWeight());
            stmt.setString(7, transport.getStatus().name());
            
            // Values for UPDATE
            stmt.setString(8, transport.getDepartureDateTime());
            stmt.setString(9, transport.getTruckId());
            stmt.setString(10, transport.getDriverId());
            stmt.setString(11, transport.getOriginSiteId());
            stmt.setFloat(12, transport.getDepartureWeight());
            stmt.setString(13, transport.getStatus().name());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error saving transport", e);
        }
        
        return transport;
    }

    @Override
    public void deleteById(String id) throws SQLException {
        String sql = "DELETE FROM transports WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting transport with id: " + id, e);
        }
    }

    @Override
    public List<TransportDTO> findByDriverId(String driverId) throws SQLException {
        List<TransportDTO> transports = new ArrayList<>();
        String sql = "SELECT * FROM transports WHERE driver_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, driverId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transports.add(mapResultSetToTransportDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding transports for driver: " + driverId, e);
        }
        return transports;
    }

    @Override
    public List<TransportDTO> findByTruckPlate(String truckPlate) throws SQLException {
        List<TransportDTO> transports = new ArrayList<>();
        String sql = "SELECT * FROM transports WHERE truck_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, truckPlate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transports.add(mapResultSetToTransportDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding transports for truck: " + truckPlate, e);
        }
        return transports;
    }

    @Override
    public List<TransportDTO> findByStatus(String status) throws SQLException {
        List<TransportDTO> transports = new ArrayList<>();
        String sql = "SELECT * FROM transports WHERE status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transports.add(mapResultSetToTransportDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding transports by status: " + status, e);
        }
        return transports;
    }

    @Override
    public List<TransportDTO> findActiveTransports() throws SQLException {
        List<TransportDTO> transports = new ArrayList<>();
        String sql = """
            SELECT * FROM transports 
            WHERE status IN ('PENDING', 'IN_PROGRESS')
            ORDER BY created_at DESC
        """;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                transports.add(mapResultSetToTransportDTO(rs));
            }
        }
        return transports;
    }

    @Override
    public List<TransportDTO> findActiveTransportsByDriver(String driverId) throws SQLException {
        List<TransportDTO> transports = new ArrayList<>();
        String sql = """
            SELECT * FROM transports 
            WHERE driver_id = ? AND status IN ('PENDING', 'IN_PROGRESS')
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, driverId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transports.add(mapResultSetToTransportDTO(rs));
            }
        }
        return transports;
    }

    private TransportDTO mapResultSetToTransportDTO(ResultSet rs) throws SQLException {
        TransportDTO dto = new TransportDTO();
        dto.setTransportId(rs.getString("id"));
        dto.setDepartureDateTime(rs.getString("departure_datetime"));
        dto.setTruckId(rs.getString("truck_id"));
        dto.setDriverId(rs.getString("driver_id"));
        dto.setOriginSiteId(rs.getString("origin_site_id"));
        dto.setDepartureWeight(rs.getFloat("departure_weight"));
        dto.setStatus(TransportStatus.valueOf(rs.getString("status")));
        return dto;
    }

    @Override
    public void updateStatus(String transportId, String newStatus) throws SQLException {
        String sql = "UPDATE transports SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newStatus);
            stmt.setString(2, transportId.trim());  // מוסיף trim כדי להסיר רווחים מוסתרים
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                System.err.println("No transport found with ID: " + transportId);
            }
        }
    }

} 