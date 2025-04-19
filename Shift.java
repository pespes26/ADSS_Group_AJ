package DomainLayer;

import java.util.Date;
import java.util.List;
import java.util.Map;
public class Shift {
    private Date shiftDate;
    private ShiftType shiftType;
    private List<Role> shiftRoles;
    private Map<Employee, Role> shiftEmployees;
    private Employee shiftManager;

    public Shift(Date shiftDate, ShiftType shiftType, List<Role> shiftRoles, Map<Employee, Role> shiftEmployees, Employee shiftManager) {
        this.shiftDate = shiftDate;
        this.shiftType = shiftType;
        this.shiftRoles = shiftRoles;
        this.shiftEmployees = shiftEmployees;
        this.shiftManager = shiftManager;
    }
}
