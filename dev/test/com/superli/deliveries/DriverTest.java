package test.com.superli.deliveries;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.superli.deliveries.domain.core.*;

import java.util.*;

class DriverTest {

    private Driver createTestDriver(String id, String name, LicenseType licenseType, boolean available) {
        return new Driver(
                id,
                name,
                "000-000-000",
                0.0,
                "Standard",
                new Date(),
                new ArrayList<>(),
                new ArrayList<>(),
                new Role("Driver"),
                licenseType
        );
    }

    @Test
    void constructor_ValidData_CreatesDriver() {
        Driver driver = createTestDriver("123456789", "Israel Israeli", LicenseType.C, true);
        assertNotNull(driver);
        assertEquals("123456789", driver.getDriverId());
        assertEquals("Israel Israeli", driver.getFullName());
        assertEquals(LicenseType.C, driver.getLicenseType());
        assertTrue(driver.isAvailable());
    }

    @Test
    void constructor_NullId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            createTestDriver(null, "Test Driver", LicenseType.C, true);
        });
    }

    @Test
    void constructor_BlankId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            createTestDriver("   ", "Test Driver", LicenseType.C, true);
        });
    }

    @Test
    void constructor_NullName_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            createTestDriver("987654321", null, LicenseType.C1, false);
        });
    }

    @Test
    void constructor_EmptyName_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            createTestDriver("987654321", "", LicenseType.C1, false);
        });
    }

    @Test
    void setAvailable_UpdatesAvailability() {
        Driver driver = createTestDriver("123", "Test Driver", LicenseType.C, false);
        driver.setAvailable(true);
        assertTrue(driver.isAvailable());
    }

    @Test
    void setLicenseType_ValidType_UpdatesLicenseType() {
        Driver driver = createTestDriver("D789", "Test", LicenseType.B, true);
        LicenseType newLicenseType = LicenseType.C1;
        driver.setLicenseType(newLicenseType);
        assertEquals(newLicenseType, driver.getLicenseType());
    }

    @Test
    void equals_SameId_ShouldBeEqual() {
        Driver driver1 = createTestDriver("ID_EQ", "Name A", LicenseType.C, true);
        Driver driver2 = createTestDriver("ID_EQ", "Name B", LicenseType.B, false);
        assertEquals(driver1, driver2);
        assertEquals(driver1.hashCode(), driver2.hashCode());
    }

    @Test
    void equals_DifferentId_ShouldNotBeEqual() {
        Driver driver1 = createTestDriver("ID1", "Name", LicenseType.C, true);
        Driver driver2 = createTestDriver("ID2", "Name", LicenseType.C, true);
        assertNotEquals(driver1, driver2);
    }
}