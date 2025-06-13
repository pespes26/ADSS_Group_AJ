package com.superli.deliveries;

import com.superli.deliveries.dataaccess.dao.HR.ShiftDAO;
import com.superli.deliveries.dataaccess.dao.HR.ShiftDAOImpl;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftAssignmentTest {
    private Connection conn;
    private ShiftDAO shiftDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:deliveries.db");
        Statement stmt = conn.createStatement();

        stmt.execute("DROP TABLE IF EXISTS shift_assignments");
        stmt.execute("DROP TABLE IF EXISTS roles");
        stmt.execute("DROP TABLE IF EXISTS employees");

        stmt.execute("""
            CREATE TABLE employees (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                bank_account TEXT NOT NULL,
                salary REAL NOT NULL,
                employment_terms TEXT NOT NULL,
                start_date TEXT NOT NULL
            );
        """);

        stmt.execute("""
            CREATE TABLE roles (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            );
        """);

        stmt.execute("""
            CREATE TABLE shift_assignments (
                employee_id TEXT NOT NULL,
                day_of_week TEXT CHECK(day_of_week IN ('SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY')) NOT NULL,
                shift_type TEXT CHECK(shift_type IN ('MORNING','EVENING')) NOT NULL,
                date TEXT NOT NULL,
                role_id INTEGER NOT NULL,
                PRIMARY KEY (employee_id, day_of_week, shift_type),
                FOREIGN KEY (employee_id) REFERENCES employees(id),
                FOREIGN KEY (role_id) REFERENCES roles(id)
            );
        """);

        stmt.execute("""
            INSERT INTO employees (id, name, bank_account, salary, employment_terms, start_date)
            VALUES ('123456789', 'Test Employee', '123-456789', 5000.0, 'Full-Time', '2025-01-01');
        """);

        stmt.execute("INSERT INTO roles (id, name) VALUES (1, 'Driver')");

        shiftDAO = new ShiftDAOImpl(conn);
    }

    @Test
    public void testSaveAssignment_insertsRecord() throws SQLException {
        shiftDAO.saveAssignment("123456789", "SUNDAY", "MORNING", "2025-06-15", 1);

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM shift_assignments");
        ResultSet rs = ps.executeQuery();

        assertTrue(rs.next(), "No record inserted into shift_assignments");
        assertEquals("123456789", rs.getString("employee_id"));
        assertEquals("SUNDAY", rs.getString("day_of_week"));
        assertEquals("MORNING", rs.getString("shift_type"));
        assertEquals("2025-06-15", rs.getString("date"));
        assertEquals(1, rs.getInt("role_id"));

        assertFalse(rs.next(), "More than one record inserted unexpectedly");
    }

    @Test
    public void testSaveAssignment_duplicatePrimaryKey_throwsException() throws SQLException {
        shiftDAO.saveAssignment("123456789", "SUNDAY", "MORNING", "2025-06-15", 1);

        SQLException thrown = assertThrows(SQLException.class, () -> {
            shiftDAO.saveAssignment("123456789", "SUNDAY", "MORNING", "2025-06-16", 1);
        });

        assertTrue(thrown.getMessage().contains("UNIQUE") || thrown.getMessage().contains("PRIMARY"));
    }

    @Test
    public void testSaveAssignment_invalidShiftType_fails() {
        SQLException thrown = assertThrows(SQLException.class, () -> {
            shiftDAO.saveAssignment("123456789", "SUNDAY", "NIGHT", "2025-06-15", 1);
        });

        assertTrue(thrown.getMessage().contains("CHECK constraint failed") || thrown.getMessage().contains("shift_type"));
    }

    @Test
    public void testSaveAssignment_invalidDayOfWeek_fails() {
        SQLException thrown = assertThrows(SQLException.class, () -> {
            shiftDAO.saveAssignment("123456789", "FUNDAY", "MORNING", "2025-06-15", 1);
        });

        assertTrue(thrown.getMessage().contains("CHECK constraint failed") || thrown.getMessage().contains("day_of_week"));
    }

    @Test
    public void testGetDriversByDate() throws SQLException {

        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS roles");
        stmt.execute("""
            CREATE TABLE roles (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            );
        """);
        stmt.execute("INSERT INTO roles (id, name) VALUES (1, 'Driver'), (2, 'Warehouse Worker')");


        stmt.execute("""
        INSERT INTO employees (id, name, bank_account, salary, employment_terms, start_date) VALUES
        ('111111111', 'Driver One', '111', 5000, 'Full-Time', '2023-01-01'),
        ('222222222', 'Driver Two', '222', 5200, 'Full-Time', '2023-02-01'),
        ('333333333', 'Warehouse worker', '333', 4800, 'Part-Time', '2024-01-15');
    """);


        stmt.execute("""
        INSERT INTO shift_assignments (employee_id, day_of_week, shift_type, date, role_id) VALUES
        ('111111111', 'SUNDAY', 'MORNING', '2025-06-15', 1),
        ('222222222', 'SUNDAY', 'MORNING', '2025-06-15', 1),
        ('333333333', 'SUNDAY', 'MORNING', '2025-06-15', 2);
    """);

        // ביצוע שאילתה – שליפת כל הדרייברים למשמרת בתאריך 2025-06-15
        PreparedStatement ps = conn.prepareStatement("""
        SELECT e.id, e.name
        FROM shift_assignments sa
        JOIN employees e ON sa.employee_id = e.id
        JOIN roles r ON sa.role_id = r.id
        WHERE sa.date = ? AND r.name = 'Driver'
    """);
        ps.setString(1, "2025-06-15");
        ResultSet rs = ps.executeQuery();


        int count = 0;
        while (rs.next()) {
            String id = rs.getString("id");
            String name = rs.getString("name");
            System.out.println("Found driver: " + id + " - " + name);
            count++;
        }

        assertEquals(2, count, "Should find 2 drivers for that shift and date.");
    }

}
