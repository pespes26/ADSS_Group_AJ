package com.superli.deliveries.domain.core;
import java.util.Objects;

/**
 * Represents a physical site (e.g., store, supplier, warehouse)
 * used in the deliveries module. Matches the Site class in the Domain Layer diagram.
 */
public class Site {

    private final String siteId;
    private String address;
    private String phoneNumber;
    private String contactPersonName;
    private Zone zone; // The zone this site belongs to

    /**
     * Constructs a new Site object.
     *
     * @param siteId            Unique identifier. Cannot be null or empty.
     * @param address           Address of the site. Cannot be null or empty.
     * @param phoneNumber       Phone number (optional).
     * @param contactPersonName Contact person's name (optional).
     * @param zone              Zone this site belongs to. Cannot be null.
     */
    public Site(String siteId, String address, String phoneNumber, String contactPersonName, Zone zone) {
        if (siteId == null || siteId.trim().isEmpty()) {
            throw new IllegalArgumentException("Site ID cannot be null or empty.");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        if (zone == null) {
            throw new IllegalArgumentException("Zone cannot be null.");
        }

        this.siteId = siteId;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.contactPersonName = contactPersonName;
        this.zone = zone;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public Zone getZone() {
        return zone;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public void setZone(Zone zone) {
        if (zone == null) {
            throw new IllegalArgumentException("Zone cannot be null.");
        }
        this.zone = zone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== SITE DETAILS ===\n");
        sb.append("Site ID: ").append(siteId).append("\n");
        sb.append("Address: ").append(address).append("\n");

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            sb.append("Phone: ").append(phoneNumber).append("\n");
        }

        if (contactPersonName != null && !contactPersonName.isEmpty()) {
            sb.append("Contact Person: ").append(contactPersonName).append("\n");
        }

        sb.append("Zone: ").append(zone.getName()).append(" (").append(zone.getZoneId()).append(")\n");
        sb.append("====================");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Site)) return false;
        Site site = (Site) o;
        return siteId.equals(site.siteId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(siteId);
    }
}