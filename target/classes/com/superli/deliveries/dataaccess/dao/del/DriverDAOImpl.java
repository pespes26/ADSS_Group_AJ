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
                e.id, e.name, e.bank_account, e.salary, e.site_id, 
                e.employment_terms, e.start_date, d.license_type, d.available,
                GROUP_CONCAT(r.name) as roles
            FROM employees e 
            LEFT JOIN drivers d ON e.id = d.id 
            LEFT JOIN employee_roles er ON e.id = er.employee_id 
            LEFT JOIN roles r ON er.role_id = r.id
            WHERE e.id = ? 
            GROUP BY e.id, d.license_type, d.available
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
                e.id, e.name, e.bank_account, e.salary, e.site_id, 
                e.employment_terms, e.start_date, d.license_type, d.available,
                GROUP_CONCAT(DISTINCT r.name) as roles
            FROM employees e 
            LEFT JOIN drivers d ON e.id = d.id 
            LEFT JOIN employee_roles er ON e.id = er.employee_id 
            LEFT JOIN roles r ON er.role_id = r.id
            GROUP BY e.id, e.name, e.bank_account, e.salary, e.site_id, 
                     e.employment_terms, e.start_date, d.license_type, d.available
            ORDER BY e.name
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
    public DriverDTO save(DriverDTO driverDto) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // === שמירה לטבלת employees ===
            String employeeSql = """
            INSERT INTO employees 
            (id, name, bank_account, salary, site_id, employment_terms, start_date) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET 
                name = ?,
                bank_account = ?,
                salary = ?,
                site_id = ?,
                employment_terms = ?,
                start_date = ?
        """;

            try (PreparedStatement employeeStmt = conn.prepareStatement(employeeSql)) {
                // חובה לוודא שהשם לא ריק
                String name = driverDto.getFullName();
                if (name == null || name.trim().isEmpty()) {
                    throw new DataAccessException("❌ Missing name for driver with ID: " + driverDto.getId());
                }

                String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

                employeeStmt.setString(1, driverDto.getId());
                employeeStmt.setString(2, name);
                employeeStmt.setString(3, "000-000-000"); // ברירת מחדל לחשבון בנק
                employeeStmt.setDouble(4, 0.0);
                employeeStmt.setInt(5, -1);
                employeeStmt.setString(6, "Standard");
                employeeStmt.setString(7, currentDate);

                // פרמטרים לעדכון במקרה של ON CONFLICT
                employeeStmt.setString(8, name);
                employeeStmt.setString(9, "000-000-000");
                employeeStmt.setDouble(10, 0.0);
                employeeStmt.setInt(11, -1);
                employeeStmt.setString(12, "Standard");
                employeeStmt.setString(13, currentDate);

                employeeStmt.executeUpdate();
            }

            // === שמירה לטבלת drivers ===
            String driverSql = """
            INSERT INTO drivers (id, license_type, available) 
            VALUES (?, ?, ?)
            ON CONFLICT(id) DO UPDATE SET 
                license_type = ?,
                available = ?
        """;

            try (PreparedStatement driverStmt = conn.prepareStatement(driverSql)) {
                // בדיקה לרישיון
                String licenseType = driverDto.getLicenseType();
                if (licenseType == null || !List.of("B", "C", "C1", "C2", "E").contains(licenseType)) {
                    throw new DataAccessException("❌ Invalid license type for driver ID: " + driverDto.getId());
                }

                driverStmt.setString(1, driverDto.getId());
                driverStmt.setString(2, licenseType);
                driverStmt.setBoolean(3, driverDto.isAvailable());
                driverStmt.setString(4, licenseType);
                driverStmt.setBoolean(5, driverDto.isAvailable());

                driverStmt.executeUpdate();
            }

            conn.commit();
            return driverDto;

        } catch (SQLException e) {
            conn.rollback();
            throw new DataAccessException("Error saving driver", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

//    @Override
//    public DriverDTO save(DriverDTO driverDto) throws SQLException {
//        conn.setAutoCommit(false);
//        try {
//            // First save to employees table
//            String employeeSql = """
//                INSERT INTO employees
//                (id, name, bank_account, salary, site_id, employment_terms, start_date)
//                VALUES (?, ?, ?, ?, ?, ?, ?)
//                ON CONFLICT(id) DO UPDATE SET
//                    name = ?,
//                    bank_account = ?,
//                    salary = ?,
//                    site_id = ?,
//                    employment_terms = ?,
//                    start_date = ?
//            """;
//
//            try (PreparedStatement employeeStmt = conn.prepareStatement(employeeSql)) {
//                employeeStmt.setString(1, driverDto.getId());
//                String name = driverDto.getFullName();
//                if (driverDto.getFullName() == null || driverDto.getFullName().trim().isEmpty()) {
//                    throw new DataAccessException("❌Driver full name is required");
//                }
//                employeeStmt.setString(2, name);
//                employeeStmt.setString(3, "000-000-000"); // Default bank account
//                employeeStmt.setDouble(4, 0.0); // Default salary
//                employeeStmt.setInt(5, -1); // Default site ID
//                employeeStmt.setString(6, "Standard"); // Default terms
//                employeeStmt.setString(7, new java.sql.Date(System.currentTimeMillis()).toString()); // Current date
//                employeeStmt.executeUpdate();
//            }
//
//            // Then save to drivers table
//            String driverSql = """
//                INSERT INTO drivers (id, license_type, available)
//                VALUES (?, ?, ?)
//                ON CONFLICT(id) DO UPDATE SET
//                    license_type = ?,
//                    available = ?
//            """;
//
//            try (PreparedStatement driverStmt = conn.prepareStatement(driverSql)) {
//                String name = driverDto.getFullName();
//                System.out.println("DEBUG: saving driver with full name = '" + name + "'");
//                if (name == null || name.trim().isEmpty()) {
//                    throw new DataAccessException("Driver full name is required and cannot be empty.");
//                }
//                driverStmt.setString(1, driverDto.getId()); // id
//                driverStmt.setString(2, name); // name
//                String licenseType = driverDto.getLicenseType();
//                if (licenseType == null || !List.of("A", "B", "C", "D", "E").contains(licenseType)) {
//                    throw new DataAccessException("Invalid license type for driver: " + licenseType);
//                }
//                driverStmt.setString(2, licenseType);
//                driverStmt.setBoolean(3, driverDto.isAvailable());      // available
//                driverStmt.executeUpdate();
//            }
//
//
//            conn.commit();
//            return driverDto;
//        } catch (SQLException e) {
//            conn.rollback();
//            throw new DataAccessException("Error saving driver", e);
//        } finally {
//            conn.setAutoCommit(true);
//        }
//    }

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
                e.id, e.name, e.bank_account, e.salary, e.site_id, 
                e.employment_terms, e.start_date, d.license_type, d.available,
                GROUP_CONCAT(DISTINCT r.name) as roles
            FROM employees e 
            INNER JOIN drivers d ON e.id = d.id 
            LEFT JOIN employee_roles er ON e.id = er.employee_id 
            LEFT JOIN roles r ON er.role_id = r.id
            WHERE d.available = 1 
            GROUP BY e.id, e.name, e.bank_account, e.salary, e.site_id, 
                     e.employment_terms, e.start_date, d.license_type, d.available
            ORDER BY e.name
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
    public List<DriverDTO> findUnavailableDrivers() throws SQLException {
        List<DriverDTO> drivers = new ArrayList<>();
        String sql = """
            SELECT 
                e.id, e.name, e.bank_account, e.salary, e.site_id, 
                e.employment_terms, e.start_date, d.license_type, d.available,
                GROUP_CONCAT(DISTINCT r.name) as roles
            FROM employees e 
            INNER JOIN drivers d ON e.id = d.id 
            LEFT JOIN employee_roles er ON e.id = er.employee_id 
            LEFT JOIN roles r ON er.role_id = r.id
            WHERE d.available = 0 
            GROUP BY e.id, e.name, e.bank_account, e.salary, e.site_id, 
                     e.employment_terms, e.start_date, d.license_type, d.available
            ORDER BY e.name
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Driver driver = mapResultSetToDriver(rs);
                if (!driver.isAvailable()) {  // Double check availability
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
                e.id, e.name, e.bank_account, e.salary, e.site_id, 
                e.employment_terms, e.start_date, d.license_type, d.available,
                GROUP_CONCAT(DISTINCT r.name) as roles
            FROM employees e 
            LEFT JOIN drivers d ON e.id = d.id 
            LEFT JOIN employee_roles er ON e.id = er.employee_id 
            LEFT JOIN roles r ON er.role_id = r.id
            WHERE d.license_type = ? 
            GROUP BY e.id, e.name, e.bank_account, e.salary, e.site_id, 
                     e.employment_terms, e.start_date, d.license_type, d.available
            ORDER BY e.name
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
                e.id, e.name, e.bank_account, e.salary, e.site_id, 
                e.employment_terms, e.start_date, d.license_type, d.available,
                GROUP_CONCAT(DISTINCT r.name) as roles
            FROM employees e 
            LEFT JOIN drivers d ON e.id = d.id 
            LEFT JOIN employee_roles er ON e.id = er.employee_id 
            LEFT JOIN roles r ON er.role_id = r.id
            WHERE d.license_number = ? 
            GROUP BY e.id, e.name, e.bank_account, e.salary, e.site_id, 
                     e.employment_terms, e.start_date, d.license_type, d.available
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
        String dateStr = rs.getString("start_date");
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

        // Parse roles from GROUP_CONCAT result
        List<Role> roles = new ArrayList<>();
        String rolesStr = rs.getString("roles");
        if (rolesStr != null && !rolesStr.isEmpty()) {
            for (String roleName : rolesStr.split(",")) {
                roles.add(new Role(roleName.trim()));
            }
        }

        Driver driver = new Driver(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("bank_account"),
                rs.getDouble("salary"),
                rs.getInt("site_id"),
                rs.getString("employment_terms"),
                startDate,
                roles, // Use parsed roles instead of empty list
                new ArrayList<>(), // availabilityConstraints
                new Role("DRIVER"), // loginRole
                licenseType
        );
        driver.setAvailable(rs.getInt("available") == 1);  // Convert int to boolean
        return driver;
    }
} 