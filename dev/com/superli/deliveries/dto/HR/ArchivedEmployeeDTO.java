package com.superli.deliveries.dto.HR;

public class ArchivedEmployeeDTO {
    private int id;
    private String fullName;
    private String archivedDate;

    public ArchivedEmployeeDTO() {}

    public ArchivedEmployeeDTO(int id, String fullName, String archivedDate) {
        this.id = id;
        this.fullName = fullName;
        this.archivedDate = archivedDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getArchivedDate() { return archivedDate; }
    public void setArchivedDate(String archivedDate) { this.archivedDate = archivedDate; }
}