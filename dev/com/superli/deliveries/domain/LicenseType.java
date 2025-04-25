package com.superli.deliveries.domain;

/**
 * Represents the predefined types of driver's licenses available in the system.
 */
public enum LicenseType {
    B("B"),
    C("C"),
    C1("C1"),
    C2("C2"),
    E("E");

    private final String value;

    LicenseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Find a license type by its string value.
     *
     * @param value The string value to search for
     * @return The corresponding LicenseType or null if not found
     */
    public static LicenseType fromString(String value) {
        for (LicenseType type : LicenseType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}