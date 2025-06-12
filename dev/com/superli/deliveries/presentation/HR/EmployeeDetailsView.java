package com.superli.deliveries.presentation.HR;

import com.superli.deliveries.dto.HR.EmployeeDTO;

public class EmployeeDetailsView {
    private final EmployeeDTO employee;

    public EmployeeDetailsView(EmployeeDTO employee) {
        this.employee = employee;
    }

    public void display() {
        System.out.println("====== Employee Details ======");
        System.out.println("ID: " + employee.getId());
        System.out.println("Name: " + employee.getFullName());
        System.out.println("Bank Account: " + employee.getBankAccount());
        System.out.println("Salary: $" + employee.getSalary());
        System.out.println("Employment Terms: " + employee.getEmploymentTerms());
        System.out.println("Start Date: " + employee.getStartDate());

        /*if (employee.getLicense() != null) {
            System.out.println("License: " + employee.getLicense());
        }*/
        System.out.println("==============================");
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + employee.getId() +
                ", name='" + employee.getFullName() + '\'' +
                ", bankAccount='" + employee.getBankAccount() + '\'' +
                ", salary=" + employee.getSalary() +
                ", terms='" + employee.getEmploymentTerms() + '\'' +
                ", startDate='" + employee.getStartDate() + '\'' +
                //(employee.getLicense() != null ? ", license='" + employee.getLicense() + '\'' : "") +
                '}';
    }
}
