
package com.superli.deliveries;

import com.superli.deliveries.dataaccess.dao.HR.ShiftDAOImpl;
import com.superli.deliveries.dto.HR.ShiftDTO;
import com.superli.deliveries.domain.core.ShiftType;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.sql.Date;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftDAOImplTest {
    private static Connection connection;
    private ShiftDAOImpl dao;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
            CREATE TABLE employees (
                id TEXT PRIMARY KEY
            );
            """);
            stmt.executeUpdate("""
            CREATE TABLE roles (
                id INTEGER PRIMARY KEY,
                name TEXT
            );
            """);
            stmt.executeUpdate("""
            CREATE TABLE shift_assignments (
                employee_id TEXT NOT NULL,
                day_of_week TEXT CHECK(day_of_week IN ('SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY')),
                shift_type TEXT CHECK(shift_type IN ('MORNING','EVENING')),
                date TEXT,
                role_id INTEGER,
                site_id INTEGER,
                PRIMARY KEY (employee_id, day_of_week, shift_type, site_id),
                FOREIGN KEY (employee_id) REFERENCES employees(id),
                FOREIGN KEY (role_id) REFERENCES roles(id)
            );
            """);
        }
    }

    @BeforeEach
    void init() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM shift_assignments");
            stmt.execute("DELETE FROM employees");
            stmt.execute("DELETE FROM roles");
        }
        dao = new ShiftDAOImpl(connection);
    }

    @Test
    void testSaveAndFindAll() throws SQLException {
        ShiftDTO shift = new ShiftDTO();
        shift.setShiftDate(Date.valueOf("2025-06-21"));
        shift.setShiftType("Morning");
        shift.setShiftDay("Saturday");
        shift.setSiteId(1);

        Map<Integer, Integer> map = new HashMap<>();
        map.put(101, 201);
        map.put(102, 202);
        shift.setAssignedEmployees(map);

        dao.save(shift);

        List<ShiftDTO> all = dao.findAll();

        List<ShiftDTO> filtered = all.stream()
                .filter(s -> s.getShiftDate().equals(Date.valueOf("2025-06-21"))
                        && s.getSiteId() == 1
                        && s.getShiftType().equalsIgnoreCase("Morning")
                        && s.getShiftDay().equalsIgnoreCase("Saturday"))
                .toList();

        assertEquals(1, filtered.size(), "Expected exactly one matching shift");

        ShiftDTO retrieved = filtered.get(0);
        Map<Integer, Integer> assigned = retrieved.getAssignedEmployees();

        assertEquals(2, assigned.size());
        assertEquals(201, assigned.get(101));
        assertEquals(202, assigned.get(102));
    }

    @Test
    void testSaveAssignmentAndIsEmployeeAlreadyAssigned() throws SQLException {
        String employeeId = "123";
        String dayOfWeek = "MONDAY";
        String shiftType = "MORNING";
        String date = "2025-06-23";
        int roleId = 1;
        int siteId = 1;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO employees (id) VALUES ('123');");
            stmt.execute("INSERT INTO roles (id, name) VALUES (1, 'GENERIC');");
        }

        dao.saveAssignment(employeeId, dayOfWeek, shiftType, date, roleId, siteId);

        boolean isAssigned = dao.isEmployeeAlreadyAssigned(employeeId, dayOfWeek, shiftType, date);
        assertTrue(isAssigned, "Expected employee to be assigned but got false.");
    }

    @Test
    void testRemoveAssignment() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO employees (id) VALUES ('123');");
            stmt.execute("INSERT INTO roles (id, name) VALUES (111, 'GENERIC');");
        }
        dao.saveAssignment("123", "Monday", "Morning", "2025-06-23", 111, 2);
        dao.removeAssignment("123", "Monday", "Morning", "2025-06-23", 2);
        boolean assigned = dao.isEmployeeAlreadyAssigned("123", "Monday", "Morning", "2025-06-23");
        assertFalse(assigned);
    }

    @Test
    void testMultipleAssignmentsSameShift() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO employees (id) VALUES ('201'), ('202');");
            stmt.execute("INSERT INTO roles (id, name) VALUES (101, 'R1'), (102, 'R2');");
        }
        dao.saveAssignment("201", "Tuesday", "Evening", "2025-06-24", 101, 1);
        dao.saveAssignment("202", "Tuesday", "Evening", "2025-06-24", 102, 1);
        List<ShiftDTO> shifts = dao.findAll();
        long count = shifts.stream().filter(s -> s.getShiftDate().equals(Date.valueOf("2025-06-24"))).count();
        assertTrue(count >= 1, "Expected at least one shift with assignments");
    }

    @Test
    void testFindAllReturnsEmptyInitially() throws SQLException {
        List<ShiftDTO> shifts = dao.findAll();
        assertNotNull(shifts);
        assertEquals(0, shifts.size(), "Expected no shifts in a fresh DB");
    }

    @Test
    void testSaveAssignment_invalidShiftType_fails() {
        SQLException thrown = assertThrows(SQLException.class, () -> {
            dao.saveAssignment("123456789", "SUNDAY", "NIGHT", "2025-06-15", 1, 1);
        });
        assertTrue(thrown.getMessage().contains("CHECK constraint failed") || thrown.getMessage().contains("shift_type"));
    }

    @Test
    void testSaveAssignment_invalidDayOfWeek_fails() {
        SQLException thrown = assertThrows(SQLException.class, () -> {
            dao.saveAssignment("123456789", "FUNDAY", "MORNING", "2025-06-15", 1, 1);
        });
        assertTrue(thrown.getMessage().contains("CHECK constraint failed") || thrown.getMessage().contains("day_of_week"));
    }

    @Test
    void testSaveAssignment_duplicatePrimaryKey_throwsException() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO employees (id) VALUES ('123456789');");
            stmt.execute("INSERT INTO roles (id, name) VALUES (1, 'GENERIC');");
        }
        dao.saveAssignment("123456789", "SUNDAY", "MORNING", "2025-06-15", 1, 1);
        SQLException thrown = assertThrows(SQLException.class, () -> {
            dao.saveAssignment("123456789", "SUNDAY", "MORNING", "2025-06-16", 1, 1);
        });
        assertTrue(thrown.getMessage().contains("UNIQUE") || thrown.getMessage().contains("PRIMARY"));
    }

    @Test
    void testGetEmployeeIdsByRoleAndDateAndType_returnsDriver() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO employees (id) VALUES ('111');");
            stmt.execute("INSERT INTO roles (id, name) VALUES (1, 'DRIVER');");
        }

        dao.saveAssignment("111", "MONDAY", "MORNING", "2025-06-23", 1, 1);

        List<String> drivers = dao.getEmployeeIdsByRoleAndDateAndType("DRIVER", "2025-06-23", ShiftType.MORNING, 1);
        assertEquals(1, drivers.size());
        assertEquals("111", drivers.get(0));
    }

    @Test
    void testGetEmployeeIdsByRoleAndDateAndType_returnsWarehouseWorker() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO employees (id) VALUES ('222');");
            stmt.execute("INSERT INTO roles (id, name) VALUES (2, 'WAREHOUSE WORKER');");
        }

        dao.saveAssignment("222", "MONDAY", "EVENING", "2025-06-23", 2, 1);

        List<String> workers = dao.getEmployeeIdsByRoleAndDateAndType("WAREHOUSE WORKER", "2025-06-23", ShiftType.EVENING, 1);
        assertEquals(1, workers.size());
        assertEquals("222", workers.get(0));
    }

    @Test
    void testGetEmployeeIdsByRoleAndDateAndType_emptyResult() throws SQLException {
        List<String> result = dao.getEmployeeIdsByRoleAndDateAndType("DRIVER", "2030-01-01", ShiftType.MORNING, 99);
        assertTrue(result.isEmpty());
    }

    @AfterAll
    static void tearDown() throws SQLException {
        if (connection != null) connection.close();
    }
}