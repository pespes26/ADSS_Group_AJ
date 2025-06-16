package com.superli.deliveries;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import com.superli.deliveries.domain.core.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class HRLogisticsIntegrationTest {

    private HRManager hrManager;
    private Driver testDriver;
    private Truck testTruck;
    private Site testSite;
    private Zone testZone;
    private Transport testTransport;

    @BeforeEach
    void setUp() {
        hrManager = new HRManager();
        testZone = new Zone("Z001", "Test Zone");
        testSite = new Site("S001", "123 Main St", "555-0123", "John Smith", testZone);
        testTruck = new Truck("ABC123", "Volvo", 5000f, 10000f, LicenseType.C);
        testDriver = new Driver("D123", "John Doe", "123-456-789", 0.0, 0, "Standard", 
            new java.util.Date(), new ArrayList<>(), new ArrayList<>(), 
            new Role("Driver"), LicenseType.C);
        testTransport = new Transport("T001", testTruck, testDriver, testSite);
    }

    @Test
    void driverAssignedToTransport_BecomesUnavailable() {
        // Add driver to HR system
        hrManager.addEmployee(testDriver);
        assertTrue(testDriver.isAvailable());

        // Create and assign transport
        testTransport.setStatus(TransportStatus.DISPATCHED);
        assertFalse(testDriver.isAvailable());
    }

    @Test
    void transportCompleted_DriverBecomesAvailable() {
        // Add driver to HR system
        hrManager.addEmployee(testDriver);
        testTransport.setStatus(TransportStatus.DISPATCHED);
        assertFalse(testDriver.isAvailable());

        // Complete transport
        testTransport.setStatus(TransportStatus.COMPLETED);
        assertTrue(testDriver.isAvailable());
    }

    @Test
    void transportCancelled_DriverBecomesAvailable() {
        // Add driver to HR system
        hrManager.addEmployee(testDriver);
        testTransport.setStatus(TransportStatus.DISPATCHED);
        assertFalse(testDriver.isAvailable());

        // Cancel transport
        testTransport.setStatus(TransportStatus.CANCELLED);
        assertTrue(testDriver.isAvailable());
    }

    @Test
    void driverUnavailable_CannotBeAssignedToNewTransport() {
        // Add driver to HR system
        hrManager.addEmployee(testDriver);
        testTransport.setStatus(TransportStatus.DISPATCHED);
        assertFalse(testDriver.isAvailable());

        // Try to create new transport with same driver
        Transport newTransport = new Transport("T002", testTruck, testDriver, testSite);
        assertThrows(IllegalStateException.class, () -> {
            newTransport.setStatus(TransportStatus.DISPATCHED);
        });
    }

    @Test
    void driverWithIncorrectLicense_CannotBeAssignedToTruck() {
        // Create driver with B license
        Driver driverB = new Driver("D456", "Jane Doe", "987-654-321", 0.0, 0, "Standard", 
            new java.util.Date(), new ArrayList<>(), new ArrayList<>(), 
            new Role("Driver"), LicenseType.B);
        hrManager.addEmployee(driverB);

        // Try to create transport with C license truck
        assertThrows(IllegalArgumentException.class, () -> {
            new Transport("T003", testTruck, driverB, testSite);
        });
    }
} 