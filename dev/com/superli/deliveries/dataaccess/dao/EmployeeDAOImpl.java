package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.EmployeeDTO;
import com.superli.deliveries.util.Database;
import com.superli.deliveries.exceptions.DataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeDAOImpl implements EmployeeDAO {
    private final Connection conn;

    public EmployeeDAOImpl() {
        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    @Override
    public List<EmployeeDTO> findAll() throws SQLException {
        List<EmployeeDTO> employees = new ArrayList<>();
        String sql = "SELECT e.*, er.role_name, er.qualification_level " +
                    "FROM employees e " +
                    "LEFT JOIN employee_roles er ON e.id = er.employee_id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employees.add(mapResultSetToEmployeeDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all employees", e);
        }
        return employees;
    }

    @Override
    public Optional<EmployeeDTO> findById(String id) throws SQLException {
        String sql = "SELECT e.*, er.role_name, er.qualification_level " +
                    "FROM employees e " +
                    "LEFT JOIN employee_roles er ON e.id = er.employee_id " +
                    "WHERE e.id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEmployeeDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding employee by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public EmployeeDTO save(EmployeeDTO employee) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // Insert into employees table
            String employeeSql = "INSERT INTO employees (id, full_name, bank_account, salary, employment_terms, start_date) " +
                               "VALUES (?, ?, ?, ?, ?, ?) " +
                               "ON CONFLICT(id) DO UPDATE SET " +
                               "full_name = ?, bank_account = ?, salary = ?, employment_terms = ?, start_date = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(employeeSql)) {
                stmt.setString(1, employee.getId());
                stmt.setString(2, employee.getFullName());
                stmt.setString(3, employee.getBankAccount());
                stmt.setDouble(4, employee.getSalary());
                stmt.setString(5, employee.getEmploymentTerms());
                stmt.setString(6, employee.getStartDate());
                
                // Values for UPDATE
                stmt.setString(7, employee.getFullName());
                stmt.setString(8, employee.getBankAccount());
                stmt.setDouble(9, employee.getSalary());
                stmt.setString(10, employee.getEmploymentTerms());
                stmt.setString(11, employee.getStartDate());
                
                stmt.executeUpdate();
            }

            // Insert/Update employee role
            String roleSql = "INSERT INTO employee_roles (employee_id, role_name, qualification_level) " +
                           "VALUES (?, ?, ?) " +
                           "ON CONFLICT(employee_id) DO UPDATE SET " +
                           "role_name = ?, qualification_level = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(roleSql)) {
                stmt.setString(1, employee.getId());
                stmt.setString(2, employee.getRole());
                stmt.setInt(3, employee.getQualificationLevel());
                
                // Values for UPDATE
                stmt.setString(4, employee.getRole());
                stmt.setInt(5, employee.getQualificationLevel());
                
                stmt.executeUpdate();
            }

            conn.commit();
            return employee;
        } catch (SQLException e) {
            conn.rollback();
            throw new DataAccessException("Error saving employee", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public void deleteById(String id) throws SQLException {
        conn.setAutoCommit(false);
        try {
            // Delete from employee_roles
            String roleSql = "DELETE FROM employee_roles WHERE employee_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(roleSql)) {
                stmt.setString(1, id);
                stmt.executeUpdate();
            }

            // Delete from employees
            String employeeSql = "DELETE FROM employees WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(employeeSql)) {
                stmt.setString(1, id);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new DataAccessException("Error deleting employee with id: " + id, e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public List<EmployeeDTO> findByRole(String role) throws SQLException {
        List<EmployeeDTO> employees = new ArrayList<>();
        String sql = "SELECT e.*, er.role_name, er.qualification_level " +
                    "FROM employees e " +
                    "JOIN employee_roles er ON e.id = er.employee_id " +
                    "WHERE er.role_name = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employees.add(mapResultSetToEmployeeDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding employees by role: " + role, e);
        }
        return employees;
    }

    @Override
    public List<EmployeeDTO> findActiveEmployees() throws SQLException {
        List<EmployeeDTO> employees = new ArrayList<>();
        String sql = "SELECT e.*, er.role_name, er.qualification_level " +
                    "FROM employees e " +
                    "LEFT JOIN employee_roles er ON e.id = er.employee_id " +
                    "WHERE e.active = true";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employees.add(mapResultSetToEmployeeDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding active employees", e);
        }
        return employees;
    }

    @Override
    public Optional<EmployeeDTO> findByEmail(String email) throws SQLException {
        String sql = "SELECT e.*, er.role_name, er.qualification_level " +
                    "FROM employees e " +
                    "LEFT JOIN employee_roles er ON e.id = er.employee_id " +
                    "WHERE e.email = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEmployeeDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding employee by email: " + email, e);
        }
        return Optional.empty();
    }

    @Override
    public List<EmployeeDTO> findByQualificationLevel(int level) throws SQLException {
        List<EmployeeDTO> employees = new ArrayList<>();
        String sql = "SELECT e.*, er.role_name, er.qualification_level " +
                    "FROM employees e " +
                    "JOIN employee_roles er ON e.id = er.employee_id " +
                    "WHERE er.qualification_level = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, level);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employees.add(mapResultSetToEmployeeDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding employees by qualification level: " + level, e);
        }
        return employees;
    }

    private EmployeeDTO mapResultSetToEmployeeDTO(ResultSet rs) throws SQLException {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(rs.getString("id"));
        dto.setFullName(rs.getString("full_name"));
        dto.setBankAccount(rs.getString("bank_account"));
        dto.setSalary(rs.getDouble("salary"));
        dto.setEmploymentTerms(rs.getString("employment_terms"));
        dto.setStartDate(rs.getString("start_date"));
        dto.setLicense(rs.getString("license"));
        dto.setRole(rs.getString("role_name"));
        dto.setQualificationLevel(rs.getInt("qualification_level"));
        return dto;
    }
} 