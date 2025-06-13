package com.superli.deliveries;

import com.superli.deliveries.dataaccess.dao.HR.RoleDAO;
import com.superli.deliveries.dataaccess.dao.HR.RoleDAOImpl;
import com.superli.deliveries.dto.HR.RoleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RoleDAOTest {

    private RoleDAO roleDAO;

    private Connection conn;

   @BeforeEach
    public void setup() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:deliveries.db");
        roleDAO = new RoleDAOImpl(conn);

        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE IF EXISTS roles;");
        stmt.execute("DELETE FROM sqlite_sequence WHERE name = 'roles';");

        stmt.execute("""
        CREATE TABLE roles (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL UNIQUE
        );
    """);
    }



   @Test
    public void testSaveAndFindRoleByName() throws SQLException {
        RoleDTO role = new RoleDTO("Warehouse Worker");
        RoleDTO saved = roleDAO.save(role);

        Optional<RoleDTO> fetched = roleDAO.findByName("Warehouse Worker");

        assertTrue(fetched.isPresent());
        assertEquals("Warehouse Worker", fetched.get().getName());
        assertTrue(fetched.get().getId() > 0);
    }

    @Test
    public void testDuplicateRoleName_shouldOnlyCreateOneRecord() throws SQLException {
        RoleDTO role1 = new RoleDTO("Driver");
        RoleDTO saved1 = roleDAO.save(role1);

        RoleDTO role2 = new RoleDTO("Driver");
        RoleDTO saved2 = roleDAO.save(role2);

        assertEquals(saved1.getId(), saved2.getId());

        List<RoleDTO> allRoles = roleDAO.findAll();
        long count = allRoles.stream().filter(r -> r.getName().equals("Driver")).count();
        assertEquals(1, count);
    }

    @Test
    public void testDeleteRoleById_thenRoleNotFound() throws SQLException {
        RoleDTO role = new RoleDTO("Transport Manager");
        RoleDTO saved = roleDAO.save(role);

        roleDAO.deleteById(saved.getId());

        Optional<RoleDTO> result = roleDAO.findById(saved.getId());
        assertFalse(result.isPresent());
    }

    @Test
    public void testFindAllRoles_returnsCorrectList() throws SQLException {
        roleDAO.save(new RoleDTO("Driver"));
        roleDAO.save(new RoleDTO("Warehouse Worker"));
        roleDAO.save(new RoleDTO("Transport Manager"));

        List<RoleDTO> roles = roleDAO.findAll();
        assertEquals(3, roles.size());

        List<String> roleNames = roles.stream().map(RoleDTO::getName).toList();
        assertTrue(roleNames.contains("Driver"));
        assertTrue(roleNames.contains("Warehouse Worker"));
        assertTrue(roleNames.contains("Transport Manager"));
    }

    @Test
    public void testFindById_existingRole_returnsCorrectRole() throws SQLException {
        RoleDTO role = new RoleDTO("Driver");
        RoleDTO saved = roleDAO.save(role);

        Optional<RoleDTO> fetched = roleDAO.findById(saved.getId());

        assertTrue(fetched.isPresent());
        assertEquals("Driver", fetched.get().getName());
    }

    @Test
    public void testFindById_nonExistent_returnsEmpty() throws SQLException {
        Optional<RoleDTO> result = roleDAO.findById(9999);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteNonExistingRole_doesNotThrow() {
        assertDoesNotThrow(() -> roleDAO.deleteById(9999));
    }

    @Test
    public void testRoleNameWithSpecialCharacters() throws SQLException {
        String specialName = "Singer";
        RoleDTO role = new RoleDTO(specialName);
        RoleDTO saved = roleDAO.save(role);

        Optional<RoleDTO> fetched = roleDAO.findByName(specialName);
        assertTrue(fetched.isPresent());
        assertEquals(saved.getId(), fetched.get().getId());
    }

    @Test
    public void testDuplicateRoleName_returnsSameRole() throws SQLException {
        RoleDTO role1 = new RoleDTO("Driver");
        RoleDTO saved1 = roleDAO.save(role1);

        RoleDTO role2 = new RoleDTO("Driver");
        RoleDTO saved2 = roleDAO.save(role2);

        assertEquals(saved1.getId(), saved2.getId());
    }

   @Test
    public void testTransportManagerAddedCorrectly() throws SQLException {
        RoleDTO saved = roleDAO.save(new RoleDTO("Transport Manager"));
        Optional<RoleDTO> result = roleDAO.findByName("Transport Manager");

        assertTrue(result.isPresent());
        assertEquals(saved.getId(), result.get().getId());
    }

}
