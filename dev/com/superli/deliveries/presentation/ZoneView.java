package com.superli.deliveries.presentation;

/**
 * Represents a geographic zone, formatted for presentation.
 * Belongs to the Presentation Layer as a simple DTO.
 */
public class ZoneView {

    private final int zoneId;
    private final String name;

    /**
     * Constructs a ZoneView object.
     *
     * @param zoneId Unique identifier for the zone.
     * @param name   The name of the zone.
     */
    public ZoneView(int zoneId, String name) {
        this.zoneId = zoneId;
        this.name = name;
    }

    public int getZoneId() {
        return zoneId;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ZoneView{" +
                "zoneId=" + zoneId +
                ", name='" + name + '\'' +
                '}';
    }

    // equals() and hashCode() can be added if used in collections or comparisons
}
