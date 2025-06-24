package com.superli.deliveries.application.controllers;


import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


import com.superli.deliveries.application.services.EmployeeManagmentService;
import com.superli.deliveries.application.services.RoleService;
import com.superli.deliveries.application.services.ShiftService;
import com.superli.deliveries.dataaccess.dao.HR.RoleDAO;
import com.superli.deliveries.dataaccess.dao.HR.RoleDAOImpl;
import com.superli.deliveries.domain.core.*;

import com.superli.deliveries.domain.core.Employee;
import com.superli.deliveries.domain.core.HRManager;
import com.superli.deliveries.domain.core.LicenseType;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.domain.core.Shift;
import com.superli.deliveries.util.Database;


public class ManagerController {
    private static HRManager hrManager = new HRManager();
    private static RoleService roleController;
    private static ShiftService shiftController;

    public static void setHRManager(HRManager hr) {
        hrManager = hr;
    }

    public static HRManager getHRManager() {
        return hrManager;
    }



    public static void initHRControllers() throws SQLException {
        Connection conn = Database.getConnection();
        RoleDAO roleDAO = new RoleDAOImpl(conn);
        roleController = new RoleService(roleDAO);
    }

    public static RoleService getRoleController() {
        return roleController;
    }
    // --------------------------------Employee Management ------------------------------------
    public static String addNewEmployee(String id, String fullName, String bankAccount, double salary, int site,
                                        String employeeTerms, List<Role> roleQualifications, LicenseType licenseTypeIfNeeded,boolean isDriver) {
        return EmployeeManagmentService.addNewEmployee(id, fullName, bankAccount, salary, site,
                employeeTerms, roleQualifications,  licenseTypeIfNeeded, isDriver);
    }
    public static String removeEmployeeById (Employee e){
        return EmployeeManagmentService.removeEmployeeById(e);
    }

    public static String printEmployeeById(String id){
        return EmployeeManagmentService.printEmployeeById(id);
    }

    public static String addNewRoleToEmployee(String id_str, String roleName) {
        return EmployeeManagmentService.addNewRoleToEmployee(id_str,roleName);
    }


    public static void editEmployee(Scanner sc) {
        EmployeeManagmentService.editEmployee(sc);
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
        return RoleService.addNewRole(roleName);
    }

    // --------------------------------Role And Shift Management ------------------------------------
    public static void assignEmployeeToShift(Scanner sc) {
        ShiftService.assignEmployeeToShift(sc);
    }


    public static void removeEmployeeFromShift(Scanner sc) {
        ShiftService.removeEmployeeFromShift(sc);
    }

    public static  List<String>  viewAssignedShifts() {
        return ShiftService.viewAssignedShifts();
    }

    public static String createShiftsForTheWeek(){

        return  ShiftService.createShiftsForTheWeek();

    }
    public static void defineRolesForSpecificShift(DayOfWeek day, ShiftType type, Map < String, Integer > roleNameToCountMap,int siteId){
        ShiftService.defineRolesForSpecificShift( day,  type,  roleNameToCountMap, siteId);
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