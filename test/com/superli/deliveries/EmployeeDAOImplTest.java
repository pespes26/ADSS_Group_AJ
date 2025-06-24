
package com.superli.deliveries;

import com.superli.deliveries.dataaccess.dao.HR.EmployeeDAOImpl;
import com.superli.deliveries.dto.HR.EmployeeDTO;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeDAOImplTest {

    private static Connection connection;
    private EmployeeDAOImpl employeeDAO;

    @BeforeAll
    static void setupDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE employees (
                    id TEXT PRIMARY KEY,
                    name TEXT,
                    bank_account TEXT,
                    salary REAL,
                    employment_terms TEXT,
                    start_date TEXT,
                    site_id INTEGER
                );
            """);
            stmt.executeUpdate("""
                CREATE TABLE archived_employees (
                    employee_id TEXT PRIMARY KEY,
                    name TEXT,
                    bank_account TEXT,
                    salary REAL,
                    employment_terms TEXT,
                    start_date TEXT,
                    site_id INTEGER
                );
            """);
        }
    }

    @BeforeEach
    void init() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM employees");
            stmt.executeUpdate("DELETE FROM archived_employees");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        employeeDAO = new EmployeeDAOImpl(connection);
    }

    private EmployeeDTO createTestEmployee(String id, String name) {
        return new EmployeeDTO(id, name, "111-222", 5500, "Full-Time", "2023-01-01", 1);
    }

    @Test
    void testSaveAndFindById() throws SQLException {
        EmployeeDTO dto = createTestEmployee("1", "Alice");
        employeeDAO.save(dto);

        Optional<EmployeeDTO> result = employeeDAO.findById("1");
        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getFullName());
    }

    @Test
    void testFindAllWithNoRecords() throws SQLException {
        List<EmployeeDTO> all = employeeDAO.findAll();
        assertTrue(all.isEmpty());
    }

    @Test
    void testFindAllWithMultipleRecords() throws SQLException {
        employeeDAO.save(createTestEmployee("2", "Bob"));
        employeeDAO.save(createTestEmployee("3", "Charlie"));

        List<EmployeeDTO> all = employeeDAO.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testUpdateEmployeeRecord() throws SQLException {
        EmployeeDTO dto = createTestEmployee("4", "David");
        employeeDAO.save(dto);

        dto.setFullName("Updated David");
        employeeDAO.save(dto);

        Optional<EmployeeDTO> result = employeeDAO.findById("4");
        assertTrue(result.isPresent());
        assertEquals("Updated David", result.get().getFullName());
    }

    @Test
    void testDeleteByIdAndArchive() throws SQLException {
        EmployeeDTO dto = createTestEmployee("5", "Eve");
        employeeDAO.save(dto);
        employeeDAO.deleteById("5");

        Optional<EmployeeDTO> result = employeeDAO.findById("5");
        assertTrue(result.isEmpty());

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM archived_employees WHERE employee_id = ?")) {
            stmt.setString(1, "5");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("Eve", rs.getString("name"));
        }
    }

    @Test
    void testDeleteByIdThrowsForMissing() {
        SQLException thrown = assertThrows(SQLException.class, () -> {
            employeeDAO.deleteById("MISSING");
        });

        assertNotNull(thrown.getCause());
        assertTrue(thrown.getCause().getMessage().contains("not found"));
    }



    @Test
    void testSaveWithExistingIdOverwrites() throws SQLException {
        employeeDAO.save(createTestEmployee("6", "Original"));
        employeeDAO.save(createTestEmployee("6", "Updated"));

        Optional<EmployeeDTO> result = employeeDAO.findById("6");
        assertTrue(result.isPresent());
        assertEquals("Updated", result.get().getFullName());
    }

    @Test
    void testSaveMultipleEmployees() throws SQLException {
        for (int i = 10; i < 15; i++) {
            employeeDAO.save(createTestEmployee(String.valueOf(i), "Emp" + i));
        }
        List<EmployeeDTO> employees = employeeDAO.findAll();
        assertTrue(employees.size() >= 5);
    }

    @Test
    void testArchiveEmployeeDataCorrectly() throws SQLException {
        EmployeeDTO dto = createTestEmployee("16", "ToArchive");
        employeeDAO.save(dto);
        employeeDAO.deleteById("16");

        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM archived_employees WHERE employee_id = ?")) {
            stmt.setString(1, "16");
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());
            assertEquals("ToArchive", rs.getString("name"));
        }
    }

    @AfterAll
    static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}