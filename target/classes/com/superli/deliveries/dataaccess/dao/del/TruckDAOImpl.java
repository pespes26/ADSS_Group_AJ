package com.superli.deliveries.dataaccess.dao.del;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.superli.deliveries.domain.core.LicenseType;
import com.superli.deliveries.dto.del.TruckDTO;
import com.superli.deliveries.exceptions.DataAccessException;
import com.superli.deliveries.util.Database;

public class TruckDAOImpl implements TruckDAO {
    private final Connection conn;

    public TruckDAOImpl() {
        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    @Override
    public List<TruckDTO> findAll() throws SQLException {
        List<TruckDTO> trucks = new ArrayList<>();
        String sql = "SELECT * FROM trucks";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                trucks.add(mapResultSetToTruckDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all trucks", e);
        }
        return trucks;
    }

    @Override
    public Optional<TruckDTO> findById(int id) throws SQLException {
        String sql = "SELECT * FROM trucks WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToTruckDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding truck by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TruckDTO> findByPlate(String plate) throws SQLException {
        String sql = "SELECT * FROM trucks WHERE plate_number = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plate);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToTruckDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding truck by plate: " + plate, e);
        }
        return Optional.empty();
    }

    @Override
    public TruckDTO save(TruckDTO truckDTO) throws SQLException {
        String sql = "INSERT INTO trucks (plate_number, model, net_weight, max_weight, required_license_type, available) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT(plate_number) DO UPDATE SET " +
                    "model = ?, net_weight = ?, max_weight = ?, required_license_type = ?, available = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, truckDTO.getlicensePlate());
            stmt.setString(2, truckDTO.getModel());
            stmt.setFloat(3, truckDTO.getNetWeight());
            stmt.setFloat(4, truckDTO.getMaxWeight());
            stmt.setString(5, truckDTO.getRequiredLicenseType().toString());
            stmt.setBoolean(6, truckDTO.isAvailable());
            
            // Values for UPDATE
            stmt.setString(7, truckDTO.getModel());
            stmt.setFloat(8, truckDTO.getNetWeight());
            stmt.setFloat(9, truckDTO.getMaxWeight());
            stmt.setString(10, truckDTO.getRequiredLicenseType().toString());
            stmt.setBoolean(11, truckDTO.isAvailable());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error saving truck: " + truckDTO.getlicensePlate(), e);
        }
        
        return truckDTO;
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM trucks WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting truck with id: " + id, e);
        }
    }

    @Override
    public void deleteByPlate(String plate) throws SQLException {
        String sql = "DELETE FROM trucks WHERE plate_number = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plate);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting truck with plate: " + plate, e);
        }
    }

    @Override
    public List<TruckDTO> findAvailableTrucks() throws SQLException {
        List<TruckDTO> trucks = new ArrayList<>();
        String sql = "SELECT * FROM trucks WHERE available = true";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                trucks.add(mapResultSetToTruckDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding available trucks", e);
        }
        return trucks;
    }

    @Override
    public List<TruckDTO> findByType(String type) throws SQLException {
        List<TruckDTO> trucks = new ArrayList<>();
        String sql = "SELECT * FROM trucks WHERE model = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                trucks.add(mapResultSetToTruckDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding trucks by type: " + type, e);
        }
        return trucks;
    }

    @Override
    public List<TruckDTO> findByCapacityRange(float minCapacity, float maxCapacity) throws SQLException {
        List<TruckDTO> trucks = new ArrayList<>();
        String sql = "SELECT * FROM trucks WHERE max_weight BETWEEN ? AND ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setFloat(1, minCapacity);
            stmt.setFloat(2, maxCapacity);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                trucks.add(mapResultSetToTruckDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding trucks by capacity range", e);
        }
        return trucks;
    }

    private TruckDTO mapResultSetToTruckDTO(ResultSet rs) throws SQLException {
        TruckDTO dto = new TruckDTO();
        dto.setLicensePlate(rs.getString("license_plate"));
        dto.setModel(rs.getString("model"));
        dto.setNetWeight(rs.getFloat("net_weight"));
        dto.setMaxWeight(rs.getFloat("max_weight"));
        dto.setRequiredLicenseType(LicenseType.valueOf(rs.getString("required_license_type")));
        dto.setAvailable(rs.getBoolean("available"));
        return dto;
    }
} 