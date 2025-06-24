package com.superli.deliveries;

import com.superli.deliveries.dataaccess.dao.HR.AvailableShiftDAO;
import com.superli.deliveries.domain.core.AvailableShifts;
import com.superli.deliveries.domain.core.Employee;
import com.superli.deliveries.domain.core.ShiftType;
import com.superli.deliveries.application.controllers.EmployeeController;
import com.superli.deliveries.dto.HR.AvailableShiftDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Date;
import java.time.DayOfWeek;
import java.util.ArrayList;

public class EmployeeServiceTest {
    private Employee createEmptyEmployee(String id) {
        return new Employee(
                id, "Test User", "123456", 5000, 1, "Full Time", new Date(System.currentTimeMillis()),
                new ArrayList<>(), new ArrayList<>(), null
        );
    }

    @Test
    void givenEmployeeHasAvailability_whenShiftExistsCalled_thenReturnsTrue() {
        Employee emp = createEmptyEmployee("1");
        emp.addAvailability(new AvailableShifts(DayOfWeek.SUNDAY, ShiftType.MORNING));

        boolean result = EmployeeController.shiftExists(emp, DayOfWeek.SUNDAY, ShiftType.MORNING);

        assertTrue(result);
    }

    @Test
    void givenEmployeeWithoutAvailability_whenShiftExistsCalled_thenReturnsFalse() {
        Employee emp = createEmptyEmployee("2");

        boolean result = EmployeeController.shiftExists(emp, DayOfWeek.MONDAY, ShiftType.EVENING);

        assertFalse(result);
    }

    @Test
    void givenValidDayString_whenParseDay_thenReturnsCorrectEnum() {
        DayOfWeek result = EmployeeController.parseDay("THURSDAY");

        assertEquals(DayOfWeek.THURSDAY, result);
    }

    @Test
    void givenInvalidDayString_whenParseDay_thenReturnsNull() {
        DayOfWeek result = EmployeeController.parseDay("FUNDAY");

        assertNull(result);
    }

    @Test
    void givenNewAvailability_whenAddNewAvailability_thenEmployeeHasNewConstraint() {
        Employee emp = createEmptyEmployee("3");

        EmployeeController.addNewAvailability(emp, DayOfWeek.TUESDAY, ShiftType.EVENING);

        assertTrue(emp.isAvailable(DayOfWeek.TUESDAY, ShiftType.EVENING));
    }

    @Test
    void givenDuplicateAvailability_whenAddNewAvailability_thenNoDuplicatesCreated() {
        Employee emp = createEmptyEmployee("4");
        emp.addAvailability(new AvailableShifts(DayOfWeek.FRIDAY, ShiftType.MORNING));

        EmployeeController.addNewAvailability(emp, DayOfWeek.FRIDAY, ShiftType.MORNING);

        long count = emp.getAvailabilityConstraints().stream()
                .filter(a -> a.getDay() == DayOfWeek.FRIDAY && a.getShift() == ShiftType.MORNING)
                .count();

        assertEquals(1, count);
    }

    @Test
    void givenExistingAvailability_whenRemoveAvailability_thenRemovedSuccessfully() {
        Employee emp = createEmptyEmployee("5");
        emp.addAvailability(new AvailableShifts(DayOfWeek.SATURDAY, ShiftType.EVENING));

        EmployeeController.removeAvailability(emp, DayOfWeek.SATURDAY, ShiftType.EVENING);

        assertFalse(emp.isAvailable(DayOfWeek.SATURDAY, ShiftType.EVENING));
    }

    @Test
    void givenNonExistingAvailability_whenRemoveAvailability_thenNoCrashAndNothingRemoved() {
        Employee emp = createEmptyEmployee("6");
        emp.addAvailability(new AvailableShifts(DayOfWeek.SUNDAY, ShiftType.MORNING));

        EmployeeController.removeAvailability(emp, DayOfWeek.MONDAY, ShiftType.EVENING);

        assertEquals(1, emp.getAvailabilityConstraints().size());
    }

    @Test
    void givenEmployeeWithMultipleAvailabilities_whenShiftExists_thenCorrectlyMatchesEach() {
        Employee emp = createEmptyEmployee("7");
        emp.addAvailability(new AvailableShifts(DayOfWeek.MONDAY, ShiftType.EVENING));
        emp.addAvailability(new AvailableShifts(DayOfWeek.THURSDAY, ShiftType.MORNING));

        assertTrue(EmployeeController.shiftExists(emp, DayOfWeek.MONDAY, ShiftType.EVENING));
        assertTrue(EmployeeController.shiftExists(emp, DayOfWeek.THURSDAY, ShiftType.MORNING));
        assertFalse(EmployeeController.shiftExists(emp, DayOfWeek.WEDNESDAY, ShiftType.EVENING));
    }

    @Test
    void givenInvalidEmployeeId_whenAddNewAvailability_thenNoCrashThrown() {
        Employee emp = createEmptyEmployee("INVALID");

        assertDoesNotThrow(() ->
                EmployeeController.addNewAvailability(emp, DayOfWeek.TUESDAY, ShiftType.MORNING)
        );
    }



}