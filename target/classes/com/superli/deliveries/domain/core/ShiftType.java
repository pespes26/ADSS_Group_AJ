package domain.core;

/**
 * Represents the different types of shifts in the system.
 */
public enum ShiftType {
    MORNING("Morning Shift"),
    AFTERNOON("Afternoon Shift"),
    NIGHT("Night Shift");

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