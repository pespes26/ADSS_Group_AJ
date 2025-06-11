package com.superli.deliveries;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.superli.deliveries.domain.core.Employee;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.domain.core.Shift;
import com.superli.deliveries.domain.core.ShiftType;

public class ShiftTest {

    private Employee createTestEmployee(String id) {
        return new Employee(
                id,
                "Test Employee",
                "Test Bank",
                5000.0,
                "Full Time",
                new Date(),
                new ArrayList<>(),
                new ArrayList<>(),
                new Role("Test Role")
        );
    }

    private Shift createTestShiftWithRole(Role role) {
        Date today = new Date();
        List<Role> requiredRoles = new ArrayList<>();
        requiredRoles.add(role);
        Map<Employee, Role> shiftEmployees = new HashMap<>();
        Employee manager = createTestEmployee("999999999");
        return new Shift("2", today, ShiftType.MORNING, DayOfWeek.MONDAY, requiredRoles, shiftEmployees, manager);
    }

    /**
     * Test adding an employee to a shift successfully.
     */
    @Test
    public void givenUnassignedEmployee_whenAddEmployeeToShift_thenEmployeeAssignedSuccessfully() {
        Role cashier = new Role("Cashier");
        Shift shift = createTestShiftWithRole(cashier);
        Employee emp = createTestEmployee("123456789");

        shift.addEmployeeToShift(emp, cashier);

        assertTrue(shift.getShiftEmployees().containsKey(emp));
        assertTrue(shift.getShiftRequiredRoles().isEmpty());
    }

    /**
     * Test that adding the same employee again does not duplicate the assignment.
     */
    @Test
    public void givenAssignedEmployee_whenAddEmployeeToShiftAgain_thenNoDuplicateAssignment() {
        Role cashier = new Role("Cashier");
        Shift shift = createTestShiftWithRole(cashier);
        Employee emp = createTestEmployee("123456789");

        shift.addEmployeeToShift(emp, cashier);
        shift.addEmployeeToShift(emp, cashier);

        assertEquals(1, shift.getShiftEmployees().size());
    }

    /**
     * Test removing an employee from a shift and restoring the required role.
     */
    @Test
    public void givenAssignedEmployee_whenRemoveEmployeeFromShift_thenEmployeeRemovedAndRoleRestored() {
        Role cashier = new Role("Cashier");
        Shift shift = createTestShiftWithRole(cashier);
        Employee emp = createTestEmployee("123456789");

        shift.addEmployeeToShift(emp, cashier);
        shift.removeEmployeeFromShift(emp);

        assertFalse(shift.getShiftEmployees().containsKey(emp));
        assertTrue(shift.getShiftRequiredRoles().contains(cashier));
    }

    /**
     * Test checking if an assigned employee is identified.
     */
    @Test
    public void givenAssignedEmployee_whenCheckIsEmployeeAssigned_thenReturnTrue() {
        Role cashier = new Role("Cashier");
        Shift shift = createTestShiftWithRole(cashier);
        Employee emp = createTestEmployee("123456789");

        shift.addEmployeeToShift(emp, cashier);

        assertTrue(shift.isEmployeeAssigned(emp));
    }

    /**
     * Test checking if an unassigned employee is not identified as assigned.
     */
    @Test
    public void givenUnassignedEmployee_whenCheckIsEmployeeAssigned_thenReturnFalse() {
        Role cashier = new Role("Cashier");
        Shift shift = createTestShiftWithRole(cashier);
        Employee emp = createTestEmployee("123456789");

        assertFalse(shift.isEmployeeAssigned(emp));
    }

    /**
     * Test checking if a shift is fully assigned when all roles are filled.
     */
    @Test
    public void givenAllRolesAssigned_whenCheckIsShiftFullyAssigned_thenReturnTrue() {
        Role cashier = new Role("Cashier");
        Shift shift = createTestShiftWithRole(cashier);
        Employee emp = createTestEmployee("123456789");

        shift.addEmployeeToShift(emp, cashier);

        assertTrue(shift.isShiftFullyAssigned());
    }

    /**
     * Test checking if a shift is not fully assigned when roles are still missing.
     */
    @Test
    public void givenRolesLeftUnassigned_whenCheckIsShiftFullyAssigned_thenReturnFalse() {
        Role cashier = new Role("Cashier");
        Shift shift = createTestShiftWithRole(cashier);

        assertFalse(shift.isShiftFullyAssigned());
    }

    /**
     * Test if a shift is recognized as past when the date is in the past.
     */
    @Test
    public void givenPastShiftDate_whenCheckIsPastShift_thenReturnTrue() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();

        Shift shift = new Shift("5", yesterday, ShiftType.MORNING, DayOfWeek.SUNDAY, new ArrayList<>(), new HashMap<>(), createTestEmployee("999999999"));

