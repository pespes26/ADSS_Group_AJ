package com.superli.deliveries.domain; // Ensure this matches your Truck class package

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Truck class.
 */
class TruckTest {

    // Constants for valid default values (makes tests cleaner)
    private static final String VALID_PLATE = "TRUCK-123";
    private static final String VALID_MODEL = "Volvo FH";
    private static final float VALID_NET_WEIGHT = 8000.0f; // e.g., 8 tons
    private static final float VALID_MAX_WEIGHT = 20000.0f; // e.g., 20 tons gross
    private static final String VALID_LICENSE_TYPE = "C+E";

    @Test
    void constructor_ValidData_CreatesTruck() {
        // Arrange & Act
        Truck truck = new Truck(VALID_PLATE, VALID_MODEL, VALID_NET_WEIGHT, VALID_MAX_WEIGHT, VALID_LICENSE_TYPE);

        // Assert
        assertNotNull(truck, "Truck object should not be null");
        assertEquals(VALID_PLATE, truck.getPlateNum());
        assertEquals(VALID_MODEL, truck.getModel());
        assertEquals(VALID_NET_WEIGHT, truck.getNetWeight());
        assertEquals(VALID_MAX_WEIGHT, truck.getMaxWeight());
        assertEquals(VALID_LICENSE_TYPE, truck.getRequiredLicenseType());
    }

    // --- Constructor Validation Tests ---

    @Test
    void constructor_NullLicensePlate_ThrowsIllegalArgumentException() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck(null, VALID_MODEL, VALID_NET_WEIGHT, VALID_MAX_WEIGHT, VALID_LICENSE_TYPE);
        }, "Constructor should throw IllegalArgumentException for null license plate");
    }

    @Test
    void constructor_BlankLicensePlate_ThrowsIllegalArgumentException() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck("   ", VALID_MODEL, VALID_NET_WEIGHT, VALID_MAX_WEIGHT, VALID_LICENSE_TYPE);
        }, "Constructor should throw IllegalArgumentException for blank license plate");
    }

    @Test
    void constructor_NullModel_ThrowsIllegalArgumentException() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck(VALID_PLATE, null, VALID_NET_WEIGHT, VALID_MAX_WEIGHT, VALID_LICENSE_TYPE);
        }, "Constructor should throw IllegalArgumentException for null model");
    }

    @Test
    void constructor_NetWeightZero_ThrowsIllegalArgumentException() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck(VALID_PLATE, VALID_MODEL, 0.0f, VALID_MAX_WEIGHT, VALID_LICENSE_TYPE);
        }, "Constructor should throw IllegalArgumentException for zero net weight");
    }

    @Test
    void constructor_NetWeightNegative_ThrowsIllegalArgumentException() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck(VALID_PLATE, VALID_MODEL, -100f, VALID_MAX_WEIGHT, VALID_LICENSE_TYPE);
        }, "Constructor should throw IllegalArgumentException for negative net weight");
    }

    @Test
    void constructor_MaxWeightLessThanNetWeight_ThrowsIllegalArgumentException() {
        // Arrange
        float netWeight = 1000f;
        float maxWeight = 999f; // Less than net weight

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck(VALID_PLATE, VALID_MODEL, netWeight, maxWeight, VALID_LICENSE_TYPE);
        }, "Constructor should throw IllegalArgumentException when max weight is less than net weight");
    }

    @Test
    void constructor_NullLicenseType_ThrowsIllegalArgumentException() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Truck(VALID_PLATE, VALID_MODEL, VALID_NET_WEIGHT, VALID_MAX_WEIGHT, null);
        }, "Constructor should throw IllegalArgumentException for null required license type");
    }

    // --- TODO: Add more constructor validation tests ---
    // * Blank Model
    // * Blank Required License Type
    // * Maybe Max Weight equals Net Weight (valid case)

    // --- Equals and HashCode Tests ---

    @Test
    void equals_SamePlate_ShouldBeEqual() {
        // Arrange
        Truck truck1 = new Truck(VALID_PLATE, "Model A", 8000f, 20000f, "C");
        Truck truck2 = new Truck(VALID_PLATE, "Model B", 9000f, 22000f, "C+E"); // Same plate

        // Act & Assert
        assertEquals(truck1, truck2, "Trucks with the same license plate should be equal");
        assertEquals(truck1.hashCode(), truck2.hashCode(), "HashCode should be equal for equal objects");
    }

    @Test
    void equals_DifferentPlate_ShouldNotBeEqual() {
        // Arrange
        Truck truck1 = new Truck("PLATE-1", "Model A", 8000f, 20000f, "C");
        Truck truck2 = new Truck("PLATE-2", "Model A", 8000f, 20000f, "C"); // Different plate

        // Act & Assert
        assertNotEquals(truck1, truck2, "Trucks with different license plates should not be equal");
    }

    // --- TODO: Add equals test for null and different type ---


    // --- No Setters to test as fields are final ---

}