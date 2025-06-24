package com.superli.deliveries.dto.HR;

public class ShiftRoleDTO {
    private String dayOfWeek;
    private String shiftType;
    private int roleId;
    private int requiredCount;
    private int siteId;

    public ShiftRoleDTO() {}

    public ShiftRoleDTO(String dayOfWeek, String shiftType, int roleId, int requiredCount, int siteId) {
        this.dayOfWeek = dayOfWeek;
        this.shiftType = shiftType;
        this.roleId = roleId;
        this.requiredCount = requiredCount;
        this.siteId = siteId;
    }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getShiftType() { return shiftType; }
    public void setShiftType(String shiftType) { this.shiftType = shiftType; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public int getRequiredCount() { return requiredCount; }
    public void setRequiredCount(int requiredCount) { this.requiredCount = requiredCount; }

    public int getSiteId() { return siteId; }
    public void setSiteId(int siteId) { this.siteId = siteId; }
}
