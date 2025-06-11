package com.superli.deliveries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.superli.deliveries.domain.core.Zone;

/**
 * Unit tests for the Zone class.
 */
class ZoneTest {

    @Test
    void constructor_ValidData_CreatesZone() {
        // Arrange
        String zoneId = "ZONE-001";
        String name = "Central Zone";

        // Act
        Zone zone = new Zone(zoneId, name);

        // Assert
        assertNotNull(zone, "Zone object should not be null");
        assertEquals(zoneId, zone.getZoneId(), "Zone ID should match");
        assertEquals(name, zone.getName(), "Zone name should match");
    }

    // --- Constructor Validation Tests ---

    @Test
    void constructor_NullZoneId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Zone(null, "Test Zone");
        }, "Constructor should throw for null zoneId");
    }

    @Test
    void constructor_BlankZoneId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Zone("   ", "Test Zone");
        }, "Constructor should throw for blank zoneId");
    }

    @Test
    void constructor_NullName_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Zone("ZONE-ID", null);
        }, "Constructor should throw for null name");
    }

    @Test
    void constructor_BlankName_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Zone("ZONE-ID", "   ");
        }, "Constructor should throw for blank name");
    }

    // --- Equals and HashCode Tests ---

    @Test
    void equals_SameId_ShouldBeEqual() {
        // Arrange
        String zoneId = "ZONE-EQ";
        Zone zone1 = new Zone(zoneId, "Zone Name One");
        Zone zone2 = new Zone(zoneId, "Zone Name Two"); // Same ID, different name

        // Act & Assert
        assertEquals(zone1, zone2, "Zones with the same ID should be equal");
        assertEquals(zone1.hashCode(), zone2.hashCode(), "HashCode should be equal for equal objects");
    }

    @Test
    void equals_DifferentId_ShouldNotBeEqual() {
        // Arrange
        Zone zone1 = new Zone("ZONE-1", "Name");
        Zone zone2 = new Zone("ZONE-2", "Name"); // Different ID

        // Act & Assert
        assertNotEquals(zone1, zone2, "Zones with different IDs should not be equal");
    }

    @Test
    void equals_NullOrDifferentType_ShouldNotBeEqual() {
        // Arrange
        Zone zone = new Zone("ZONE-X", "Name");

        // Act & Assert
        assertNotEquals(zone, null, "Zone should not be equal to null");
        assertNotEquals(zone, "a string", "Zone should not be equal to a different type object");
    }

    // --- toString Test ---

    @Test
    void toString_ReturnsCorrectFormat() {
        // Arrange
        Zone zone = new Zone("ZONE-001", "Central Zone");

        // Act
        String result = zone.toString();

        // Assert
        assertEquals("Zone{zoneId='ZONE-001', name='Central Zone'}", result, "toString should return the correct format");
    }
}