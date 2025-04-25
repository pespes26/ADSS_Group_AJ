package com.superli.deliveries.domain;

/**
 * Represents a geographic zone used for delivery planning.
 * Matches the Zone class in the Domain Layer class diagram.
 */
public class Zone {

    /** The unique identifier of the zone */
    private final String zoneId;

    /** The name of the zone */
    private final String name;

    /**
     * Constructs a new Zone object.
     *
     * @param zoneId The unique identifier for the zone. Cannot be null or empty.
     * @param name   The name of the zone. Cannot be null or empty.
     * @throws IllegalArgumentException if zoneId or name is null or empty.
     */
    public Zone(String zoneId, String name) {
        if (zoneId == null || zoneId.trim().isEmpty()) {
            throw new IllegalArgumentException("Zone ID cannot be null or empty.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Zone name cannot be null or empty.");
        }

        this.zoneId = zoneId;
        this.name = name;
    }

    /**
     * Returns the unique ID of the zone.
     * @return The zone ID.
     */
    public String getZoneId() {
        return zoneId;
    }

    /**
     * Returns the name of the zone.
     * @return The zone name.
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "zoneId='" + zoneId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Zone)) return false;
        Zone zone = (Zone) o;
        return zoneId.equals(zone.zoneId);
    }

    @Override
    public int hashCode() {
        return zoneId.hashCode();
    }
}
