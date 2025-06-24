package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.EmployeeDTO;
import com.superli.deliveries.dto.HR.ShiftDTO;
import com.superli.deliveries.exceptions.DataAccessException;
import com.superli.deliveries.util.Database;
import com.superli.deliveries.domain.core.ShiftType;

import java.sql.*;
import java.sql.Date;
import java.time.DayOfWeek;
import java.util.*;

public class ShiftDAOImpl implements ShiftDAO {
    private final Connection conn;

    public ShiftDAOImpl() {
        try {
            this.conn = Database.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    public ShiftDAOImpl(Connection testConnection) {
        this.conn = testConnection;
    }

    //@Override
    /*public List<ShiftDTO> findAll() throws SQLException {
        List<ShiftDTO> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shift_assignments";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                shifts.add(mapResultSetToShiftDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding all shifts", e);
        }
        return shifts;
    }*/
    @Override
    public List<ShiftDTO> findAll() throws SQLException {
        String sql = "SELECT * FROM shift_assignments";
        Map<String, ShiftDTO> shiftMap = new HashMap<>(); // key = date+type+day+site
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String date = rs.getString("date");
                String type = rs.getString("shift_type");
                String day = rs.getString("day_of_week");
                int siteId = rs.getInt("site_id");
                String key = date + "|" + type + "|" + day + "|" + siteId;

                ShiftDTO dto = shiftMap.getOrDefault(key, new ShiftDTO());
                dto.setShiftDate(java.sql.Date.valueOf(date));
                dto.setShiftType(type);
                dto.setShiftDay(day);
                dto.setSiteId(siteId);

                Map<Integer, Integer> assigned = dto.getAssignedEmployees();
                if (assigned == null)
                    assigned = new HashMap<>();
                assigned.put(rs.getInt("employee_id"), rs.getInt("role_id"));
                dto.setAssignedEmployees(assigned);

                shiftMap.put(key, dto);
            }
        }
        return new ArrayList<>(shiftMap.values());
    }


    @Override
    public void save(ShiftDTO shift) throws SQLException {
        String sql = "INSERT INTO shift_assignments (employee_id, day_of_week, shift_type, date, role_id, site_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Map.Entry<Integer, Integer> entry : shift.getAssignedEmployees().entrySet()) {
                ps.setString(1, String.valueOf(entry.getKey()));               // employee_id
                ps.setString(2, shift.getShiftDay().toUpperCase());           // day_of_week
                ps.setString(3, shift.getShiftType().toUpperCase());          // shift_type
                ps.setString(4, shift.getShiftDate().toString());             // date
                ps.setInt(5, entry.getValue());                               // role_id
                ps.setInt(6, shift.getSiteId());                              //  site_id
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }



    public void saveAssignment(String employeeId, String dayOfWeek, String shiftType, String date, int roleId,int siteId) throws SQLException {
        String sql = """
                INSERT INTO shift_assignments (employee_id, day_of_week, shift_type, date, role_id, site_id) VALUES (?, ?, ?, ?, ?, ?)""";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ps.setString(2, dayOfWeek.toUpperCase());
            ps.setString(3, shiftType.toUpperCase());
            ps.setString(4, date);
            ps.setInt(5, roleId);
            ps.setInt(6, siteId);
            ps.executeUpdate();
        }
    }
    @Override
    public List<ShiftDTO> findByDayOfWeekAndShiftType(DayOfWeek day, ShiftType st) throws SQLException {
        List<ShiftDTO> shifts = new ArrayList<>();
        String sql = "SELECT * FROM shift_assignments WHERE shift_day = ? AND shift_type = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, day.name());
            ps.setString(2, st.name());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                shifts.add(mapResultSetToShiftDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding shifts by day and type", e);
        }
        return shifts;
    }

    @Override
    public void clearAllAndArchive() throws SQLException {
        conn.setAutoCommit(false);
        try {
            String archiveSql = """
            INSERT INTO archived_shifts (employee_id, day_of_week, shift_type, date, role_id)
            SELECT employee_id, day_of_week, shift_type, date, role_id
            FROM shift_assignments
        """;
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(archiveSql);
            }

            String deleteSql = "DELETE FROM shift_assignments";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(deleteSql);
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw new SQLException("Failed to clear and archive shifts", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @Override
    public List<ShiftDTO> findByEmployeeId(String employeeId) throws SQLException {
        List<ShiftDTO> shifts = new ArrayList<>();
        String sql =  """
            FROM employees e
            JOIN shift_assignments sa ON e.id = sa.employee_id
            WHERE sa.shift_type = ? AND sa.day_of_week = ? AND sa.date = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                shifts.add(mapResultSetToShiftDTO(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error finding shifts by employee id", e);
        }
        return shifts;
    }

    private ShiftDTO mapResultSetToShiftDTO(ResultSet rs) throws SQLException {
        ShiftDTO dto = new ShiftDTO();
        dto.setSiteId(rs.getInt("site_id"));
        dto.setShiftDate(Date.valueOf(rs.getString("date")));
        dto.setShiftType(rs.getString("shift_type"));
        dto.setShiftDay(rs.getString("day_of_week"));
        return dto;
    }


    @Override
    public List<String> getEmployeeIdsByRoleAndDateAndType(String roleName, String date, ShiftType shiftType, int siteId) throws SQLException {
        String sql = """
        SELECT e.id
        FROM shift_assignments sa
        JOIN employees e ON sa.employee_id = e.id
        JOIN roles r ON sa.role_id = r.id
        WHERE sa.date = ? AND r.name = ? AND sa.shift_type = ? AND sa.site_id = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, date);
            ps.setString(2, roleName);
            ps.setString(3, shiftType.name().toUpperCase()); // MORNING / EVENING
            ps.setInt(4, siteId);

            ResultSet rs = ps.executeQuery();
            List<String> employeeIds = new ArrayList<>();

            while (rs.next()) {
                employeeIds.add(rs.getString("id"));
            }

            return employeeIds;
        }
    }


    @Override
    public List<String> getEmployeeIdsByRoleAndDate(String roleName, String date) throws SQLException {
        String sql = """
        SELECT e.id
        FROM shift_assignments sa
        JOIN employees e ON sa.employee_id = e.id
        JOIN roles r ON sa.role_id = r.id
        WHERE sa.date = ? AND r.name = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, date);
            ps.setString(2, roleName);

            ResultSet rs = ps.executeQuery();
            List<String> employeeIds = new ArrayList<>();

            while (rs.next()) {
                employeeIds.add(rs.getString("id"));
            }

            return employeeIds;
        }
    }
    @Override
    public boolean isEmployeeAlreadyAssigned(String employeeId, String dayOfWeek, String shiftType, String shiftDate) throws SQLException {
        String query = """
        SELECT 1 FROM shift_assignments
        WHERE employee_id = ? AND day_of_week = ? AND shift_type = ? AND date = ?
    """;

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employeeId);
            stmt.setString(2, dayOfWeek);
            stmt.setString(3, shiftType);
            stmt.setString(4, shiftDate);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public void removeAssignment(String employeeId, String dayOfWeek, String shiftType, String date, int siteId) throws SQLException {
        String sql = "DELETE FROM shift_assignments WHERE employee_id = ? AND day_of_week = ? AND shift_type = ? AND date = ? AND site_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, employeeId);
            stmt.setString(2, dayOfWeek);
            stmt.setString(3, shiftType);
            stmt.setString(4, date);
            stmt.setInt(5, siteId);
            stmt.executeUpdate();
        }
    }

}