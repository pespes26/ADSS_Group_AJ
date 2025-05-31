import DomainLayer.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerManagerTest {

    private HRManager hrManager;

    @BeforeEach
    public void setUp() {
        hrManager = new HRManager();
        ControllerManager.setHRManager(hrManager);
    }

    /**
     * Test setting and getting a new HRManager instance.
     */
    @Test
    public void givenNewHRManager_whenSetAndGetHRManager_thenReturnSameInstance() {
        HRManager newManager = new HRManager();
        ControllerManager.setHRManager(newManager);
        assertSame(newManager, ControllerManager.getHRManager());
    }

    /**
     * Test checking if a numeric string is recognized as numeric.
     */
    @Test
    public void givenNumericString_whenCheckIsNumeric_thenReturnTrue() {
        assertTrue(ControllerManager.isNumeric("123456789"));
    }

    /**
     * Test checking if a non-numeric string is correctly recognized as not numeric.
     */
    @Test
    public void givenNonNumericString_whenCheckIsNumeric_thenReturnFalse() {
        assertFalse(ControllerManager.isNumeric("abc123"));
        assertFalse(ControllerManager.isNumeric(""));
        assertFalse(ControllerManager.isNumeric(null));
    }

    /**
     * Test adding a new role to the system successfully.
     */
    @Test
    public void givenNewRole_whenAddRoleToSystem_thenRoleExistsInSystem() {
        Role role = new Role("Driver");
        hrManager.addRole(role);

        assertTrue(hrManager.getAllRoles().contains(role));
    }

    /**
     * Test adding a role to an employee.
     */
    @Test
    public void givenEmployeeAndRole_whenAddRoleToEmployee_thenEmployeeHasRole() {
        Role role = new Role("Driver");
        hrManager.addRole(role);

        Employee emp = createTestEmployee("123456789");
        hrManager.addEmployee(emp);

        emp.addRoleQualification(role);

        assertTrue(emp.getRoleQualifications().contains(role));
    }

    /**
     * Test removing an existing employee by ID and archiving them.
     */
    @Test
    public void givenExistingEmployee_whenRemoveEmployeeById_thenEmployeeArchived() {
        Employee emp = createTestEmployee("123456789");
        hrManager.addEmployee(emp);

        hrManager.removeAndArchiveEmployee(emp);

        assertFalse(hrManager.getEmployees().contains(emp));
        assertTrue(hrManager.getArchivedEmployee().getArchivedEmployees().contains(emp));
    }

    /**
     * Test manually creating shifts for the upcoming week.
     */
    @Test
    public void givenEmptySystem_whenCreateShiftsForTheWeekManually_thenFourteenShiftsCreated() {
        LocalDate upcomingSunday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY));
        for (int i = 0; i < 7; i++) {
            LocalDate day = upcomingSunday.plusDays(i);
            DomainLayer.DayOfWeek dayEnum = DomainLayer.DayOfWeek.valueOf(day.getDayOfWeek().toString());
            Date date = java.sql.Date.valueOf(day);
            hrManager.addShift(new Shift(date, ShiftType.MORNING, dayEnum, new ArrayList<>(), new HashMap<>(), null));
            hrManager.addShift(new Shift(date, ShiftType.EVENING, dayEnum, new ArrayList<>(), new HashMap<>(), null));
        }

        assertEquals(14, hrManager.getAllShifts().size());
    }

    /**
     * Test assigning an available employee to a shift manually.
     */
    @Test
    public void givenAvailableEmployee_whenAssignEmployeeToShiftManually_thenEmployeeAssignedToShift() {
        Role cashier = new Role("Cashier");
        hrManager.addRole(cashier);

        Employee emp = createTestEmployee("123456789");
        emp.addRoleQualification(cashier);
        emp.addAvailableShift(new AvailableShifts(DomainLayer.DayOfWeek.SUNDAY, ShiftType.MORNING));
        hrManager.addEmployee(emp);

        Date today = new Date();
        Shift shift = new Shift(today, ShiftType.MORNING, DomainLayer.DayOfWeek.SUNDAY, new ArrayList<>(List.of(cashier)), new HashMap<>(), null);
        hrManager.addShift(shift);

        hrManager.addEmployeeToShift(shift, emp);

        assertTrue(shift.getShiftEmployees().containsKey(emp));
    }

    /**
     * Test removing an assigned employee from a shift and restoring their role.
     */
    @Test
    public void givenAssignedEmployee_whenRemoveEmployeeFromShift_thenEmployeeRemovedAndRoleRestored() {
        Role cashier = new Role("Cashier");
        hrManager.addRole(cashier);

        Employee emp = createTestEmployee("123456789");
        emp.addRoleQualification(cashier);
        emp.addAvailableShift(new AvailableShifts(DomainLayer.DayOfWeek.SUNDAY, ShiftType.MORNING));
        hrManager.addEmployee(emp);

        Date today = new Date();
        Shift shift = new Shift(today, ShiftType.MORNING, DomainLayer.DayOfWeek.SUNDAY, new ArrayList<>(List.of(cashier)), new HashMap<>(), null);
        hrManager.addShift(shift);

        hrManager.addEmployeeToShift(shift, emp);
        hrManager.removeEmployeeFromShift(shift, emp);

        assertFalse(shift.getShiftEmployees().containsKey(emp));
        assertTrue(shift.getShiftRequiredRoles().contains(cashier));
    }

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
}
