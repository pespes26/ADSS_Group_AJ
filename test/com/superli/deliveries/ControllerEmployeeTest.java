package com.superli.deliveries;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.superli.deliveries.application.controllers.EmployeeController;
import com.superli.deliveries.domain.core.AvailableShifts;
import com.superli.deliveries.domain.core.Employee;
import com.superli.deliveries.domain.core.HRManager;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.domain.core.Shift;
import com.superli.deliveries.domain.core.ShiftType;

public class ControllerEmployeeTest {

    private Employee employee;
    private HRManager hrManager;

    @BeforeEach
    public void setUp() {
        hrManager = new HRManager();
        employee = new Employee(
                "123456789",
                "Test Employee",
                "Test Bank",
                5000.0,
                2,
                "Full Time",
                new Date(),
                new ArrayList<>(),
                new ArrayList<>(),
                new Role("Test Role")
        );
        hrManager.addEmployee(employee);
    }

    /**
     * Test viewing shifts when the employee is assigned to a shift.
     */
    @Test
    public void givenEmployeeAssignedToShift_whenViewMyShifts_thenDisplayAssignedShift() {
        Shift shift = createTestShift(DayOfWeek.SUNDAY, ShiftType.MORNING);
        Role cashier = new Role("Cashier");

        shift.getShiftRequiredRoles().add(cashier);
        shift.addEmployeeToShift(employee, cashier);
        hrManager.addShift(shift);

        EmployeeController.viewMyShifts(employee, hrManager);
        assertTrue(shift.getShiftEmployees().containsKey(employee));
    }


    /**
     * Test preventing duplicate availability entries.
     */
    @Test
    public void givenDuplicateAvailability_whenUpdateAvailability_thenDoNotAddAgain() {
        AvailableShifts existing = new AvailableShifts(DayOfWeek.SUNDAY, ShiftType.MORNING);
        employee.addAvailability(existing);

        String simulatedInput = "SUNDAY\nMORNING\ndone\n";
        simulateUserInput(simulatedInput);

        Scanner sc = new Scanner(System.in);
        EmployeeController.updateAvailability(employee, sc);

        assertEquals(1, employee.getAvailabilityConstraints().size(), "Duplicate availability should not be added");
    }

    /**
     * Test attempting to remove a non-existent availability constraint.
     */
    @Test
    public void givenNonExistentAvailability_whenRemoveAvailability_thenNoChange() {
        employee.addAvailability(new AvailableShifts(DayOfWeek.MONDAY, ShiftType.MORNING));

        String simulatedInput = "2\nWEDNESDAY\nEVENING\n0\n";
        simulateUserInput(simulatedInput);

        Scanner sc = new Scanner(System.in);
        EmployeeController.updateAvailability(employee, sc);

        assertEquals(1, employee.getAvailabilityConstraints().size(), "Non-existing availability removal should not affect");
        assertEquals(DayOfWeek.MONDAY, employee.getAvailabilityConstraints().get(0).getDay());
    }


/**

 //Tests to check on Thursday on 16:00
 //if you want to check it befor/after you need to put in the ControllerEmployee this lines in comment:
 //    if (dayOfWeek != java.util.Calendar.THURSDAY) {
 //           System.out.println("Sorry, availability can only be updated on Thursdays.");
 //           return;
 //        }
 // and remove the comment from this test


     // Test updating availability when no constraints exist initially.

    @Test
    public void givenNoAvailability_whenUpdateAvailability_thenAddNewAvailability() {
        String simulatedInput = "SUNDAY\nMORNING\ndone\n";
        simulateUserInput(simulatedInput);

        Scanner sc = new Scanner(System.in);
        ControllerEmployee.updateAvailability(employee, sc);

        assertEquals(1, employee.getAvailabilityConstraints().size());
        AvailableShifts availability = employee.getAvailabilityConstraints().get(0);
        assertEquals(DomainLayer.DayOfWeek.SUNDAY, availability.getDay());
        assertEquals(ShiftType.MORNING, availability.getShift());
    }


     //Test clearing existing availability and adding new availability.

    @Test
    public void givenExistingAvailability_whenClearAndAddNewAvailability_thenUpdateSuccessfully() {
        employee.addAvailability(new AvailableShifts(DomainLayer.DayOfWeek.MONDAY, ShiftType.EVENING));

        String simulatedInput = "1\nSUNDAY\nMORNING\ndone\n";
        simulateUserInput(simulatedInput);

        Scanner sc = new Scanner(System.in);
        ControllerEmployee.updateAvailability(employee, sc);

        assertEquals(1, employee.getAvailabilityConstraints().size());
        assertEquals(DomainLayer.DayOfWeek.SUNDAY, employee.getAvailabilityConstraints().get(0).getDay());
    }


      //Test editing existing availability: adding and removing constraints.

    @Test
    public void givenExistingAvailability_whenEditAvailability_thenAddAndRemoveSuccessfully() {
        employee.addAvailability(new AvailableShifts(DomainLayer.DayOfWeek.MONDAY, ShiftType.MORNING));

        String simulatedInput = "2\n1\nTUESDAY\nMORNING\n2\nMONDAY\nMORNING\n0\n";
        simulateUserInput(simulatedInput);

        Scanner sc = new Scanner(System.in);
        ControllerEmployee.updateAvailability(employee, sc);

        List<AvailableShifts> updated = employee.getAvailabilityConstraints();

        assertEquals(1, updated.size());
        assertEquals(DomainLayer.DayOfWeek.TUESDAY, updated.get(0).getDay());
    }


     // Test handling invalid day input during availability update.

    @Test
    public void givenInvalidDayInput_whenUpdateAvailability_thenHandleAndAddValidDay() {
        String simulatedInput = "NOTADAY\nSUNDAY\nMORNING\ndone\n";
        simulateUserInput(simulatedInput);

        Scanner sc = new Scanner(System.in);
        ControllerEmployee.updateAvailability(employee, sc);

        assertEquals(1, employee.getAvailabilityConstraints().size());
        assertEquals(DomainLayer.DayOfWeek.SUNDAY, employee.getAvailabilityConstraints().get(0).getDay());
    }


     // Test handling invalid shift input during availability update.

    @Test
    public void givenInvalidShiftInput_whenUpdateAvailability_thenHandleAndAddValidShift() {
        String simulatedInput = "SUNDAY\nBADSHIFT\nMORNING\ndone\n";
        simulateUserInput(simulatedInput);

        Scanner sc = new Scanner(System.in);
        ControllerEmployee.updateAvailability(employee, sc);

        assertEquals(1, employee.getAvailabilityConstraints().size());
        assertEquals(ShiftType.MORNING, employee.getAvailabilityConstraints().get(0).getShift());
    }*/




    private Shift createTestShift(DayOfWeek day, ShiftType shiftType) {
        Date today = new Date();
        String shiftId = UUID.randomUUID().toString();
        return new Shift(3, today, shiftType, day, new ArrayList<>(), new HashMap<>(), null);
    }

    private void simulateUserInput(String input) {
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
    }
}
