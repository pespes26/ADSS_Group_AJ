package com.superli.deliveries.dto.HR;

import com.superli.deliveries.presentation.HR.EmployeeDetailsView;
import com.superli.deliveries.presentation.HR.EmployeeSummaryDetailsView;

public class EmployeeDTO {
    private String id;
    private String name;
    private String bankAccount;
    private double salary;
    private String employmentTerms;
    private String startDate;

    private transient EmployeeSummaryDetailsView summaryView;
    private transient EmployeeDetailsView detailsView;

    public EmployeeDTO() {
        initViews();
    }

    public EmployeeDTO(String id, String name, String bankAccount,
                       double salary, String employmentTerms,
                       String startDate) {
        this.id = id;
        this.name = name;
        this.bankAccount = bankAccount;
        this.salary = salary;
        this.employmentTerms = employmentTerms;
        this.startDate = startDate;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        initViews();
    }

    public String getFullName() {
        return name;
    }

    public void setFullName(String name) {
        this.name = name;
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

}