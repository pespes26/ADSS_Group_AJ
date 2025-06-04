package test.HR;

import com.superli.deliveries.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class HRManagerTest {

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

    /**
     * Test adding a new employee successfully.
     */
    @Test
    public void givenNewEmployee_whenAddEmployee_thenEmployeeAddedSuccessfully() {
        HRManager hr = new HRManager();
        Employee emp = createTestEmployee("123456789");

        boolean added = hr.addEmployee(emp);

        assertTrue(added);
        assertEquals(1, hr.getEmployees().size());
        assertEquals(emp, hr.getEmployees().get(0));
    }

    /**
     * Test that adding a duplicate employee fails.
     */
    @Test
    public void givenDuplicateEmployee_whenAddEmployee_thenAdditionFails() {
        HRManager hr = new HRManager();
        Employee emp1 = createTestEmployee("123456789");
        Employee emp2 = createTestEmployee("123456789");

        hr.addEmployee(emp1);
        boolean added = hr.addEmployee(emp2);

        assertFalse(added);
        assertEquals(1, hr.getEmployees().size());
    }

    /**
     * Test removing an existing employee.
     */
    @Test
    public void givenExistingEmployee_whenRemoveEmployee_thenEmployeeRemoved() {
        HRManager hr = new HRManager();
        Employee emp = createTestEmployee("123456789");

        hr.addEmployee(emp);
        hr.removeEmployee(emp);

        assertEquals(0, hr.getEmployees().size());
    }

    /**
     * Test finding an existing employee by ID.
     */
    @Test
    public void givenExistingEmployee_whenFindEmployeeById_thenReturnEmployee() {
        HRManager hr = new HRManager();
        Employee emp = createTestEmployee("123456789");

        hr.addEmployee(emp);
        Employee found = hr.FindEmployeeByID("123456789");

        assertNotNull(found);
        assertEquals("Test Employee", found.getFullName());
    }

    /**
     * Test finding a non-existent employee by ID.
     */
    @Test
    public void givenNonExistentEmployeeId_whenFindEmployeeById_thenReturnNull() {
        HRManager hr = new HRManager();

        Employee found = hr.FindEmployeeByID("987654321");

        assertNull(found);
    }

    /**
     * Test removing and archiving an existing employee.
     */
    @Test
    public void givenExistingEmployee_whenRemoveAndArchiveEmployee_thenArchivedSuccessfully() {
        HRManager hr = new HRManager();
        Employee emp = createTestEmployee("123456789");

        hr.addEmployee(emp);
        hr.removeAndArchiveEmployee(emp);

        assertEquals(0, hr.getEmployees().size());
        assertEquals(1, hr.getArchivedEmployee().getArchivedEmployees().size());
    }

    /**
     * Test adding a new role successfully.
     */
    @Test
    public void givenNewRole_whenAddRole_thenRoleAddedSuccessfully() {
        HRManager hr = new HRManager();
        Role role = new Role("Cashier");

        hr.addRole(role);

        assertEquals(1, hr.getAllRoles().size());
        assertEquals(role, hr.getAllRoles().get(0));
    }

    /**
     * Test that adding a duplicate role does not create duplicates.
     */
    @Test
    public void givenDuplicateRole_whenAddRole_thenOnlyOneRoleExists() {
        HRManager hr = new HRManager();
        Role role = new Role("Cashier");

        hr.addRole(role);
        hr.addRole(role);

        assertEquals(1, hr.getAllRoles().size());
    }

    /**
     * Test adding a new shift successfully.
     */
    @Test
    public void givenNewShift_whenAddShift_thenShiftAddedSuccessfully() {
        HRManager hr = new HRManager();
        Date today = new Date();
        Shift shift = new Shift(today, ShiftType.MORNING, DayOfWeek.MONDAY, new ArrayList<>(), new HashMap<>(), createTestEmployee("123456789"));

        hr.addShift(shift);

        assertEquals(1, hr.getAllShifts().size());
        assertEquals(shift, hr.getAllShifts().get(0));
    }

    /**
     * Test defining required roles for a shift.
     */
    @Test
    public void givenShiftAndRoles_whenDefineRequiredRoles_thenRolesAssigned() {
        HRManager hr = new HRManager();
        Date today = new Date();
        Shift shift = new Shift(today, ShiftType.MORNING, DayOfWeek.MONDAY, new ArrayList<>(), new HashMap<>(), createTestEmployee("111111111"));
        hr.addShift(shift);

        List<Role> requiredRoles = List.of(new Role("Cashier"), new Role("Stock Keeper"));
        hr.defineRequiredRolesForShift(shift, requiredRoles);

        assertEquals(2, shift.getShiftRequiredRoles().size());
        assertTrue(shift.getShiftRequiredRoles().contains(new Role("Cashier")));
        assertTrue(shift.getShiftRequiredRoles().contains(new Role("Stock Keeper")));
    }

    /**
     * Test adding a single required role to a shift.
     */
    @Test
    public void givenShift_whenAddRequiredRole_thenRoleAddedToShift() {
        HRManager hr = new HRManager();
        Date today = new Date();
        Shift shift = new Shift(today, ShiftType.MORNING, DayOfWeek.MONDAY, new ArrayList<>(), new HashMap<>(), createTestEmployee("222222222"));
        hr.addShift(shift);

        Role newRole = new Role("Security");
        hr.addRequiredRolesForShift(shift, newRole);

        assertEquals(1, shift.getShiftRequiredRoles().size());
        assertTrue(shift.getShiftRequiredRoles().contains(newRole));
    }

    /**
     * Test assigning an available and qualified employee to a shift.
     */
    @Test
    public void givenQualifiedAvailableEmployee_whenAddEmployeeToShift_thenEmployeeAssigned() {
        HRManager hr = new HRManager();
        Date today = new Date();
        List<Role> requiredRoles = List.of(new Role("Cashier"));
        Map<Employee, Role> shiftEmployees = new HashMap<>();
        Employee manager = createTestEmployee("111111111");
        Shift shift = new Shift(today, ShiftType.MORNING, DayOfWeek.MONDAY, new ArrayList<>(requiredRoles), shiftEmployees, manager);

        Employee employee = createTestEmployee("333333333");
        employee.addRoleQualification(requiredRoles.get(0));
        employee.addAvailableShift(new AvailableShifts(DayOfWeek.MONDAY, ShiftType.MORNING));

        hr.addEmployee(employee);
        hr.addShift(shift);

        hr.addEmployeeToShift(shift, employee);

        assertTrue(shift.getShiftEmployees().containsKey(employee));
        assertEquals(requiredRoles.get(0), shift.getShiftEmployees().get(employee));
    }

    /**
     * Test removing an assigned employee from a shift and restoring the role.
     */
    @Test
    public void givenAssignedEmployee_whenRemoveEmployeeFromShift_thenEmployeeRemovedAndRoleRestored() {
        HRManager hr = new HRManager();
        Date today = new Date();
        List<Role> requiredRoles = new ArrayList<>();
        Role cashierRole = new Role("Cashier");

        Map<Employee, Role> shiftEmployees = new HashMap<>();
        Employee manager = createTestEmployee("111111111");
        Shift shift = new Shift(today, ShiftType.MORNING, DayOfWeek.MONDAY, requiredRoles, shiftEmployees, manager);

        Employee employee = createTestEmployee("444444444");
        employee.addRoleQualification(cashierRole);
        employee.addAvailableShift(new AvailableShifts(DayOfWeek.MONDAY, ShiftType.MORNING));

        hr.addEmployee(employee);
        hr.addShift(shift);
        hr.addRequiredRolesForShift(shift, cashierRole);
        hr.addEmployeeToShift(shift, employee);

        hr.removeEmployeeFromShift(shift, employee);

        assertFalse(shift.getShiftEmployees().containsKey(employee));
        assertTrue(shift.getShiftRequiredRoles().contains(cashierRole));
    }
}
