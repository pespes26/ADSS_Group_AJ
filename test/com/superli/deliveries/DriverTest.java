package com.superli.deliveries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import com.superli.deliveries.domain.core.*;

class DriverTest {

    private Driver testDriver;
    private Role driverRole;
    private Role managerRole;

    @BeforeEach
    void setUp() {
        driverRole = new Role("Driver");
        managerRole = new Role("Manager");
        testDriver = new Driver(
            "D123",
            "John Doe",
            "123-456-789",
            5000.0,
            0,
            "Standard",
            new Date(),
            new ArrayList<>(),
            new ArrayList<>(),
            driverRole,
            LicenseType.C
        );
    }

    // Core Functionality Tests
    @Test
    void constructor_ValidData_CreatesDriver() {
        assertNotNull(testDriver);
        assertEquals("D123", testDriver.getDriverId());
        assertEquals("John Doe", testDriver.getFullName());
        assertEquals(LicenseType.C, testDriver.getLicenseType());
        assertTrue(testDriver.isAvailable());
        assertEquals(5000.0, testDriver.getSalary());
    }

    @Test
    void constructor_NullId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Driver(null, "Test Driver", "123-456-789", 5000.0, 0, "Standard",
                new Date(), new ArrayList<>(), new ArrayList<>(), driverRole, LicenseType.C);
        });
    }

    @Test
    void constructor_NullName_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Driver("D123", null, "123-456-789", 5000.0, 0, "Standard",
                new Date(), new ArrayList<>(), new ArrayList<>(), driverRole, LicenseType.C);
        });
    }

    // Role Qualification Tests
    @Test
    void addRoleQualification_ValidRole_AddsRole() {
        testDriver.addRoleQualification(managerRole);
        assertTrue(testDriver.isQualified(managerRole));
    }

    @Test
    void addRoleQualification_DuplicateRole_DoesNotAddDuplicate() {
        testDriver.addRoleQualification(driverRole);
        int initialSize = testDriver.getRoleQualifications().size();
        testDriver.addRoleQualification(driverRole);
        assertEquals(initialSize, testDriver.getRoleQualifications().size());
    }

    // Shift Management Tests
    @Test
    void addAvailableShift_ValidShift_AddsShift() {
        AvailableShifts shift = new AvailableShifts(DayOfWeek.MONDAY, ShiftType.MORNING);
        testDriver.addAvailableShift(shift);
        assertTrue(testDriver.isAvailable(DayOfWeek.MONDAY, ShiftType.MORNING));
    }

    @Test
    void addAvailableShift_DuplicateShift_DoesNotAddDuplicate() {
        AvailableShifts shift = new AvailableShifts(DayOfWeek.MONDAY, ShiftType.MORNING);
        testDriver.addAvailableShift(shift);
        int initialSize = testDriver.getAvailabilityConstraints().size();
        testDriver.addAvailableShift(shift);
        assertEquals(initialSize, testDriver.getAvailabilityConstraints().size());
    }

    @Test
    void clearAvailability_RemovesAllShifts() {
        AvailableShifts shift1 = new AvailableShifts(DayOfWeek.MONDAY, ShiftType.MORNING);
        AvailableShifts shift2 = new AvailableShifts(DayOfWeek.TUESDAY, ShiftType.EVENING);
        testDriver.addAvailableShift(shift1);
        testDriver.addAvailableShift(shift2);
        
        testDriver.clearAvailability();
        assertFalse(testDriver.isAvailable(DayOfWeek.MONDAY, ShiftType.MORNING));
        assertFalse(testDriver.isAvailable(DayOfWeek.TUESDAY, ShiftType.EVENING));
    }

    // License and Availability Tests
    @Test
    void setLicenseType_ValidType_UpdatesLicenseType() {
        testDriver.setLicenseType(LicenseType.C1);
        assertEquals(LicenseType.C1, testDriver.getLicenseType());
    }

    @Test
    void setAvailable_UpdatesAvailability() {
        testDriver.setAvailable(false);
        assertFalse(testDriver.isAvailable());
        testDriver.setAvailable(true);
        assertTrue(testDriver.isAvailable());
    }

    // Salary Tests
    @Test
    void setSalary_NegativeSalary_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            testDriver.setSalary(-1000.0);
        });
    }

    @Test
    void setSalary_ValidSalary_UpdatesSalary() {
        testDriver.setSalary(6000.0);
        assertEquals(6000.0, testDriver.getSalary());
    }

    // Equality Tests
    @Test
    void equals_SameId_ShouldBeEqual() {
        Driver driver1 = new Driver("D123", "John Doe", "123-456-789", 5000.0, 0, "Standard",
            new Date(), new ArrayList<>(), new ArrayList<>(), driverRole, LicenseType.C);
        Driver driver2 = new Driver("D123", "Jane Doe", "987-654-321", 6000.0, 1, "Premium",
            new Date(), new ArrayList<>(), new ArrayList<>(), managerRole, LicenseType.B);
        assertEquals(driver1, driver2);
        assertEquals(driver1.hashCode(), driver2.hashCode());
    }

    @Test
    void equals_DifferentId_ShouldNotBeEqual() {
        Driver driver1 = new Driver("D123", "John Doe", "123-456-789", 5000.0, 0, "Standard",
            new Date(), new ArrayList<>(), new ArrayList<>(), driverRole, LicenseType.C);
        Driver driver2 = new Driver("D456", "John Doe", "123-456-789", 5000.0, 0, "Standard",
            new Date(), new ArrayList<>(), new ArrayList<>(), driverRole, LicenseType.C);
        assertNotEquals(driver1, driver2);
    }
}