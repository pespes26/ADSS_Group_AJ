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
    public Optional<TruckDTO> findById(String plateNum) throws SQLException {
        String sql = "SELECT * FROM trucks WHERE license_plate = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plateNum);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToTruckDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding truck by plate number: " + plateNum, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<TruckDTO> findByPlate(String plate) throws SQLException {
        return findById(plate);
    }

    @Override
    public TruckDTO save(TruckDTO truck) throws SQLException {
        String sql = "INSERT INTO trucks (license_plate, model, net_weight, max_weight, license_type, available) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT(license_plate) DO UPDATE SET " +
                    "model = ?, net_weight = ?, max_weight = ?, license_type = ?, available = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, truck.getlicensePlate());
            stmt.setString(2, truck.getModel());
            stmt.setFloat(3, truck.getNetWeight());
            stmt.setFloat(4, truck.getMaxWeight());
            stmt.setString(5, truck.getRequiredLicenseType().toString());
            stmt.setBoolean(6, truck.isAvailable());
            
            // Values for UPDATE
            stmt.setString(7, truck.getModel());
            stmt.setFloat(8, truck.getNetWeight());
            stmt.setFloat(9, truck.getMaxWeight());
            stmt.setString(10, truck.getRequiredLicenseType().toString());
            stmt.setBoolean(11, truck.isAvailable());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error saving truck", e);
        }
        
        return truck;
    }

    @Override
    public void deleteById(String plateNum) throws SQLException {
        String sql = "DELETE FROM trucks WHERE license_plate = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, plateNum);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting truck with plate number: " + plateNum, e);
        }
    }

    @Override
    public void deleteByPlate(String plate) throws SQLException {
        deleteById(plate);
    }

    @Override
    public void updateTruckAvailability(String plateNum, boolean available) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // First verify the truck exists
            String checkSql = "SELECT license_plate FROM trucks WHERE license_plate = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, plateNum);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    throw new DataAccessException("Truck not found with plate number: " + plateNum);
                }
            }

            // Then update the availability
            String updateSql = "UPDATE trucks SET available = ? WHERE license_plate = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, available ? 1 : 0);  // Convert boolean to int for SQLite
                updateStmt.setString(2, plateNum);
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("Failed to update availability for truck: " + plateNum);
                }
            }

            // Verify the update
            String verifySql = "SELECT available FROM trucks WHERE license_plate = ?";
            try (PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {
                verifyStmt.setString(1, plateNum);
                ResultSet rs = verifyStmt.executeQuery();
                if (!rs.next() || rs.getInt("available") != (available ? 1 : 0)) {
                    conn.rollback();
                    throw new DataAccessException("Availability update verification failed for truck: " + plateNum);
                }
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new DataAccessException("Error updating truck availability", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public List<TruckDTO> findAvailableTrucks() throws SQLException {
        List<TruckDTO> trucks = new ArrayList<>();
        String sql = "SELECT * FROM trucks WHERE available = 1";  // Use 1 instead of true for SQLite

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                TruckDTO truck = mapResultSetToTruckDTO(rs);
                if (truck.isAvailable()) {  // Double check availability
                    trucks.add(truck);
                }
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

    @Override
    public List<TruckDTO> findByLicenseType(String licenseType) throws SQLException {
        List<TruckDTO> trucks = new ArrayList<>();
        String sql = "SELECT * FROM trucks WHERE license_type = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licenseType);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                trucks.add(mapResultSetToTruckDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding trucks by license type: " + licenseType, e);
        }
        return trucks;
    }

    private TruckDTO mapResultSetToTruckDTO(ResultSet rs) throws SQLException {
        TruckDTO dto = new TruckDTO();
        dto.setLicensePlate(rs.getString("license_plate"));
        dto.setModel(rs.getString("model"));
        dto.setNetWeight(rs.getFloat("net_weight"));
        dto.setMaxWeight(rs.getFloat("max_weight"));
        dto.setRequiredLicenseType(LicenseType.valueOf(rs.getString("license_type")));
        dto.setAvailable(rs.getInt("available") == 1);  // Convert int to boolean
        return dto;
    }
} 