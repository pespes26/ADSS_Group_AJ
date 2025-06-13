package com.superli.deliveries;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

/**
 * Unit tests for the Transport class.
 */
class TransportTest {

    private Site originSite;
    private Truck truck;
    private Driver driver;

    @BeforeEach
    void setUp() {
        originSite = new Site("Site1", "Origin St 1", "123", "Contact", new Zone("Zone1", "Central"));
        truck = new Truck("TR-001", "Volvo", 8000f, 20000f, LicenseType.C);
        driver = new Driver("DR-001", "Dan", "000-000-000", 0.0,-1,
                "Standard Terms", new Date(), new ArrayList<>(), new ArrayList<>(),
                new Role("Driver"), LicenseType.C);
    }

    // --- Constructor Tests ---

    @Test
    void constructor_ValidData_CreatesTransport() {
        Transport transport = new Transport("1", truck, driver, originSite, LocalDateTime.now());
        assertNotNull(transport, "Transport object should not be null");
        assertEquals("1", transport.getTransportId());
        assertEquals(TransportStatus.PLANNED, transport.getStatus());
        assertEquals(originSite, transport.getOriginSite());
    }

    @Test
    void constructor_NullTruck_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transport("1", null, driver, originSite, LocalDateTime.now());
        });
    }

    @Test
    void constructor_NullDriver_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transport("1", truck, null, originSite, LocalDateTime.now());
        });
    }

    @Test
    void constructor_NullOriginSite_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transport("1", truck, driver, null, LocalDateTime.now());
        });
    }

    @Test
    void constructor_NullStatus_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transport("1", truck, driver, originSite, null);
        });
    }

    // --- Setter Tests ---

    @Test
    void setDepartureWeight_ValidWeight_UpdatesWeight() {
        Transport transport = new Transport("1", truck, driver, originSite, LocalDateTime.now());
        transport.setDepartureWeight(15000f);
        assertEquals(15000f, transport.getDepartureWeight());
    }

    @Test
    void setDepartureWeight_NegativeWeight_ThrowsIllegalArgumentException() {
        Transport transport = new Transport("1", truck, driver, originSite, LocalDateTime.now());
        assertThrows(IllegalArgumentException.class, () -> transport.setDepartureWeight(-1f));
    }

    @Test
    void setStatus_ValidStatus_UpdatesStatus() {
        Transport transport = new Transport("1", truck, driver, originSite, LocalDateTime.now());
        transport.setStatus(TransportStatus.COMPLETED);
        assertEquals(TransportStatus.COMPLETED, transport.getStatus());
    }

    // --- Equals and HashCode Tests ---

    @Test
    void equals_SameId_ShouldBeEqual() {
        Transport transport1 = new Transport("1", truck, driver, originSite, LocalDateTime.now());
        Transport transport2 = new Transport("1", truck, driver, originSite, LocalDateTime.now().plusHours(1));
        assertEquals(transport1, transport2);
    }

    @Test
    void equals_DifferentId_ShouldNotBeEqual() {
        Transport transport1 = new Transport("1", truck, driver, originSite, LocalDateTime.now());
        Transport transport2 = new Transport("2", truck, driver, originSite, LocalDateTime.now());
        assertNotEquals(transport1, transport2);
    }
}