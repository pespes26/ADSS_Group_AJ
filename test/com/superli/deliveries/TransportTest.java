package com.superli.deliveries;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.superli.deliveries.domain.core.Driver;
import com.superli.deliveries.domain.core.LicenseType;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.domain.core.Site;
import com.superli.deliveries.domain.core.Transport;
import com.superli.deliveries.domain.core.TransportStatus;
import com.superli.deliveries.domain.core.Truck;
import com.superli.deliveries.domain.core.Zone;
import com.superli.deliveries.domain.core.DestinationDoc;

/**
 * Unit tests for the Transport class.
 */
class TransportTest {

    private Truck testTruck;
    private Driver testDriver;
    private Site testSite;
    private LocalDateTime testDateTime;
    private Zone testZone;

    @BeforeEach
    void setUp() {
        testTruck = new Truck("ABC123", "Volvo", 5000f, 10000f, LicenseType.C);
        testDriver = new Driver("D123", "John Doe", "123-456-789", 0.0, 0, "Standard", 
            new java.util.Date(), new ArrayList<>(), new ArrayList<>(), 
            new Role("Driver"), LicenseType.C);
        testZone = new Zone("Z001", "Test Zone");
        testSite = new Site("S001", "123 Main St", "555-0123", "John Smith", testZone);
        testDateTime = LocalDateTime.now().plusDays(1);
    }

    // Normal Cases
    @Test
    void constructor_ValidData_CreatesTransport() {
        Transport transport = new Transport("T001", testTruck, testDriver, testSite, testDateTime);
        
        assertNotNull(transport);
        assertEquals("T001", transport.getTransportId());
        assertEquals(testTruck, transport.getTruck());
        assertEquals(testDriver, transport.getDriver());
        assertEquals(testSite, transport.getOriginSite());
        assertEquals(testDateTime, transport.getDepartureDateTime());
        assertEquals(TransportStatus.PLANNED, transport.getStatus());
        assertTrue(transport.getDestinationDocs().isEmpty());
        assertEquals(0f, transport.getDepartureWeight());
    }

    @Test
    void constructor_DefaultDateTime_CreatesTransportWithTomorrow() {
        Transport transport = new Transport("T001", testTruck, testDriver, testSite);
        
        assertNotNull(transport);
        assertTrue(transport.getDepartureDateTime().isAfter(LocalDateTime.now()));
        assertTrue(transport.getDepartureDateTime().isBefore(LocalDateTime.now().plusDays(2)));
    }

    @Test
    void addDestinationDoc_ValidDoc_AddsToDestinationList() {
        Transport transport = new Transport("T001", testTruck, testDriver, testSite);
        DestinationDoc doc = new DestinationDoc("DOC001", "T001", testSite, "PLANNED");
        
        transport.addDestinationDoc(doc);
        List<DestinationDoc> destinations = transport.getDestinationDocs();
        
        assertEquals(1, destinations.size());
        assertEquals(doc, destinations.get(0));
    }

    // Invalid Cases
    @Test
    void constructor_NullTruck_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transport("T001", null, testDriver, testSite, testDateTime);
        });
    }

    @Test
    void constructor_NullDriver_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transport("T001", testTruck, null, testSite, testDateTime);
        });
    }

    @Test
    void constructor_NullSite_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transport("T001", testTruck, testDriver, null, testDateTime);
        });
    }

    @Test
    void constructor_NullDateTime_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transport("T001", testTruck, testDriver, testSite, null);
        });
    }

    @Test
    void setDepartureWeight_NegativeWeight_ThrowsIllegalArgumentException() {
        Transport transport = new Transport("T001", testTruck, testDriver, testSite);
        
        assertThrows(IllegalArgumentException.class, () -> {
            transport.setDepartureWeight(-100f);
        });
    }

    @Test
    void setStatus_NullStatus_ThrowsIllegalArgumentException() {
        Transport transport = new Transport("T001", testTruck, testDriver, testSite);
        
        assertThrows(IllegalArgumentException.class, () -> {
            transport.setStatus(null);
        });
    }

    @Test
    void addDestinationDoc_NullDoc_ThrowsIllegalArgumentException() {
        Transport transport = new Transport("T001", testTruck, testDriver, testSite);
        
        assertThrows(IllegalArgumentException.class, () -> {
            transport.addDestinationDoc(null);
        });
    }

    // Edge Cases
    @Test
    void setDepartureWeight_ZeroWeight_AcceptsZero() {
        Transport transport = new Transport("T001", testTruck, testDriver, testSite);
        transport.setDepartureWeight(0f);
        assertEquals(0f, transport.getDepartureWeight());
    }

    @Test
    void removeDestinationDoc_NullDoc_DoesNotThrowException() {
        Transport transport = new Transport("T001", testTruck, testDriver, testSite);
        assertDoesNotThrow(() -> {
            transport.removeDestinationDoc(null);
        });
    }

    @Test
    void removeDestinationDoc_NonExistentDoc_DoesNotThrowException() {
        Transport transport = new Transport("T001", testTruck, testDriver, testSite);
        DestinationDoc doc = new DestinationDoc("DOC001", "T001", testSite, "PLANNED");
        
        assertDoesNotThrow(() -> {
            transport.removeDestinationDoc(doc);
        });
    }

    @Test
    void equals_SameId_ShouldBeEqual() {
        Transport transport1 = new Transport("T001", testTruck, testDriver, testSite);
        Transport transport2 = new Transport("T001", testTruck, testDriver, testSite);
        
        assertEquals(transport1, transport2);
        assertEquals(transport1.hashCode(), transport2.hashCode());
    }

    @Test
    void equals_DifferentId_ShouldNotBeEqual() {
        Transport transport1 = new Transport("T001", testTruck, testDriver, testSite);
        Transport transport2 = new Transport("T002", testTruck, testDriver, testSite);
        
        assertNotEquals(transport1, transport2);
    }
}