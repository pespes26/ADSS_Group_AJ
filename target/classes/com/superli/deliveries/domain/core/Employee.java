package com.superli.deliveries.domain.core;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;

public class Employee {
    private String id;
    private String fullName;
    private String bankAccount;
    private double salary;
    private int siteId; //new
    private List<Role> roleQualifications;
    private String employeeTerms;
    private Date employeeStartDate;

    private List<AvailableShifts> availabilityConstraints;
    //private boolean activeWorker;
    //private Role loginRole;

    public Employee(String id, String fullName, String bankAccount, double salary, int siteId,
                    String employeeTerms, Date employeeStartDate,
                    List<Role> roleQualifications, List<AvailableShifts> availabilityConstraints, Role loginRole) {
        this.id = id;
        this.fullName = fullName;
        this.bankAccount = bankAccount;
        this.salary = salary;
        this.siteId = siteId; // new
        this.employeeTerms = employeeTerms;
        this.employeeStartDate = employeeStartDate;
        this.roleQualifications = roleQualifications != null ? roleQualifications : new java.util.ArrayList<>();
        this.availabilityConstraints = availabilityConstraints != null ? availabilityConstraints : new java.util.ArrayList<>();
        //this.activeWorker = activeWorker;
        //this.loginRole = loginRole;
    }

    //Getter method - return employee's ID
    public String getId() {
        return id;
    }

    //Getter method - return employee's full name
    public String getFullName() {
        return fullName;
    }

    //Getter method - return employee's bank account
    public String getBankAccount() {
        return bankAccount;
    }

    //Getter method - return employee's salary
    public double getSalary() {
        return salary;
    }

    //Getter method - return employee's terms
    public String getEmployeeTerms() {
        return employeeTerms;
    }

    //Getter method - return employee's Date of start day of working
    public Date getEmployeeStartDate() {
        return employeeStartDate;
    }

    //Getter method - return employee's list of his role qualifications
    public List<Role> getRoleQualifications() {
        return roleQualifications;
    }

    //Getter method - return employee's list of his availability constraints
    public List<AvailableShifts> getAvailabilityConstraints() {
        return availabilityConstraints;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setRoleQualifications(List<Role> roleQualifications) {
        this.roleQualifications = roleQualifications;
    }

    public int getSiteId() {return siteId;} // new

    public void setSiteId(int siteId) {this.siteId = siteId;} //new

    public String getEmploymentTerms() {
        return this.employeeTerms;
    }

    public Date getStartDate() {
        return this.employeeStartDate;
    }

    // ---------------------------------- Checking methods ------------------------------------
    /**
     * Checks if the employee is available to work on a given day and shift type.
     * @param day - day of the week to check if the employee is available.
     * @param shiftType - the type of the shift to check if the employee is available.
     * @return boolean value true - is the employee is available for this day and shift.
     *         boolean value false - otherwise.
     */
    public boolean isAvailable(DayOfWeek day, ShiftType shiftType) {
        for (AvailableShifts option : availabilityConstraints) {
            if (option.getDay().equals(day) && option.getShift() == shiftType) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the employee is qualified for a given role.
     *
     * @param role - the role to check if the employee is qualified for.
     * @return the boolean value true - if the employee is qualified for the role.
     *         the boolean value false - otherwise.
     */
    public boolean isQualified(Role role) {
        return roleQualifications.contains(role);
    }
    // ---------------------------------- End Checking methods ------------------------------------


    // ---------------------------------- Add methods ------------------------------------

    /**
     * Add a new role to the employee qualification list if the employee is not qualified for the given role.
     * @param role - the role to add to the employee qualification list
     */
    public void addRoleQualification(Role role){
        if (!roleQualifications.contains(role)){
            roleQualifications.add(role);
        }
    }

    /**
     * Add a new available shift to the employee availabilityConstraints list if the shift is not in the list.
     * @param shift - the shit to add to the employee availabilityConstraints list
     */
    public void addAvailableShift(AvailableShifts shift){
        if (!availabilityConstraints.contains(shift)){
            availabilityConstraints.add(shift);
        }
    }


    // ---------------------------------- End Add methods ------------------------------------

    public void printEmployee() {
        System.out.println("Employee Info:");
        System.out.println("Full name:" + fullName);
        System.out.println("Id:" + id);
        System.out.println("Bank account:" + bankAccount);
        System.out.println("Salary:" + salary);
        System.out.println("Terms:" + employeeTerms);
        System.out.println("Start date:" + employeeStartDate);
        System.out.print("Role Qualification :[");
        for (int i = 0; i < roleQualifications.size(); i++) {
            if (i < roleQualifications.size() - 1) {
                System.out.print(roleQualifications.get(i).getRoleName() + ", ");
            } else {
                System.out.print(roleQualifications.get(i).getRoleName());
            }
        }
        System.out.println("]");
        System.out.print("Availability Constraints:[");
        for (int i = 0; i < availabilityConstraints.size(); i++) {
            if (i < availabilityConstraints.size() - 1) {
                System.out.print(availabilityConstraints.get(i).getDay() + "-" + availabilityConstraints.get(i).getShift() + ", ");
            } else {
                System.out.print(availabilityConstraints.get(i).getDay() + "-" + availabilityConstraints.get(i).getShift());
            }

        }
        System.out.println("]");
    }

    public void addAvailability(AvailableShifts availability) {
        if (!availabilityConstraints.contains(availability)) {
            availabilityConstraints.add(availability);
        }
    }

    public void clearAvailability() {
        availabilityConstraints.clear();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee e = (Employee) o;
        return id.equals(e.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = formatter.format(employeeStartDate);
        return '\n' + "---Employee Details--- " + '\n' +
                "ID: " + id + '\n' +
                "Name: " + fullName + '\n' +
                "Bank account: " + bankAccount + '\n' +
                "Salary: " + salary + '\n' +
                "Terms: " + employeeTerms + '\n' +
                "Start date: " + formattedDate + '\n' +
                "----------------------";

        //(this.getLicense() != null ? ", license='" + employee.getLicense() + '\'' : "") +

    }

}