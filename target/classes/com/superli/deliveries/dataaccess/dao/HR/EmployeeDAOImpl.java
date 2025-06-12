package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.EmployeeDTO;
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
    public Optional<EmployeeDTO> findById(String id) throws SQLException {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (PreparedStatement  ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(fromResultSet(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<EmployeeDTO> findAll() throws SQLException {
        List<EmployeeDTO> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";

        try (PreparedStatement  st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                employees.add(fromResultSet(rs));
            }
        }

        return employees;
    }

    @Override
    public EmployeeDTO save(EmployeeDTO employee) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO employees (id, name, bank_account, salary, employment_terms, start_date)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement  ps = conn.prepareStatement(sql)) {
            ps.setString(1, employee.getId());
            ps.setString(2, employee.getFullName());
            ps.setString(3, employee.getBankAccount());
            ps.setDouble(4, employee.getSalary());
            ps.setString(5, employee.getEmploymentTerms());
            ps.setString(6, employee.getStartDate());
            ps.executeUpdate();
        }

        return employee;
    }

    @Override
    public void deleteById(String id) throws SQLException {
        conn.setAutoCommit(false);

        try {
            // שלב 1: שליפת העובד מהטבלה
            String selectSql = "SELECT * FROM employees WHERE id = ?";
            EmployeeDTO employee;

            try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                selectPs.setString(1, id);
                try (ResultSet rs = selectPs.executeQuery()) {
                    if (!rs.next())
                        throw new SQLException("Employee with ID " + id + " not found.");

                    employee = new EmployeeDTO(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("bank_account"),
                            rs.getDouble("salary"),
                            rs.getString("employment_terms"),
                            rs.getString("start_date") // נשאר כמחרוזת
                    );
                }
            }

            // שלב 2: הכנסת העובד לטבלת הארכיון
            String archiveSql = "INSERT INTO archived_employees (id, name, bank_account, salary, employment_terms, start_date) " +
                                "VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement archivePs = conn.prepareStatement(archiveSql)) {
                archivePs.setString(1, employee.getId());
                archivePs.setString(2, employee.getFullName());
                archivePs.setString(3, employee.getBankAccount());
                archivePs.setDouble(4, employee.getSalary());
                archivePs.setString(5, employee.getEmploymentTerms());
                archivePs.setString(6, employee.getStartDate()); // אין המרה לתאריך
                archivePs.executeUpdate();
            }

            // שלב 3: מחיקה מהטבלה הראשית
            String deleteSql = "DELETE FROM employees WHERE id = ?";
            try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                deletePs.setString(1, id);
                deletePs.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new SQLException("Failed to archive and delete employee", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // Utility method to convert ResultSet to EmployeeDTO
    private EmployeeDTO fromResultSet(ResultSet rs) throws SQLException {
        return new EmployeeDTO(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("bank_account"),
                rs.getDouble("salary"),
                rs.getString("employment_terms"),
                rs.getString("start_date")
        );
    }

    @Override
    public List<EmployeeDTO> findByRole(String role) throws SQLException {
        List<EmployeeDTO> employees = new ArrayList<>();
        String sql = "SELECT e.*" +
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



    private EmployeeDTO mapResultSetToEmployeeDTO(ResultSet rs) throws SQLException {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(rs.getString("id"));
        dto.setFullName(rs.getString("full_name"));
        dto.setBankAccount(rs.getString("bank_account"));
        dto.setSalary(rs.getDouble("salary"));
        dto.setEmploymentTerms(rs.getString("employment_terms"));
        dto.setStartDate(rs.getString("start_date"));
        //dto.setLicense(rs.getString("license"));
        return dto;
    }
}