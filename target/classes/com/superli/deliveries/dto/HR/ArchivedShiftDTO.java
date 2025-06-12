package com.superli.deliveries.dto.HR;

import java.time.DayOfWeek;
import java.util.Date;
import com.superli.deliveries.domain.core.ShiftType;

public class ArchivedShiftDTO {
    private String employeeId;
    private DayOfWeek dayOfWeek;
    private ShiftType shiftType;
    private Date date;
    private int roleId;

    public ArchivedShiftDTO() {}

    public ArchivedShiftDTO(String employeeId, DayOfWeek dayOfWeek, ShiftType shiftType, Date date, int roleId) {
        this.employeeId = employeeId;
        this.dayOfWeek = dayOfWeek;
        this.shiftType = shiftType;
        this.date = date;
        this.roleId = roleId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
