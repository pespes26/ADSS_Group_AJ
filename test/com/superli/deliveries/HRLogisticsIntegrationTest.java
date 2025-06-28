package com.superli.deliveries;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import com.superli.deliveries.domain.core.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class HRLogisticsIntegrationTest {

    private Driver testDriver;
    private Truck testTruck;
    private Site testSite;

    @BeforeEach
    void setUp() {
        Zone testZone = new Zone("Z001", "Test Zone");
        testSite = new Site("S001", "123 Main St", "555-0123", "John Smith", testZone);
        testTruck = new Truck("ABC123", "Volvo", 5000f, 10000f, LicenseType.C);

        testDriver = new Driver("D123", "John Doe", "123-456-789", 0.0, 0, "Standard",
                new Date(), new ArrayList<>(), new ArrayList<>(),
                new Role("Driver"), LicenseType.C);
    }

    @Test
    void givenNewDriver_whenTransportCreated_thenDriverRemainsAvailable() {
        // GIVEN
        assertTrue(testDriver.isAvailable());

        // WHEN
        Transport t = new Transport("T001", testTruck, testDriver, testSite);

        // THEN
        assertTrue(testDriver.isAvailable());
    }

    @Test
    void givenTransport_whenStatusChanges_thenDriverStillAvailable() {
        // GIVEN
        Transport t = new Transport("T002", testTruck, testDriver, testSite);

        // WHEN
        t.setStatus(TransportStatus.DISPATCHED);
        t.setStatus(TransportStatus.COMPLETED);

        // THEN
        assertTrue(testDriver.isAvailable());
    }

    @Test
    void givenDriverWithWrongLicense_whenTransportCreated_thenNoExceptionThrown() {
        // GIVEN
        Driver driverB = new Driver("D456", "Jane Doe", "987-654-321", 0.0, 0, "Standard",
                new Date(), new ArrayList<>(), new ArrayList<>(),
                new Role("Driver"), LicenseType.B);

        // WHEN
        Transport t = new Transport("T003", testTruck, driverB, testSite);

        // THEN
        assertEquals(driverB, t.getDriver());
    }

    @Test
    void givenTransportWithDriver_whenCreatingAnotherWithSameDriver_thenBothCreated() {
        // GIVEN
        Transport t1 = new Transport("T004", testTruck, testDriver, testSite);
        t1.setStatus(TransportStatus.DISPATCHED);

        // WHEN
        Transport t2 = new Transport("T005", testTruck, testDriver, testSite);

        // THEN
        assertNotNull(t2);
    }

    @Test
    void givenNewTruck_whenCreated_thenIsAvailableByDefault() {
        // GIVEN-WHEN-THEN
        assertTrue(testTruck.isAvailable());
    }

    @Test
    void givenTransport_whenQueried_thenCorrectTruckAndDriverReturned() {
        // GIVEN
        Transport t = new Transport("T006", testTruck, testDriver, testSite);

        // WHEN & THEN
        assertEquals(testTruck, t.getTruck());
        assertEquals(testDriver, t.getDriver());
    }

    @Test
    void givenTransport_whenWeightSet_thenWeightIsStoredCorrectly() {
        // GIVEN
        Transport t = new Transport("T007", testTruck, testDriver, testSite);

        // WHEN
        t.setDepartureWeight(3000.5f);

        // THEN
        assertEquals(3000.5f, t.getDepartureWeight());
    }

    @Test
    void givenTransport_whenStatusSet_thenStatusIsStoredCorrectly() {
        // GIVEN
        Transport t = new Transport("T008", testTruck, testDriver, testSite);

        // WHEN
        t.setStatus(TransportStatus.DISPATCHED);

        // THEN
        assertEquals(TransportStatus.DISPATCHED, t.getStatus());
    }

    @Test
    void givenTruck_whenToStringCalled_thenPlateNumberIsIncluded() {
        // GIVEN-WHEN
        String str = testTruck.toString();

        // THEN
        assertTrue(str.contains("ABC123"));
    }

    @Test
    void givenDriver_whenToStringCalled_thenIdAndNameAreIncluded() {
        // GIVEN-WHEN
        String str = testDriver.toString();

        // THEN
        assertTrue(str.contains("D123"));
        assertTrue(str.contains("John Doe"));
    }
}
