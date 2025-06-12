package com.superli.deliveries.application.controllers;


import java.util.List;
import java.util.Map;
import java.util.Scanner;
import com.superli.deliveries.domain.core.*;

import com.superli.deliveries.domain.core.Employee;
import com.superli.deliveries.domain.core.HRManager;
import com.superli.deliveries.domain.core.LicenseType;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.domain.core.Shift;


public class ManagerController {
    private static HRManager hrManager = new HRManager();

    public static void setHRManager(HRManager hr) {
        hrManager = hr;
    }

    public static HRManager getHRManager() {
        return hrManager;
    }

    // --------------------------------Employee Management ------------------------------------
    public static String addNewEmployee(String id, String fullName, String bankAccount, double salary,
                                        String employeeTerms, List<Role> roleQualifications, LicenseType licenseTypeIfNeeded,boolean isDriver) {
        return EmployeeManagmentController.addNewEmployee( id,  fullName,  bankAccount,  salary,
                employeeTerms, roleQualifications,  licenseTypeIfNeeded, isDriver);
    }
    public static String removeEmployeeById (Employee e){
        return EmployeeManagmentController.removeEmployeeById(e);
    }

    public static void printEmployeeById(Scanner sc){
        EmployeeManagmentController.printEmployeeById(sc);
    }

    public static String addNewRoleToEmployee(String id_str, String roleName) {
        return EmployeeManagmentController.addNewRoleToEmployee(id_str,roleName);
    }


    public static void editEmployee(Scanner sc) {
        EmployeeManagmentController.editEmployee(sc);
    }

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // --------------------------------Employee Management ------------------------------------
    // --------------------------------Role And Shift Management ------------------------------------

    public static String addNewRole(String roleName){
        return RoleController.addNewRole(roleName);
    }

    // --------------------------------Role And Shift Management ------------------------------------
    public static void assignEmployeeToShift(Scanner sc) {
        ShiftController.assignEmployeeToShift(sc);
    }


    public static void removeEmployeeFromShift(Scanner sc) {
        ShiftController.removeEmployeeFromShift(sc);
    }

    public static void viewAssignedShifts() {
        ShiftController.viewAssignedShifts();
    }

    public static void createShiftsForTheWeek(Scanner sc){
        ShiftController.createShiftsForTheWeek(sc);
    }
    public static void defineRolesForSpecificShift(Scanner sc){
        ShiftController.defineRolesForSpecificShift(sc);
    }
    // --------------------------------Archive Management ------------------------------------
    public static List<Shift> displayArchivedShifts(){
        return hrManager.getArchivedShifts().getAllArchivedShifts();
    }
    public static List<Employee> displayArchivedEmployees(){
        return hrManager.getArchivedEmployee().getArchivedEmployees();
    }

    // --------------------------------Archive Management ------------------------------------

}