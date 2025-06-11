package com.superli.deliveries.dataaccess.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.superli.deliveries.dto.ShiftDTO;
import com.superli.deliveries.exceptions.DataAccessException;
import com.superli.deliveries.util.Database;

public class ShiftDAOImpl implements ShiftDAO {
    private final Connection conn;

    public ShiftDAOImpl() {
        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    @Override
    public List<ShiftDTO> findAll() throws SQLException {
        List<ShiftDTO> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shifts";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                shifts.add(mapResultSetToShiftDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all shifts", e);
        }
        return shifts;
    }

    @Override
    public Optional<ShiftDTO> findById(String id) throws SQLException {
        String sql = "SELECT * FROM shifts WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToShiftDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding shift by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public void save(ShiftDTO shift) throws SQLException {
        String sql = "INSERT INTO shifts (id, employee_id, shift_date, start_time, end_time, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?) " +
                    "ON CONFLICT(id) DO UPDATE SET " +
                    "employee_id = ?, shift_date = ?, start_time = ?, end_time = ?, status = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, shift.getId());
            stmt.setString(2, shift.getEmployeeId());
            stmt.setDate(3, Date.valueOf(shift.getShiftDate()));
            stmt.setTime(4, Time.valueOf(shift.getStartTime()));
            stmt.setTime(5, Time.valueOf(shift.getEndTime()));
            stmt.setString(6, shift.getStatus());
            
            // Values for UPDATE
            stmt.setString(7, shift.getEmployeeId());
            stmt.setDate(8, Date.valueOf(shift.getShiftDate()));
            stmt.setTime(9, Time.valueOf(shift.getStartTime()));
            stmt.setTime(10, Time.valueOf(shift.getEndTime()));
            stmt.setString(11, shift.getStatus());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error saving shift", e);
        }
    }

    @Override
    public void deleteById(String id) throws SQLException {
        String sql = "DELETE FROM shifts WHERE id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting shift with id: " + id, e);
        }
    }

    @Override
    public List<ShiftDTO> findByDate(LocalDate date) throws SQLException {
        List<ShiftDTO> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shifts WHERE shift_date = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                shifts.add(mapResultSetToShiftDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding shifts by date: " + date, e);
        }
        return shifts;
    }

    @Override
    public List<ShiftDTO> findByEmployeeId(String employeeId) throws SQLException {
        List<ShiftDTO> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shifts WHERE employee_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                shifts.add(mapResultSetToShiftDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding shifts by employee id: " + employeeId, e);
        }
        return shifts;
    }

    @Override
    public List<ShiftDTO> findByDayOfWeek(DayOfWeek day) throws SQLException {
        List<ShiftDTO> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shifts WHERE EXTRACT(DOW FROM shift_date) = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, day.getValue());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                shifts.add(mapResultSetToShiftDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding shifts for day of week: " + day, e);
        }
        return shifts;
    }

    private ShiftDTO mapResultSetToShiftDTO(ResultSet rs) throws SQLException {
        ShiftDTO dto = new ShiftDTO();
        dto.setId(rs.getString("id"));
        dto.setEmployeeId(rs.getString("employee_id"));
        dto.setShiftDate(rs.getDate("shift_date").toLocalDate());
        dto.setStartTime(rs.getTime("start_time").toLocalTime());
        dto.setEndTime(rs.getTime("end_time").toLocalTime());
        dto.setStatus(rs.getString("status"));
        return dto;
    }
} 