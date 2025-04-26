import DomainLayer.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

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
        return new Shift(today, ShiftType.MORNING, DayOfWeek.MONDAY, requiredRoles, shiftEmployees, manager);
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

        Shift shift = new Shift(yesterday, ShiftType.MORNING, DayOfWeek.SUNDAY, new ArrayList<>(), new HashMap<>(), createTestEmployee("999999999"));

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

        Shift shift = new Shift(tomorrow, ShiftType.MORNING, DayOfWeek.TUESDAY, new ArrayList<>(), new HashMap<>(), createTestEmployee("999999999"));

        assertFalse(shift.isPastShift());
    }
}
