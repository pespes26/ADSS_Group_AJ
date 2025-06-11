package com.superli.deliveries.dto;

public class ArchivedShiftDTO {
    private String id;
    private String date;
    private String type;
    private String dayOfWeek;
    private String managerId;

    public ArchivedShiftDTO() {}

    public ArchivedShiftDTO(String id, String date, String type, String dayOfWeek, String managerId) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.dayOfWeek = dayOfWeek;
        this.managerId = managerId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }
}