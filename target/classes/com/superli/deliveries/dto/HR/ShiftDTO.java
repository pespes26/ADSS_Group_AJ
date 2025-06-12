package com.superli.deliveries.dto.HR;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ShiftDTO {
    private int id;
    private Date shiftDate;
    private String shiftType;
    private String shiftDay;
    private List<Integer> requiredRoleIds;
    private Map<Integer, Integer> assignedEmployees; // employeeId → roleId

    public ShiftDTO() {}

    public ShiftDTO(int id, Date shiftDate, String shiftType, String shiftDay,
                    List<Integer> requiredRoleIds, Map<Integer, Integer> assignedEmployees) {
        this.id = id;
        this.shiftDate = shiftDate;
        this.shiftType = shiftType;
        this.shiftDay = shiftDay;
        this.requiredRoleIds = requiredRoleIds;
        this.assignedEmployees = assignedEmployees;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getShiftDate() { return shiftDate; }
    public void setShiftDate(Date shiftDate) { this.shiftDate = shiftDate; }

    public String getShiftType() { return shiftType; }
    public void setShiftType(String shiftType) { this.shiftType = shiftType; }

    public String getShiftDay() { return shiftDay; }
    public void setShiftDay(String shiftDay) { this.shiftDay = shiftDay; }

    public List<Integer> getRequiredRoleIds() { return requiredRoleIds; }
    public void setRequiredRoleIds(List<Integer> requiredRoleIds) { this.requiredRoleIds = requiredRoleIds; }

    public Map<Integer, Integer> getAssignedEmployees() { return assignedEmployees; }
    public void setAssignedEmployees(Map<Integer, Integer> assignedEmployees) { this.assignedEmployees = assignedEmployees; }

}
