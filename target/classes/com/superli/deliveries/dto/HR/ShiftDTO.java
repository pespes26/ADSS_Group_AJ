package com.superli.deliveries.dto.HR;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ShiftDTO {
    private int siteId;
    private Date shiftDate;
    private String shiftType;
    private String shiftDay;
    private List<Integer> requiredRoleIds;
    private Map<Integer, Integer> assignedEmployees; // employeeId → roleId

    public ShiftDTO() {}

    public ShiftDTO(int siteId, Date shiftDate, String shiftType, String shiftDay,
                    List<Integer> requiredRoleIds, Map<Integer, Integer> assignedEmployees) {
        this.siteId = siteId;
        this.shiftDate = shiftDate;
        this.shiftType = shiftType;
        this.shiftDay = shiftDay;
        this.requiredRoleIds = requiredRoleIds;
        this.assignedEmployees = assignedEmployees;
    }

    public int getSiteId() { return siteId; }
    public void setSiteId(int siteId) { this.siteId = siteId; }


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
