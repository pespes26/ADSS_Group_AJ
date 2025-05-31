package domain.core.employee;

import java.time.DayOfWeek;

/**
 * Represents a shift that an employee is available to work.
 */
public class AvailableShifts {
    private final DayOfWeek day;
    private final ShiftType shift;

    /**
     * Constructs a new AvailableShifts.
     *
     * @param day Day of the week.
     * @param shift Type of shift.
     */
    public AvailableShifts(DayOfWeek day, ShiftType shift) {
        if (day == null) {
            throw new IllegalArgumentException("Day cannot be null.");
        }
        if (shift == null) {
            throw new IllegalArgumentException("Shift cannot be null.");
        }
        this.day = day;
        this.shift = shift;
    }

    // Getter method - returns the day
    public DayOfWeek getDay() {
        return day;
    }

    // Getter method - returns the shift
    public ShiftType getShift() {
        return shift;
    }

    @Override
    public String toString() {
        return "AvailableShifts{" +
                "day=" + day +
                ", shift=" + shift +
                '}';
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
        return 31 * day.hashCode() + shift.hashCode();
    }
}
