package DomainLayer;

public class AvailableShifts {
    private DayOfWeek day;
    private ShiftType shift;

    public AvailableShifts(DayOfWeek day, ShiftType shift) {
        this.day = day;
        this.shift = shift;
    }
}
