package test.com.superli.deliveries;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.superli.deliveries.domain.core.LicenseType;
import com.superli.deliveries.domain.core.Driver;

/**
 * Unit tests for the Driver class.
 */
class DriverTest {

    @Test
    void constructor_ValidData_CreatesDriver() {
        // Arrange
        String id = "123456789";
        String name = "Israel Israeli";
        LicenseType license = LicenseType.C;
        boolean available = true;

        // Act
        Driver driver = new Driver(id, name, license, available);

        // Assert
        assertNotNull(driver, "Driver object should not be null");
        assertEquals(id, driver.getDriverId(), "Driver ID should match");
        assertEquals(name, driver.getName(), "Driver name should match");
        assertEquals(license, driver.getLicenseType(), "License type should match");
        assertTrue(driver.isAvailable(), "Driver availability should match");
    }

    @Test
    void constructor_NullId_ThrowsIllegalArgumentException() {
        // Arrange
        String name = "Test Driver";
        LicenseType license = LicenseType.C;
        boolean available = true;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Driver(null, name, license, available);
        }, "Constructor should throw IllegalArgumentException for null ID");
    }

    @Test
    void constructor_BlankId_ThrowsIllegalArgumentException() {
        // Arrange
        String name = "Test Driver";
        LicenseType license = LicenseType.C;
        boolean available = true;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Driver("   ", name, license, available);
        }, "Constructor should throw IllegalArgumentException for blank ID");
    }

    @Test
    void constructor_NullName_ThrowsIllegalArgumentException() {
        // Arrange
        String id = "987654321";
        LicenseType license = LicenseType.C1;
        boolean available = false;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Driver(id, null, license, available);
        }, "Constructor should throw IllegalArgumentException for null name");
    }

    @Test
    void constructor_EmptyName_ThrowsIllegalArgumentException() {
        // Arrange
        String id = "987654321";
        String name = ""; // Empty name
        LicenseType license = LicenseType.C1;
        boolean available = false;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            new Driver(id, name, license, available);
        }, "Constructor should throw IllegalArgumentException for empty name");
    }

    @Test
    void setAvailable_UpdatesAvailability() {
        // Arrange
        Driver driver = new Driver("123", "Test Driver", LicenseType.C, false);

        // Act
        driver.setAvailable(true);

        // Assert
        assertTrue(driver.isAvailable(), "Driver availability should be updated to true");
    }

    @Test
    void setLicenseType_ValidType_UpdatesLicenseType() {
        // Arrange
        Driver driver = new Driver("D789", "Test", LicenseType.B, true);
        LicenseType newLicenseType = LicenseType.C1;

        // Act
        driver.setLicenseType(newLicenseType);

        // Assert
        assertEquals(newLicenseType, driver.getLicenseType());
    }

    @Test
    void equals_SameId_ShouldBeEqual() {
        // Arrange
        Driver driver1 = new Driver("ID_EQ", "Name A", LicenseType.C, true);
        Driver driver2 = new Driver("ID_EQ", "Name B", LicenseType.B, false);

        // Act & Assert
        assertEquals(driver1, driver2, "Drivers with same ID should be equal");
        assertEquals(driver1.hashCode(), driver2.hashCode(), "HashCode should be equal for equal objects");
    }

    @Test
    void equals_DifferentId_ShouldNotBeEqual() {
        // Arrange
        Driver driver1 = new Driver("ID1", "Name", LicenseType.C, true);
        Driver driver2 = new Driver("ID2", "Name", LicenseType.C, true);

        // Act & Assert
        assertNotEquals(driver1, driver2, "Drivers with different IDs should not be equal");
    }
}