package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.ArchivedEmployeeDTO;
import com.superli.deliveries.exceptions.DataAccessException;
import com.superli.deliveries.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivedEmployeeDAOImpl implements ArchivedEmployeeDAO {
    private final Connection conn;

    public ArchivedEmployeeDAOImpl() {
        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }
    public ArchivedEmployeeDAOImpl(Connection connection) {
        this.conn = connection;
    }

    @Override
    public List<ArchivedEmployeeDTO> findAll() throws SQLException {
        List<ArchivedEmployeeDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM archived_employees";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ArchivedEmployeeDTO dto = new ArchivedEmployeeDTO(
                        rs.getString("employee_id"),
                        rs.getString("name"),
                        rs.getString("bank_account"),
                        rs.getDouble("salary"),
                        rs.getString("employment_terms"),
                        rs.getString("start_date"),
                        rs.getInt("site_id")
                );
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch archived employees", e);
        }

        return list;
    }

    @Override
    public void save(ArchivedEmployeeDTO employee) throws SQLException {
        String sql = """
                INSERT INTO archived_employees 
                (employee_id, name, bank_account, salary, employment_terms, start_date, site_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)""";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employee.getEmployeeId());
            ps.setString(2, employee.getFullName());
            ps.setString(3, employee.getBankAccount());
            ps.setDouble(4, employee.getSalary());
            ps.setString(5, employee.getEmploymentTerms());
            ps.setString(6, employee.getStartDate());
            ps.setInt(7, employee.getSiteId());
            ps.executeUpdate();
        }
    }
}
