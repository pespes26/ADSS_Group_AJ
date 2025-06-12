package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.EmployeeDTO;
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

    @Override
    public List<EmployeeDTO> findAllArchivedEmployees() throws SQLException {
        List<EmployeeDTO> archivedEmployees = new ArrayList<>();
        String sql = "SELECT * FROM archived_employees";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EmployeeDTO employee = new EmployeeDTO(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("bank_account"),
                        rs.getDouble("salary"),
                        rs.getString("employment_terms"),
                        rs.getString("start_date") // נשאר כמחרוזת לפי DTO
                );
                archivedEmployees.add(employee);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve archived employees", e);
        }

        return archivedEmployees;
    }
}
