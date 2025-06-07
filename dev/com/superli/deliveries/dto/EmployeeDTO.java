package com.superli.deliveries.dto;

import com.superli.deliveries.presentation.*;

public class EmployeeDTO {
    private int id;
    private String fullName;
    private String bankAccount;
    private double salary;
    private String employmentTerms;
    private String startDate;
    private String license; // nullable

    private transient EmployeeSummaryDetailsView summaryView;
    private transient EmployeeDetailsView detailsView;

    public EmployeeDTO() {
        initViews();
    }

    public EmployeeDTO(int id, String fullName, String bankAccount,
                       double salary, String employmentTerms,
                       String startDate, String license) {
        this.id = id;
        this.fullName = fullName;
        this.bankAccount = bankAccount;
        this.salary = salary;
        this.employmentTerms = employmentTerms;
        this.startDate = startDate;
        this.license = license;
        initViews();
    }

    // --- View Initializer ---
    private void initViews() {
        this.summaryView = new EmployeeSummaryDetailsView(this);
        this.detailsView = new EmployeeDetailsView(this);
    }

    // --- View Accessors ---
    public EmployeeSummaryDetailsView getSummaryView() {
        return summaryView;
    }

    public EmployeeDetailsView getDetailsView() {
        return detailsView;
    }

    // --- Getters & Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        initViews();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        initViews();
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getEmploymentTerms() {
        return employmentTerms;
    }

    public void setEmploymentTerms(String employmentTerms) {
        this.employmentTerms = employmentTerms;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}