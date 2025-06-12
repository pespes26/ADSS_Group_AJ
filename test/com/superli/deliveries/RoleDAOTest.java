package com.superli.deliveries;

import com.superli.deliveries.dataaccess.dao.HR.RoleDAO;
import com.superli.deliveries.dataaccess.dao.HR.RoleDAOImpl;
import com.superli.deliveries.dto.HR.RoleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RoleDAOTest {

    private RoleDAO roleDAO;

    @BeforeEach
    public void setup() throws SQLException {
        // מחובר למסד נתונים SQLite (בזיכרון או קובץ זמני)
        Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
        roleDAO = new RoleDAOImpl(conn);

        // אתחול טבלת roles אם צריך
        conn.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS roles (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            );
        """);
    }

    @Test
    public void testSaveAndFindRoleByName() throws SQLException {
        // Arrange
        RoleDTO role = new RoleDTO();
        role.setName("Warehouse Worker");

        // Act
        RoleDTO saved = roleDAO.save(role);
        Optional<RoleDTO> fetched = roleDAO.findByName("Warehouse Worker");

        // Assert
        assertTrue(fetched.isPresent());
        assertEquals("Warehouse Worker", fetched.get().getName());
        assertTrue(fetched.get().getId() > 0);  // ודא שנשמר עם מזהה
    }

    @Test
    public void testDuplicateRoleName_shouldThrowSQLException() throws SQLException {
        // Arrange
        RoleDTO role1 = new RoleDTO();
        role1.setName("Driver");

        RoleDTO role2 = new RoleDTO();
        role2.setName("Driver"); // אותו שם

        // Act
        roleDAO.save(role1);

        // Assert
        assertThrows(SQLException.class, () -> roleDAO.save(role2));
    }
    @Test
    public void testDeleteRoleById_thenRoleNotFound() throws SQLException {
        // Arrange
        RoleDTO role = new RoleDTO();
        role.setName("Transport Manager");
        RoleDTO saved = roleDAO.save(role);

        // Act
        roleDAO.deleteById(saved.getId());

        // Assert
        Optional<RoleDTO> result = roleDAO.findById(saved.getId());
        assertFalse(result.isPresent());
    }
    /*@Test
    public void testDuplicateRoleName_shouldOnlyCreateOneRecord() throws SQLException {
        RoleDTO role1 = new RoleDTO();
        role1.setName("Driver");
        roleDAO.save(role1);

        RoleDTO role2 = new RoleDTO();
        role2.setName("Driver");
        roleDAO.save(role2);  // לא תיזרק חריגה

        List<RoleDTO> allRoles = roleDAO.findAll();
        long count = allRoles.stream()
                .filter(r -> r.getName().equals("Driver"))
                .count();
        assertEquals(1, count);  // רק אחת צריכה להופיע
    }*/


}
