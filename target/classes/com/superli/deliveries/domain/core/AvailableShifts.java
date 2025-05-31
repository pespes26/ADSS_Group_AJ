package domain.core;

import java.time.DayOfWeek;
import java.util.Objects;

/**
 * Represents a shift availability constraint for an employee.
 */
public class AvailableShifts {
    private final DayOfWeek day;
    private final ShiftType shift;

    /**
     * Creates a new availability constraint.
     *
     * @param day The day of the week
     * @param shift The type of shift
     * @throws IllegalArgumentException if either parameter is null
     */
    public AvailableShifts(DayOfWeek day, ShiftType shift) {
        if (day == null) {
            throw new IllegalArgumentException("Day cannot be null");
        }
        if (shift == null) {
            throw new IllegalArgumentException("Shift type cannot be null");
        }

        this.day = day;
        this.shift = shift;
    }

    /**
     * Gets the day of the week for this availability.
     *
     * @return The day of the week
     */
    public DayOfWeek getDay() {
        return day;
    }

    /**
     * Gets the shift type for this availability.
     *
     * @return The shift type
     */
    public ShiftType getShift() {
        return shift;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AvailableShifts)) return false;
        AvailableShifts that = (AvailableShifts) o;
        return day == that.day && shift == that.shift;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, shift);
    }

    @Override
    public String toString() {
        return String.format("AvailableShifts{day=%s, shift=%s}", day, shift);
    }
} 