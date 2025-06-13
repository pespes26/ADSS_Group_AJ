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
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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

        try (PreparedStatement st = conn.prepareStatement(sql);
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
            INSERT OR REPLACE INTO employees 
            (id, name, bank_account, salary, employment_terms, start_date, site_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employee.getId());
            ps.setString(2, employee.getFullName());
            ps.setString(3, employee.getBankAccount());
            ps.setDouble(4, employee.getSalary());
            ps.setString(5, employee.getEmploymentTerms());
            ps.setString(6, employee.getStartDate());
            ps.setInt(7, employee.getSiteId()); // 🔹 חדש
            ps.executeUpdate();
        }

        return employee;
    }

    @Override
    public void deleteById(String id) throws SQLException {
        conn.setAutoCommit(false);

        try {
            EmployeeDTO employee;
            String selectSql = "SELECT * FROM employees WHERE id = ?";

            try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                selectPs.setString(1, id);
                try (ResultSet rs = selectPs.executeQuery()) {
                    if (!rs.next())
                        throw new SQLException("Employee with ID " + id + " not found.");

                    employee = fromResultSet(rs);
                }
            }

            String archiveSql = """
                INSERT INTO archived_employees 
                (id, name, bank_account, salary, employment_terms, start_date, site_id)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
            try (PreparedStatement archivePs = conn.prepareStatement(archiveSql)) {
                archivePs.setString(1, employee.getId());
                archivePs.setString(2, employee.getFullName());
                archivePs.setString(3, employee.getBankAccount());
                archivePs.setDouble(4, employee.getSalary());
                archivePs.setString(5, employee.getEmploymentTerms());
                archivePs.setString(6, employee.getStartDate());
                archivePs.setInt(7, employee.getSiteId()); // 🔹 חדש
                archivePs.executeUpdate();
            }

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

    private EmployeeDTO fromResultSet(ResultSet rs) throws SQLException {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(rs.getString("id"));
        dto.setFullName(rs.getString("name"));
        dto.setBankAccount(rs.getString("bank_account"));
        dto.setSalary(rs.getDouble("salary"));
        dto.setEmploymentTerms(rs.getString("employment_terms"));
        dto.setStartDate(rs.getString("start_date"));
        dto.setSiteId(rs.getInt("site_id")); // 🔹 חדש
        return dto;
    }

    @Override
    public List<EmployeeDTO> findByRole(String role) throws SQLException {
        List<EmployeeDTO> employees = new ArrayList<>();
        String sql = """
            SELECT e.* 
            FROM employees e
            JOIN employee_roles er ON e.id = er.employee_id
            WHERE er.role_name = ?
        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employees.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding employees by role: " + role, e);
        }
        return employees;
    }
}
