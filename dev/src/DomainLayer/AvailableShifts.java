package DomainLayer;

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
}
