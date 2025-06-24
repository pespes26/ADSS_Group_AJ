package com.superli.deliveries.application.services;

import com.superli.deliveries.Mappers.EmployeeMapper;
import com.superli.deliveries.application.controllers.ManagerController;
import com.superli.deliveries.dataaccess.dao.HR.*;
import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.HR.*;
import com.superli.deliveries.util.Database;
import com.superli.deliveries.presentation.EmployeeManagmentView;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class EmployeeManagmentService {
    private static final EmployeeManagmentView view = new EmployeeManagmentView();

    public static String addNewEmployee(String id, String fullName, String bankAccount, double salary, int site,
                                        String employeeTerms, List<Role> roleQualifications, LicenseType licenseTypeIfNeeded, boolean isDriver) {
        HRManager hr = ManagerController.getHRManager();

        Date employeeStartDate = new Date();
        List<AvailableShifts> availabilityConstraints = new ArrayList<>();
        Role loginRole = new Role("");

        Employee newEmployee;

        if (isDriver) {
            if (licenseTypeIfNeeded == null) {
                return "License type is required for driver.";
            }

            newEmployee = new Driver(id, fullName, bankAccount, salary, site, employeeTerms,
                    employeeStartDate, roleQualifications, availabilityConstraints, loginRole, licenseTypeIfNeeded);
        } else {
            newEmployee = new Employee(id, fullName, bankAccount, salary, site, employeeTerms,
                    employeeStartDate, roleQualifications, availabilityConstraints, loginRole);
        }

        boolean addedSuccessfully = hr.addEmployee(newEmployee);

        if (addedSuccessfully) {
            try {
                EmployeeDAO employeeDAO = new EmployeeDAOImpl();
                RoleDAO roleDAO = new RoleDAOImpl();
                EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAOImpl();

                EmployeeDTO dto = EmployeeMapper.toDTO(newEmployee);
                employeeDAO.save(dto);
                for (Role role : roleQualifications) {
                    RoleDTO roleDTO = roleDAO.findByName(role.getRoleName()).orElse(null);
                    if (roleDTO != null) {
                        employeeRoleDAO.assignRole(Integer.parseInt(id), roleDTO.getId());
                    }
                }
            } catch (Exception e) {
                return "Employee added but DB update failed: " + e.getMessage();
            }
        }

        return addedSuccessfully
                ? (isDriver ? "Driver added successfully." : "Employee added successfully.")
                : "Failed to add employee.";
    }

    public static String removeEmployeeById(Employee e) {
        HRManager hr = ManagerController.getHRManager();
        hr.removeAndArchiveEmployee(e);

        try {
            EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAOImpl();
            employeeRoleDAO.removeRolesByEmployeeId(Integer.parseInt(e.getId()));

            EmployeeDAO employeeDAO = new EmployeeDAOImpl();
            employeeDAO.deleteById(e.getId());

            return "Employee with ID " + e.getId() + " removed and archived.";

        } catch (SQLException ex) {
            ex.printStackTrace();
            return "Error archiving/removing employee from DB: " + ex.getMessage();
        }
    }


    public static String printEmployeeById(String id) {
        HRManager hr = ManagerController.getHRManager();
        Employee employee = hr.FindEmployeeByID(id);

        if (employee == null) {
            return "Employee with ID " + id + " not found.";

        }
        return employee.toString();
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

    public static String addNewRoleToEmployee(String id_str, String roleName) {
        HRManager hr = ManagerController.getHRManager();
        Employee employee = hr.FindEmployeeByID(id_str);

        if (employee == null) {
            return "Employee with ID " + id_str + " not found.";
        }

        Role foundRole = null;
        for (Role role : hr.getAllRoles()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                foundRole = role;
                break;
            }
        }

        if (foundRole == null) {
            return "Role '" + roleName + "' not found in the system.";
        }

        if (employee.getRoleQualifications().contains(foundRole)) {
            return "Employee already has the role '" + roleName + "'.";
        } else {
            employee.addRoleQualification(foundRole);
            try {
                RoleDAO roleDAO = new RoleDAOImpl();
                EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAOImpl();
                RoleDTO roleDTO = roleDAO.findByName(foundRole.getRoleName()).orElse(null);
                if (roleDTO != null) {
                    employeeRoleDAO.assignRole(Integer.parseInt(id_str), roleDTO.getId());
                }
            } catch (SQLException ex) {
                return "Error updating DB with new role: " + ex.getMessage();
            }
            return "Role '" + roleName + "' added to employee successfully.";
        }
    }

    public static void editEmployee(Scanner sc) {
        HRManager hr = ManagerController.getHRManager();
        Employee employee = null;

        while (employee == null) {
            view.promptEmployeeId();
            String id = sc.nextLine().trim();

            if (id.equalsIgnoreCase("exit")) {
                view.printReturningToMenu();
                return;
            }

            employee = hr.FindEmployeeByID(id);

            if (employee == null) {
                view.printEmployeeNotFound(id);
            }
        }

        view.printEditingHeader(employee);

        try {
            Connection conn = Database.getConnection();
            EmployeeDAO employeeDAO = new EmployeeDAOImpl(conn);
            RoleDAO roleDAO = new RoleDAOImpl(conn);
            EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAOImpl(conn);

            boolean editing = true;
            while (editing) {
                view.printEditMenu();
                String choice = sc.nextLine().trim();

                switch (choice) {
                    case "1":
                        view.promptFullName();
                        String fullName = sc.nextLine().trim();
                        if (!fullName.isEmpty()) {
                            employee.setFullName(fullName);
                        }
                        break;

                    case "2":
                        view.promptBankAccount();
                        String bankAccount = sc.nextLine().trim();
                        if (!bankAccount.isEmpty()) {
                            employee.setBankAccount(bankAccount);
                        }
                        break;

                    case "3":
                        view.promptSalary();
                        String salaryStr = sc.nextLine().trim();
                        if (isNumeric(salaryStr)) {
                            double salary = Double.parseDouble(salaryStr);
                            employee.setSalary(salary);
                        }
                        break;

                    case "4":
                        List<Role> newQualifications = new ArrayList<>();
                        while (true) {
                            view.promptRoleName();
                            String roleName = sc.nextLine().trim();
                            if (roleName.equalsIgnoreCase("done")) break;

                            RoleDTO roleDTO = roleDAO.findByName(roleName).orElse(null);
                            if (roleDTO == null) {
                                view.printRoleNotFound(roleName);
                            } else {
                                newQualifications.add(new Role(roleDTO.getName()));
                            }
                        }

                        employee.setRoleQualifications(newQualifications);
                        employeeRoleDAO.removeRolesByEmployeeId(Integer.parseInt(employee.getId()));
                        for (Role role : newQualifications) {
                            RoleDTO roleDTO = roleDAO.findByName(role.getRoleName()).orElse(null);
                            if (roleDTO != null) {
                                employeeRoleDAO.assignRole(Integer.parseInt(employee.getId()), roleDTO.getId());
                            }
                        }
                        break;

                    case "0":
                        editing = false;
                        break;

                    default:
                        view.printInvalidChoice();
                }

                try {
                    employeeDAO.save(EmployeeMapper.toDTO(employee));
                } catch (SQLException e) {
                    view.printUpdateError(e.getMessage());
                }
            }

        } catch (SQLException e) {
            view.printDatabaseError(e.getMessage());
        }
    }


}
