package com.superli.deliveries;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import com.superli.deliveries.domain.core.*;

/**
 * Unit tests for the Truck class.
 */
class TruckTest {

    private Truck testTruck;

    @BeforeEach
    void setUp() {
        testTruck = new Truck(
            "TR-123",
            "Volvo FH",
            8000.0f,
            20000.0f,
            LicenseType.C
        );
    }

    // Core Functionality Tests
    @Test
    void constructor_ValidData_CreatesTruck() {
        assertNotNull(testTruck);
        assertEquals("TR-123", testTruck.getPlateNum());
        assertEquals("Volvo FH", testTruck.getModel());
        assertEquals(8000.0f, testTruck.getNetWeight());
        assertEquals(20000.0f, testTruck.getMaxWeight());
        assertEquals(LicenseType.C, testTruck.getRequiredLicenseType());
        assertTrue(testTruck.isAvailable());
    }

    @Test
    void constructor_NullPlateNum_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck(null, "Volvo", 8000.0f, 20000.0f, LicenseType.C);
        });
    }

    @Test
    void constructor_BlankPlateNum_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck("   ", "Volvo", 8000.0f, 20000.0f, LicenseType.C);
        });
    }

    @Test
    void constructor_NullModel_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck("TR-123", null, 8000.0f, 20000.0f, LicenseType.C);
        });
    }

    @Test
    void constructor_BlankModel_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck("TR-123", "   ", 8000.0f, 20000.0f, LicenseType.C);
        });
    }

    // Weight Validation Tests
    @Test
    void constructor_NegativeNetWeight_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck("TR-123", "Volvo", -1000.0f, 20000.0f, LicenseType.C);
        });
    }

    @Test
    void constructor_NegativeMaxWeight_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck("TR-123", "Volvo", 8000.0f, -1000.0f, LicenseType.C);
        });
    }

    @Test
    void constructor_MaxWeightLessThanNetWeight_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck("TR-123", "Volvo", 10000.0f, 5000.0f, LicenseType.C);
        });
    }

    // Capacity Tests
    @Test
    void getCapacity_ReturnsCorrectValue() {
        assertEquals(12000.0f, testTruck.getMaxWeight() - testTruck.getNetWeight());
    }

    @Test
    void getCapacity_ZeroCapacity_ReturnsZero() {
        Truck truck = new Truck("TR-456", "Small Truck", 5000.0f, 5000.0f, LicenseType.B);
        assertEquals(0.0f, truck.getMaxWeight() - truck.getNetWeight());
    }

    // Availability Tests
    @Test
    void setAvailable_UpdatesAvailability() {
        testTruck.setAvailable(false);
        assertFalse(testTruck.isAvailable());
        testTruck.setAvailable(true);
        assertTrue(testTruck.isAvailable());
    }

    // License Type Tests
    @Test
    void constructor_NullLicenseType_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck("TR-123", "Volvo", 8000.0f, 20000.0f, null);
        });
    }

    // Equality Tests
    @Test
    void equals_SamePlateNum_ShouldBeEqual() {
        Truck truck1 = new Truck("TR-123", "Volvo", 8000.0f, 20000.0f, LicenseType.C);
        Truck truck2 = new Truck("TR-123", "Scania", 9000.0f, 25000.0f, LicenseType.B);
        assertEquals(truck1, truck2);
        assertEquals(truck1.hashCode(), truck2.hashCode());
    }

    @Test
    void equals_DifferentPlateNum_ShouldNotBeEqual() {
        Truck truck1 = new Truck("TR-123", "Volvo", 8000.0f, 20000.0f, LicenseType.C);
        Truck truck2 = new Truck("TR-456", "Volvo", 8000.0f, 20000.0f, LicenseType.C);
        assertNotEquals(truck1, truck2);
    }
}