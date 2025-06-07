package com.superli.deliveries.domain.core;

/**
 * Represents the different types of shifts in the system.
 */
public enum ShiftType {
    MORNING("Morning"),
    EVENING("Evening");

    private final String displayName;

    ShiftType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
} 