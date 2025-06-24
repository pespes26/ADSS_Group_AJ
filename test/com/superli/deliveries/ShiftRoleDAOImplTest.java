package com.superli.deliveries;

import com.superli.deliveries.dataaccess.dao.HR.ShiftRoleDAOImpl;
import com.superli.deliveries.dto.HR.ShiftRoleDTO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftRoleDAOImplTest {

    private static Connection connection;
    private ShiftRoleDAOImpl dao;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE shift_required_roles (
                    day_of_week TEXT,
                    shift_type TEXT,
                    role_id INTEGER,
                    site_id INTEGER,
                    required_count INTEGER,
                    PRIMARY KEY(day_of_week, shift_type, role_id, site_id)
                );
            """);
        }
    }

    @BeforeEach
    void init() {
        dao = new ShiftRoleDAOImpl(connection);
    }

    @Test
    void testSaveAndFindByKey() throws SQLException {
        ShiftRoleDTO dto = new ShiftRoleDTO("Sunday", "Morning", 1, 2, 1);
        dao.save(dto);
        ShiftRoleDTO found = dao.findByKey("Sunday", "Morning", 1, 1);
        assertNotNull(found);
        assertEquals(2, found.getRequiredCount());
    }

    @Test
    void testUpdateExisting() throws SQLException {
        ShiftRoleDTO dto = new ShiftRoleDTO("Sunday", "Morning", 1, 5, 1);
        dao.save(dto);
        dto.setRequiredCount(10);
        dao.save(dto);
        ShiftRoleDTO updated = dao.findByKey("Sunday", "Morning", 1, 1);
        assertEquals(10, updated.getRequiredCount());
    }

    @Test
    void testDelete() throws SQLException {
        ShiftRoleDTO dto = new ShiftRoleDTO("Monday", "Evening", 2, 3, 1);
        dao.save(dto);
        dao.delete("Monday", "Evening", 2, 1);
        assertNull(dao.findByKey("Monday", "Evening", 2, 1));
    }

    @Test
    void testFindAll() throws SQLException {
        dao.save(new ShiftRoleDTO("Tuesday", "Morning", 3, 2, 1));
        dao.save(new ShiftRoleDTO("Tuesday", "Evening", 4, 1, 1));
        List<ShiftRoleDTO> all = dao.findAll();
        assertTrue(all.size() >= 2);
    }

    @Test
    void testGetRequiredRolesForShift() throws SQLException {
        dao.save(new ShiftRoleDTO("Wednesday", "Morning", 5, 3, 1));
        dao.save(new ShiftRoleDTO("Wednesday", "Morning", 6, 2, 1));
        Map<Integer, Integer> roles = dao.getRequiredRolesForShift("Wednesday", "Morning", 1);
        assertEquals(2, roles.size());
        assertEquals(3, roles.get(5));
        assertEquals(2, roles.get(6));
    }

    @Test
    void testDeleteAllForShift() throws SQLException {
        dao.save(new ShiftRoleDTO("Thursday", "Evening", 7, 1, 1));
        dao.save(new ShiftRoleDTO("Thursday", "Evening", 8, 1, 1));
        dao.deleteAllForShift("Thursday", "Evening", 1);
        assertNull(dao.findByKey("Thursday", "Evening", 7, 1));
        assertNull(dao.findByKey("Thursday", "Evening", 8, 1));
    }

    @AfterAll
    static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}