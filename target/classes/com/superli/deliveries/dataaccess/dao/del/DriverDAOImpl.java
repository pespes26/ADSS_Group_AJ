package com.superli.deliveries.dataaccess.dao.del;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.superli.deliveries.Mappers.DriverMapper;
import com.superli.deliveries.domain.core.Driver;
import com.superli.deliveries.domain.core.LicenseType;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.dto.del.DriverDTO;
import com.superli.deliveries.exceptions.DataAccessException;
import com.superli.deliveries.util.Database;

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
        String sql = """
            SELECT 
                e.id, e.full_name, e.bank_account, e.salary, e.site_id, 
                e.employee_terms, e.employee_start_date, e.login_role,
                d.license_type, d.available,
                GROUP_CONCAT(er.role_name) as roles
            FROM employees e 
            LEFT JOIN drivers d ON e.id = d.id 
            LEFT JOIN employee_roles er ON e.id = er.employee_id 
            WHERE e.id = ? 
            GROUP BY e.id, d.id
        """;

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
        String sql = """
            SELECT 
                e.id, e.full_name, e.bank_account, e.salary, e.site_id, 
                e.employee_terms, e.employee_start_date, e.login_role,
                d.license_type, d.available,
                GROUP_CONCAT(er.role_name) as roles
            FROM employees e 
            LEFT JOIN drivers d ON e.id = d.id 
            LEFT JOIN employee_roles er ON e.id = er.employee_id 
            GROUP BY e.id, d.id
            ORDER BY e.full_name
        """;

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
        conn.setAutoCommit(false);
        try {
            // First save to employees table
            String employeeSql = """
                INSERT INTO employees 
                (id, full_name, bank_account, salary, site_id, employee_terms, employee_start_date, login_role) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET 
                    full_name = ?,
                    bank_account = ?,
                    salary = ?,
                    site_id = ?,
                    employee_terms = ?,
                    employee_start_date = ?,
                    login_role = ?
            """;

            try (PreparedStatement employeeStmt = conn.prepareStatement(employeeSql)) {
                employeeStmt.setString(1, dto.getId());
                employeeStmt.setString(2, dto.getFullName());
                employeeStmt.setString(3, "000-000-000"); // Default bank account
                employeeStmt.setDouble(4, 0.0); // Default salary
                employeeStmt.setInt(5, -1); // Default site ID
                employeeStmt.setString(6, "Standard"); // Default terms
                employeeStmt.setString(7, new java.sql.Date(System.currentTimeMillis()).toString()); // Current date
                employeeStmt.setString(8, "DRIVER"); // Default role
                // Update values
                employeeStmt.setString(9, dto.getFullName());
                employeeStmt.setString(10, "000-000-000");
                employeeStmt.setDouble(11, 0.0);
                employeeStmt.setInt(12, -1);
                employeeStmt.setString(13, "Standard");
                employeeStmt.setString(14, new java.sql.Date(System.currentTimeMillis()).toString());
                employeeStmt.setString(15, "DRIVER");
                employeeStmt.executeUpdate();
            }

            // Then save to drivers table
            String driverSql = """
                INSERT INTO drivers (id, license_type, available) 
                VALUES (?, ?, ?)
                ON CONFLICT(id) DO UPDATE SET 
                    license_type = ?,
                    available = ?
            """;

            try (PreparedStatement driverStmt = conn.prepareStatement(driverSql)) {
                driverStmt.setString(1, dto.getId());
                driverStmt.setString(2, dto.getLicenseType());
                driverStmt.setBoolean(3, dto.isAvailable());
                driverStmt.setString(4, dto.getLicenseType());
                driverStmt.setBoolean(5, dto.isAvailable());
                driverStmt.executeUpdate();
            }

            conn.commit();
            return dto;
        } catch (SQLException e) {
            conn.rollback();
            throw new DataAccessException("Error saving driver", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // First delete from drivers table
            String driverSql = "DELETE FROM drivers WHERE id = ?";
            try (PreparedStatement driverStmt = conn.prepareStatement(driverSql)) {
                driverStmt.setString(1, id);
                driverStmt.executeUpdate();
            }

            // Then delete from employees table
            String employeeSql = "DELETE FROM employees WHERE id = ?";
            try (PreparedStatement employeeStmt = conn.prepareStatement(employeeSql)) {
                employeeStmt.setString(1, id);
                employeeStmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new DataAccessException("Error deleting driver with id: " + id, e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public List<DriverDTO> findAvailableDrivers() throws SQLException {
        List<DriverDTO> drivers = new ArrayList<>();
        String sql = """
            SELECT 
                e.id, e.full_name, e.bank_account, e.salary, e.site_id, 
                e.employee_terms, e.employee_start_date, e.login_role,
                d.license_type, d.available,
                GROUP_CONCAT(er.role_name) as roles
            FROM employees e 
            INNER JOIN drivers d ON e.id = d.id 
            LEFT JOIN employee_roles er ON e.id = er.employee_id 
            WHERE d.available = 1 
            GROUP BY e.id, d.id, d.available
            ORDER BY e.full_name
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Driver driver = mapResultSetToDriver(rs);
                if (driver.isAvailable()) {  // Double check availability
                    drivers.add(DriverMapper.toDTO(driver));
                }
            }
        }
        return drivers;
    }

    @Override
    public List<DriverDTO> findByLicenseType(String licenseType) throws SQLException {
        List<DriverDTO> drivers = new ArrayList<>();
        String sql = """
            SELECT 
                e.id, e.full_name, e.bank_account, e.salary, e.site_id, 
                e.employee_terms, e.employee_start_date, e.login_role,
                d.license_type, d.available,
                GROUP_CONCAT(er.role_name) as roles
            FROM employees e 
            LEFT JOIN drivers d ON e.id = d.id 
            LEFT JOIN employee_roles er ON e.id = er.employee_id 
            WHERE d.license_type = ? 
            GROUP BY e.id, d.id
            ORDER BY e.full_name
        """;

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
        String sql = """
            SELECT 
                e.id, e.full_name, e.bank_account, e.salary, e.site_id, 
                e.employee_terms, e.employee_start_date, e.login_role,
                d.license_type, d.available,
                GROUP_CONCAT(er.role_name) as roles
            FROM employees e 
            LEFT JOIN drivers d ON e.id = d.id 
            LEFT JOIN employee_roles er ON e.id = er.employee_id 
            WHERE d.license_number = ? 
            GROUP BY e.id, d.id
        """;

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

    @Override
    public void updateDriverAvailability(String id, boolean available) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // First verify the driver exists
            String checkSql = "SELECT id FROM drivers WHERE id = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, id);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    throw new DataAccessException("Driver not found with id: " + id);
                }
            }

            // Then update the availability
            String updateSql = "UPDATE drivers SET available = ? WHERE id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, available ? 1 : 0);  // Convert boolean to int for SQLite
                updateStmt.setString(2, id);
                int rowsAffected = updateStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("Failed to update availability for driver: " + id);
                }
            }

            // Verify the update
            String verifySql = "SELECT available FROM drivers WHERE id = ?";
            try (PreparedStatement verifyStmt = conn.prepareStatement(verifySql)) {
                verifyStmt.setString(1, id);
                ResultSet rs = verifyStmt.executeQuery();
                if (!rs.next() || rs.getInt("available") != (available ? 1 : 0)) {
                    conn.rollback();
                    throw new DataAccessException("Availability update verification failed for driver: " + id);
                }
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new DataAccessException("Error updating driver availability", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
        String dateStr = rs.getString("employee_start_date");
        java.util.Date startDate = null;
        try {
            if (dateStr != null) {
                startDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            }
        } catch (java.text.ParseException e) {
            throw new DataAccessException("Error parsing employee start date: " + dateStr, e);
        }

        String licenseTypeStr = rs.getString("license_type");
        LicenseType licenseType = licenseTypeStr != null ? 
            LicenseType.valueOf(licenseTypeStr) : 
            LicenseType.C; // Default to C if null

        Driver driver = new Driver(
                rs.getString("id"),
                rs.getString("full_name"),
                rs.getString("bank_account"),
                rs.getDouble("salary"),
                rs.getInt("site_id"),
                rs.getString("employee_terms"),
                startDate,
                new ArrayList<>(), // roleQualifications
                new ArrayList<>(), // availabilityConstraints
                new Role("DRIVER"), // loginRole
                licenseType
        );
        driver.setAvailable(rs.getInt("available") == 1);  // Convert int to boolean
        return driver;
    }
} 