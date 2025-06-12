package com.superli.deliveries.application.controllers;
import com.superli.deliveries.domain.core.*;

import com.superli.deliveries.dataaccess.dao.HR.RoleDAO;
import com.superli.deliveries.dto.HR.RoleDTO;
import com.superli.deliveries.domain.core.Role;

public class RoleController {

    private static RoleDAO roleDAO;

    // inject DAO once (או לקבל דרך constructor אם תעבור ל־Spring)
    public static void setRoleDAO(RoleDAO dao) {
        roleDAO = dao;
    }

    public static String addNewRole(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            return "Invalid role name. Cannot be empty.";
        }

        if (!roleName.matches("[a-zA-Z ]+")) {
            return "Invalid role name. Please use letters only.";
        }

        try {
            if (roleDAO.findByName(roleName).isPresent()) {
                return "Role already exists.";
            }

            RoleDTO dto = new RoleDTO();
            roleDAO.save(dto); // שומר לטבלת roles

            return "Role '" + roleName + "' added successfully.";
        } catch (Exception e) {
            return "Error adding role: " + e.getMessage();
        }
    }


    /**
     * Adds an existing role from the system to a specific employee.
     *
     * @param hrManager the HRManager instance managing the system state
     * @param employeeId the ID of the employee
     * @param roleName the name of the role to assign
     * @return message indicating success or failure
     */
    public static String addRoleToEmployee(HRManager hrManager, String employeeId, String roleName) {
        Employee employee = hrManager.FindEmployeeByID(employeeId);
        if (employee == null) {
            return "Employee not found.";
        }

        Role foundRole = null;
        for (Role role : hrManager.getAllRoles()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                foundRole = role;
                break;
            }
        }

        if (foundRole == null) {
            return "Role not found.";
        }

        if (employee.getRoleQualifications().contains(foundRole)) {
            return "Employee already has this role.";
        }

        employee.addRoleQualification(foundRole);
        return "Role added to employee successfully.";
    }

    /**
     * Validates whether a given string is a valid role name.
     *
     * @param roleName role name string
     * @return true if valid, false otherwise
     */
    public static boolean isValidRoleName(String roleName) {
        return roleName != null && roleName.matches("[a-zA-Z 0]+");
    }


}