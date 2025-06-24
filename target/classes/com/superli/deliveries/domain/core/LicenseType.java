package com.superli.deliveries.domain.core;

/**
 * Represents the type of driver's license.
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
}