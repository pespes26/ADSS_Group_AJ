package com.superli.deliveries.domain.core;

import java.time.DayOfWeek;
import java.util.Objects;

public class AvailableShifts {
    private DayOfWeek day;
    private ShiftType shift;

    public AvailableShifts(DayOfWeek day, ShiftType shift) {
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
}