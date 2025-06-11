package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.TransportDTO;
import com.superli.deliveries.util.Database;
import com.superli.deliveries.exceptions.DataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        String sql = "INSERT INTO transports (id, driver_id, truck_plate, status, start_date, end_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT(id) DO UPDATE SET " +
                    "driver_id = ?, truck_plate = ?, status = ?, start_date = ?, end_date = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transport.getTransportId());
            stmt.setString(2, transport.getDriverId());
            stmt.setString(3, transport.getTruckId());
            stmt.setString(4, transport.getStatus());
            stmt.setString(5, transport.getDepartureDateTime());
            stmt.setString(6, null); // end_date, adjust as needed
            // Values for UPDATE
            stmt.setString(7, transport.getDriverId());
            stmt.setString(8, transport.getTruckId());
            stmt.setString(9, transport.getStatus());
            stmt.setString(10, transport.getDepartureDateTime());
            stmt.setString(11, null); // end_date, adjust as needed
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
        String sql = "SELECT * FROM transports WHERE truck_plate = ?";

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
            throw new DataAccessException("Error finding transports with status: " + status, e);
        }
        return transports;
    }

    @Override
    public List<TransportDTO> findActiveTransports() throws SQLException {
        List<TransportDTO> transports = new ArrayList<>();
        String sql = "SELECT * FROM transports WHERE status IN ('PENDING', 'IN_PROGRESS')";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                transports.add(mapResultSetToTransportDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding active transports", e);
        }
        return transports;
    }

    private TransportDTO mapResultSetToTransportDTO(ResultSet rs) throws SQLException {
        TransportDTO dto = new TransportDTO();
        dto.setTransportId(rs.getString("id"));
        dto.setDriverId(rs.getString("driver_id"));
        dto.setTruckId(rs.getString("truck_plate"));
        dto.setStatus(rs.getString("status"));
        dto.setDepartureDateTime(rs.getString("start_date"));
        return dto;
    }
} 