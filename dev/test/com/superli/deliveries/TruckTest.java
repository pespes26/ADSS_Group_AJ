package test.com.superli.deliveries;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.superli.deliveries.domain.core.*;

/**
 * Unit tests for the Truck class.
 */
class TruckTest {

    @Test
    void constructor_ValidData_CreatesTruck() {
        // Arrange & Act
        Truck truck = new Truck(
                "TR-123",
                "Volvo FH",
                8000.0f,
                20000.0f,
                LicenseType.C
        );

        // Assert core immutable attributes
        assertEquals("TR-123", truck.getPlateNum());
        assertEquals("Volvo FH", truck.getModel());
        assertEquals(8000.0f, truck.getNetWeight());
        assertEquals(20000.0f, truck.getMaxWeight());
        assertEquals(LicenseType.C, truck.getRequiredLicenseType());

        // Assert default availability
        assertTrue(truck.isAvailable(), "New truck should be available by default");
    }

    @Test
    void constructor_SpecificAvailability_CreatesTruck() {
        Truck truck = new Truck(
                "TR-456",
                "Scania",
                9000.0f,
                25000.0f,
                LicenseType.C,
                false
        );

        assertFalse(truck.isAvailable());
        assertEquals(LicenseType.C, truck.getRequiredLicenseType());
    }

    @Test
    void setAvailability_ChangesAvailabilityStatus() {
        // Arrange
        Truck truck = new Truck(
                "TR-789",
                "Mercedes",
                7000.0f,
                18000.0f,
                LicenseType.B
        );

        // Initial state
        assertTrue(truck.isAvailable());

        // Change availability multiple times
        truck.setAvailable(false);
        assertFalse(truck.isAvailable());

        truck.setAvailable(true);
        assertTrue(truck.isAvailable());
    }

    @Test
    void multipleAvailabilityChanges_DoNotAffectOtherAttributes() {
        Truck truck = new Truck(
                "TR-MULTI",
                "Iveco",
                8500.0f,
                22000.0f,
                LicenseType.C1
        );

        // Store original core attributes
        String originalPlateNum = truck.getPlateNum();
        String originalModel = truck.getModel();
        float originalNetWeight = truck.getNetWeight();
        float originalMaxWeight = truck.getMaxWeight();
        LicenseType originalLicenseType = truck.getRequiredLicenseType();

        // Change availability multiple times
        truck.setAvailable(false);
        truck.setAvailable(true);
        truck.setAvailable(false);

        // Verify core attributes remain unchanged
        assertEquals(originalPlateNum, truck.getPlateNum());
        assertEquals(originalModel, truck.getModel());
        assertEquals(originalNetWeight, truck.getNetWeight());
        assertEquals(originalMaxWeight, truck.getMaxWeight());
        assertEquals(originalLicenseType, truck.getRequiredLicenseType());
    }

    // Existing validation tests remain the same...
}