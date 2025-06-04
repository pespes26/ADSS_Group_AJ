package test.com.superli.deliveries; // Ensure this matches your Site class package

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.superli.deliveries.domain.core.Zone;
import com.superli.deliveries.domain.core.Site;

/**
 * Unit tests for the Site class using realistic data examples.
 */
class SiteTest {

    private Zone validZone;

    @BeforeEach
    void setUp() {
        validZone = new Zone("DAROM", "Southern Region");
    }

    // --- Constructor Tests ---

    @Test
    void constructor_ValidData_CreatesSite() {
        Site site = new Site("BEER-SHEVA-MAIN", "Rager Blvd 1, Be'er Sheva", "08-6461111", "Yossi Cohen", validZone);
        assertNotNull(site);
        assertEquals("BEER-SHEVA-MAIN", site.getSiteId());
        assertEquals("Rager Blvd 1, Be'er Sheva", site.getAddress());
        assertEquals("08-6461111", site.getPhoneNumber());
        assertEquals("Yossi Cohen", site.getContactPersonName());
        assertEquals(validZone, site.getZone());
    }

    @Test
    void constructor_ValidDataWithNullOptionalFields_CreatesSite() {
        Site site = new Site("OMER-WAREHOUSE", "HaShalom 10, Omer", null, null, validZone);
        assertNotNull(site);
        assertEquals("OMER-WAREHOUSE", site.getSiteId());
        assertEquals("HaShalom 10, Omer", site.getAddress());
        assertNull(site.getPhoneNumber());
        assertNull(site.getContactPersonName());
        assertEquals(validZone, site.getZone());
    }

    @Test
    void constructor_NullSiteId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Site(null, "Rager Blvd 1", "08-6461111", "Yossi", validZone));
    }

    @Test
    void constructor_BlankSiteId_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Site("   ", "Rager Blvd 1", "08-6461111", "Yossi", validZone));
    }

    @Test
    void constructor_NullAddress_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Site("S-003", null, "08-6461111", "Yossi", validZone));
    }

    @Test
    void constructor_BlankAddress_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Site("S-004", "   ", "08-6461111", "Yossi", validZone));
    }

    @Test
    void constructor_NullZone_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Site("S-005", "Address", "08-6461111", "Yossi", null));
    }

    // --- Setter Tests ---

    @Test
    void setAddress_ValidAddress_UpdatesAddress() {
        Site site = new Site("S-100", "Old Address", "08-111", "Contact", validZone);
        site.setAddress("Ben Gurion Blvd 25");
        assertEquals("Ben Gurion Blvd 25", site.getAddress());
    }

    @Test
    void setAddress_NullAddress_ThrowsIllegalArgumentException() {
        Site site = new Site("S-101", "Old Address", "08-111", "Contact", validZone);
        assertThrows(IllegalArgumentException.class, () -> site.setAddress(null));
    }

    @Test
    void setAddress_BlankAddress_ThrowsIllegalArgumentException() {
        Site site = new Site("S-102", "Old Address", "08-111", "Contact", validZone);
        assertThrows(IllegalArgumentException.class, () -> site.setAddress("   "));
    }

    @Test
    void setPhoneNumber_ValidNumber_UpdatesNumber() {
        Site site = new Site("S-103", "Address", "08-111", "Contact", validZone);
        site.setPhoneNumber("052-9876543");
        assertEquals("052-9876543", site.getPhoneNumber());
    }

    @Test
    void setPhoneNumber_NullNumber_UpdatesToNull() {
        Site site = new Site("S-104", "Address", "08-111", "Contact", validZone);
        site.setPhoneNumber(null);
        assertNull(site.getPhoneNumber());
    }

    @Test
    void setContactPersonName_ValidName_UpdatesName() {
        Site site = new Site("S-105", "Address", "Phone", "Old Name", validZone);
        site.setContactPersonName("Gal Gadot");
        assertEquals("Gal Gadot", site.getContactPersonName());
    }

    @Test
    void setContactPersonName_NullName_UpdatesToNull() {
        Site site = new Site("S-106", "Address", "Phone", "Old Name", validZone);
        site.setContactPersonName(null);
        assertNull(site.getContactPersonName());
    }

    @Test
    void setZone_ValidZone_UpdatesZone() {
        Site site = new Site("S-107", "Address", "Phone", "Contact", validZone);
        Zone newZone = new Zone("MERKAZ", "Central Region");
        site.setZone(newZone);
        assertEquals(newZone, site.getZone());
    }

    @Test
    void setZone_NullZone_ThrowsIllegalArgumentException() {
        Site site = new Site("S-108", "Address", "Phone", "Contact", validZone);
        assertThrows(IllegalArgumentException.class, () -> site.setZone(null));
    }

    // --- Equals and HashCode Tests ---

    @Test
    void equals_SameId_ShouldBeEqual() {
        Site site1 = new Site("BEER-SHEVA-MAIN", "Address A", "111", "Contact A", validZone);
        Zone otherZone = new Zone("OTHER", "Other Region");
        Site site2 = new Site("BEER-SHEVA-MAIN", "Address B", "222", "Contact B", otherZone);
        assertEquals(site1, site2);
        assertEquals(site1.hashCode(), site2.hashCode());
    }

    @Test
    void equals_DifferentId_ShouldNotBeEqual() {
        Site site1 = new Site("ID1", "Addr", "111", "Contact", validZone);
        Site site2 = new Site("ID2", "Addr", "111", "Contact", validZone);
        assertNotEquals(site1, site2);
    }

    @Test
    void equals_NullOrDifferentType_ShouldNotBeEqual() {
        Site site = new Site("ID", "Addr", "111", "Contact", validZone);
        assertNotEquals(site, null);
        assertNotEquals(site, "String");
    }
}
