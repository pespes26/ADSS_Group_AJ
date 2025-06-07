package com.superli.deliveries.dto;

public class ArchivedShiftDTO {
    private int id;
    private String date;
    private String type;
    private String dayOfWeek;
    private int managerId;

    public ArchivedShiftDTO() {}

    public ArchivedShiftDTO(int id, String date, String type, String dayOfWeek, int managerId) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.dayOfWeek = dayOfWeek;
        this.managerId = managerId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public int getManagerId() { return managerId; }
    public void setManagerId(int managerId) { this.managerId = managerId; }
}