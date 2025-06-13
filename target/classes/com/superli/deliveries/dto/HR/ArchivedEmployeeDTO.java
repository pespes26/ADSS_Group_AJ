package com.superli.deliveries.dto.HR;

public class ArchivedEmployeeDTO {
    private String id;
    private String fullName;
    private String bankAccount;
    private double salary;
    private String employmentTerms;
    private String startDate;
    private int siteId;
    private String archivedDate;

    public ArchivedEmployeeDTO() {}

    public ArchivedEmployeeDTO(String id, String fullName, String bankAccount, double salary,
                               String employmentTerms, String startDate, int siteId, String archivedDate) {
        this.id = id;
        this.fullName = fullName;
        this.bankAccount = bankAccount;
        this.salary = salary;
        this.employmentTerms = employmentTerms;
        this.startDate = startDate;
        this.siteId = siteId;
        this.archivedDate = archivedDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getEmploymentTerms() { return employmentTerms; }
    public void setEmploymentTerms(String employmentTerms) { this.employmentTerms = employmentTerms; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public int getSiteId() { return siteId; }
    public void setSiteId(int siteId) { this.siteId = siteId; }

    public String getArchivedDate() { return archivedDate; }
    public void setArchivedDate(String archivedDate) { this.archivedDate = archivedDate; }
}