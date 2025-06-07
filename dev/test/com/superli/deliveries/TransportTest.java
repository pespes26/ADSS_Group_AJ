package test.com.superli.deliveries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

import com.superli.deliveries.domain.core.*;

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
        driver = new Driver("DR-001", "Dan", "000-000-000", 0.0,
                "Standard Terms", new Date(), new ArrayList<>(), new ArrayList<>(),
                new Role("Driver"), LicenseType.C);
    }

    // --- Constructor Tests ---

    @Test
    void constructor_ValidData_CreatesTransport() {
        Transport transport = new Transport(1, LocalDateTime.now(), truck, driver, originSite, TransportStatus.PLANNED);
        assertNotNull(transport, "Transport object should not be null");
        assertEquals(1, transport.getTransportId());
        assertEquals(TransportStatus.PLANNED, transport.getStatus());
        assertEquals(originSite, transport.getOriginSite());
    }

    @Test
    void constructor_NullTruck_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transport(1, LocalDateTime.now(), null, driver, originSite, TransportStatus.PLANNED);
        });
    }

    @Test
    void constructor_NullDriver_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transport(1, LocalDateTime.now(), truck, null, originSite, TransportStatus.PLANNED);
        });
    }

    @Test
    void constructor_NullOriginSite_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transport(1, LocalDateTime.now(), truck, driver, null, TransportStatus.PLANNED);
        });
    }

    @Test
    void constructor_NullStatus_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Transport(1, LocalDateTime.now(), truck, driver, originSite, null);
        });
    }

    // --- Setter Tests ---

    @Test
    void setDepartureWeight_ValidWeight_UpdatesWeight() {
        Transport transport = new Transport(1, LocalDateTime.now(), truck, driver, originSite, TransportStatus.PLANNED);
        transport.setDepartureWeight(15000f);
        assertEquals(15000f, transport.getDepartureWeight());
    }

    @Test
    void setDepartureWeight_NegativeWeight_ThrowsIllegalArgumentException() {
        Transport transport = new Transport(1, LocalDateTime.now(), truck, driver, originSite, TransportStatus.PLANNED);
        assertThrows(IllegalArgumentException.class, () -> transport.setDepartureWeight(-1f));
    }

    @Test
    void setStatus_ValidStatus_UpdatesStatus() {
        Transport transport = new Transport(1, LocalDateTime.now(), truck, driver, originSite, TransportStatus.PLANNED);
        transport.setStatus(TransportStatus.COMPLETED);
        assertEquals(TransportStatus.COMPLETED, transport.getStatus());
    }

    // --- Equals and HashCode Tests ---

    @Test
    void equals_SameId_ShouldBeEqual() {
        Transport transport1 = new Transport(1, LocalDateTime.now(), truck, driver, originSite, TransportStatus.PLANNED);
        Transport transport2 = new Transport(1, LocalDateTime.now().plusHours(1), truck, driver, originSite, TransportStatus.COMPLETED);
        assertEquals(transport1, transport2);
    }

    @Test
    void equals_DifferentId_ShouldNotBeEqual() {
        Transport transport1 = new Transport(1, LocalDateTime.now(), truck, driver, originSite, TransportStatus.PLANNED);
        Transport transport2 = new Transport(2, LocalDateTime.now(), truck, driver, originSite, TransportStatus.PLANNED);
        assertNotEquals(transport1, transport2);
    }
}