        assertTrue(shift.isPastShift());
    }

    /**
     * Test if a shift is not recognized as past when the date is in the future.
     */
    @Test
    public void givenFutureShiftDate_whenCheckIsPastShift_thenReturnFalse() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();

        Shift shift = new Shift("7", tomorrow, ShiftType.MORNING, DayOfWeek.TUESDAY, new ArrayList<>(), new HashMap<>(), createTestEmployee("999999999"));

        assertFalse(shift.isPastShift());
    }

    @Test
    void constructor_ValidData_CreatesShift() {
        // Arrange
        int id = 123;
        Date shiftDate = new Date();
        ShiftType shiftType = ShiftType.MORNING;
        DayOfWeek shiftDay = DayOfWeek.MONDAY;
        List<Role> requiredRoles = new ArrayList<>();
        Map<Employee, Role> employees = new HashMap<>();
        Employee manager = new Employee("M1", "Manager", "manager@test.com", 0.0, "Standard", new Date(), new ArrayList<>(), new ArrayList<>(), new Role("Manager"));

        // Act
        Shift shift = new Shift(String.valueOf(id), shiftDate, shiftType, shiftDay, requiredRoles, employees, manager);

        // Assert
        assertNotNull(shift);
        assertEquals(String.valueOf(id), shift.getShiftId());
        assertEquals(shiftDate, shift.getShiftDate());
        assertEquals(shiftType, shift.getShiftType());
        assertEquals(shiftDay, shift.getShiftDay());
        assertEquals(requiredRoles, shift.getShiftRequiredRoles());
        assertEquals(employees, shift.getShiftEmployees());
        assertEquals(manager, shift.getShiftManager());
    }

    @Test
    void addEmployeeToShift_ValidAssignment_AddsEmployee() {
        // Arrange
        Shift shift = createTestShift();
        Employee employee = new Employee("E1", "Test", "test@test.com", 0.0, "Standard", new Date(), new ArrayList<>(), new ArrayList<>(), new Role("Driver"));
        Role role = new Role("Driver");

        // Act
        shift.addEmployeeToShift(employee, role);

        // Assert
        assertTrue(shift.isEmployeeAssigned(employee));
        assertFalse(shift.getShiftRequiredRoles().contains(role));
    }

    @Test
    void removeEmployeeFromShift_ValidRemoval_RemovesEmployee() {
        // Arrange
        Shift shift = createTestShift();
        Employee employee = new Employee("E1", "Test", "test@test.com", 0.0, "Standard", new Date(), new ArrayList<>(), new ArrayList<>(), new Role("Driver"));
        Role role = new Role("Driver");
        shift.addEmployeeToShift(employee, role);

        // Act
        shift.removeEmployeeFromShift(employee);

        // Assert
        assertFalse(shift.isEmployeeAssigned(employee));
        assertTrue(shift.getShiftRequiredRoles().contains(role));
    }

    @Test
    void isShiftFullyAssigned_NoRequiredRoles_ReturnsTrue() {
        // Arrange
        Shift shift = createTestShift();
        Employee employee = new Employee("E1", "Test", "test@test.com", 0.0, "Standard", new Date(), new ArrayList<>(), new ArrayList<>(), new Role("Driver"));
        Role role = new Role("Driver");
        shift.addEmployeeToShift(employee, role);

        // Act & Assert
        assertTrue(shift.isShiftFullyAssigned());
    }

    @Test
    void isShiftFullyAssigned_HasRequiredRoles_ReturnsFalse() {
        // Arrange
        Shift shift = createTestShift();

        // Act & Assert
        assertFalse(shift.isShiftFullyAssigned());
    }

    @Test
    void isPastShift_ShiftDateBeforeNow_ReturnsTrue() {
        // Arrange
        Date pastDate = new Date(System.currentTimeMillis() - 86400000); // 1 day ago
        Shift shift = new Shift("1", pastDate, ShiftType.MORNING, DayOfWeek.MONDAY, new ArrayList<>(), new HashMap<>(), null);

        // Act & Assert
        assertTrue(shift.isPastShift());
    }

    @Test
    void isPastShift_ShiftDateAfterNow_ReturnsFalse() {
        // Arrange
        Date futureDate = new Date(System.currentTimeMillis() + 86400000); // 1 day in future
        Shift shift = new Shift("1", futureDate, ShiftType.MORNING, DayOfWeek.MONDAY, new ArrayList<>(), new HashMap<>(), null);

        // Act & Assert
        assertFalse(shift.isPastShift());
    }

    private Shift createTestShift() {
        List<Role> requiredRoles = new ArrayList<>();
        requiredRoles.add(new Role("Driver"));
        return new Shift("1", new Date(), ShiftType.MORNING, DayOfWeek.MONDAY, requiredRoles, new HashMap<>(), null);
    }
}
