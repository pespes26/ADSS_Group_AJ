package com.superli.deliveries.dataaccess.dao.del;

import com.superli.deliveries.dto.del.DriverDTO;
import com.superli.deliveries.domain.core.Driver;
import com.superli.deliveries.domain.core.LicenseType;
import com.superli.deliveries.Mappers.DriverMapper;
import com.superli.deliveries.util.Database;
import com.superli.deliveries.exceptions.DataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DriverDAOImpl implements DriverDAO {
    private final Connection conn;

    public DriverDAOImpl() {
        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    @Override
    public Optional<DriverDTO> findById(String id) throws SQLException {
        String sql = "SELECT e.*, d.*, er.role_name, er.qualification_level " +
                    "FROM employees e " +
                    "LEFT JOIN driver d ON e.id = d.employee_id " +
                    "LEFT JOIN employee_roles er ON e.id = er.employee_id " +
                    "WHERE e.id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Driver driver = mapResultSetToDriver(rs);
                return Optional.of(DriverMapper.toDTO(driver));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding driver by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<DriverDTO> findAll() throws SQLException {
        List<DriverDTO> drivers = new ArrayList<>();
        String sql = "SELECT e.*, d.*, er.role_name, er.qualification_level " +
                    "FROM employees e " +
                    "LEFT JOIN driver d ON e.id = d.employee_id " +
                    "LEFT JOIN employee_roles er ON e.id = er.employee_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Driver driver = mapResultSetToDriver(rs);
                drivers.add(DriverMapper.toDTO(driver));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all drivers", e);
        }
        return drivers;
    }

    @Override
    public DriverDTO save(DriverDTO dto) throws SQLException {
        String sql = "INSERT INTO employees (id, full_name) VALUES (?, ?) " +
                    "ON CONFLICT(id) DO UPDATE SET full_name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dto.getId());
            stmt.setString(2, dto.getFullName());
            stmt.setString(3, dto.getFullName());
            
            stmt.executeUpdate();

            // Save driver-specific data
            String driverSql = "INSERT INTO driver (employee_id, license_type) VALUES (?, ?) " +
                             "ON CONFLICT(employee_id) DO UPDATE SET license_type = ?";

            try (PreparedStatement driverStmt = conn.prepareStatement(driverSql)) {
                driverStmt.setString(1, dto.getId());
                driverStmt.setString(2, dto.getLicenseType());
                driverStmt.setString(3, dto.getLicenseType());
                driverStmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error saving driver", e);
        }
        
        return dto;
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting driver with id: " + id, e);
        }
    }

    @Override
    public List<DriverDTO> findAvailableDrivers() throws SQLException {
        List<DriverDTO> drivers = new ArrayList<>();
        String sql = "SELECT e.*, d.*, er.role_name, er.qualification_level " +
                    "FROM employees e " +
                    "LEFT JOIN driver d ON e.id = d.employee_id " +
                    "LEFT JOIN employee_roles er ON e.id = er.employee_id " +
                    "WHERE d.available = true";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Driver driver = mapResultSetToDriver(rs);
                drivers.add(DriverMapper.toDTO(driver));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding available drivers", e);
        }
        return drivers;
    }

    @Override
    public List<DriverDTO> findByLicenseType(String licenseType) throws SQLException {
        List<DriverDTO> drivers = new ArrayList<>();
        String sql = "SELECT e.*, d.*, er.role_name, er.qualification_level " +
                    "FROM employees e " +
                    "LEFT JOIN driver d ON e.id = d.employee_id " +
                    "LEFT JOIN employee_roles er ON e.id = er.employee_id " +
                    "WHERE d.license_type = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licenseType);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Driver driver = mapResultSetToDriver(rs);
                drivers.add(DriverMapper.toDTO(driver));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding drivers by license type: " + licenseType, e);
        }
        return drivers;
    }

    @Override
    public Optional<DriverDTO> findByLicenseNumber(String licenseNumber) throws SQLException {
        String sql = "SELECT e.*, d.*, er.role_name, er.qualification_level " +
                    "FROM employees e " +
                    "LEFT JOIN driver d ON e.id = d.employee_id " +
                    "LEFT JOIN employee_roles er ON e.id = er.employee_id " +
                    "WHERE d.license_number = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, licenseNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Driver driver = mapResultSetToDriver(rs);
                return Optional.of(DriverMapper.toDTO(driver));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding driver by license number: " + licenseNumber, e);
        }
        return Optional.empty();
    }

    private Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
        Driver driver = new Driver(
            String.valueOf(rs.getInt("id")),
            rs.getString("full_name"),
            null, // bankAccount
            0.0, // salary
            null, // employeeTerms
            null, // employeeStartDate
            null, // roleQualifications
            null, // availabilityConstraints
            null, // loginRole
            LicenseType.valueOf(rs.getString("license_type"))
        );
        driver.setAvailable(rs.getBoolean("available"));
        return driver;
    }
} 