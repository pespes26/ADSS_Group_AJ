package com.superli.deliveries.dto;

public class AvailableShiftDTO {
    private int employeeId;
    private String dayOfWeek;
    private String shiftType;

    public AvailableShiftDTO() {}

    public AvailableShiftDTO(int employeeId, String dayOfWeek, String shiftType) {
        this.employeeId = employeeId;
        this.dayOfWeek = dayOfWeek;
        this.shiftType = shiftType;
    }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getShiftType() { return shiftType; }
    public void setShiftType(String shiftType) { this.shiftType = shiftType; }
}