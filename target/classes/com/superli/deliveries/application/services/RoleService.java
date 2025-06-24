package com.superli.deliveries.application.services;
import com.superli.deliveries.application.controllers.ManagerController;
import com.superli.deliveries.dataaccess.dao.HR.RoleDAOImpl;
import com.superli.deliveries.domain.core.*;

import com.superli.deliveries.dataaccess.dao.HR.RoleDAO;
import com.superli.deliveries.dto.HR.RoleDTO;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RoleService {

    private static RoleDAO roleDAO;

    public RoleService(RoleDAO dao) {
        RoleService.roleDAO = dao;
    }


    public static void setRoleDAO(RoleDAO dao) {
        roleDAO = dao;
    }

    public static String addNewRole(String roleName) {


        HRManager hr = ManagerController.getHRManager();
        if (roleName == null || roleName.trim().isEmpty()) {
            return "Invalid role name. Cannot be empty.";
        }

        if (!roleName.matches("[a-zA-Z ]+")) {
            return "Invalid role name. Please use letters only.";
        }
        try {
            hr.addRole(new Role(roleName));
            RoleDTO dto = new RoleDTO(roleName);
            roleDAO.save(dto);

            return "Role '" + roleName + "' added successfully.";
        } catch (Exception e) {
            return "Error adding role: " + e.getMessage();
        }
    }
    public static void ensureSystemRolesExist() {
        try {
            if (roleDAO.findByName("shift manager").isEmpty()) {
                RoleDTO dto1 = new RoleDTO("shift manager");
                roleDAO.save(dto1);
                ManagerController.getHRManager().addRole(new Role("shift manager"));
                System.out.println("System role 'shift manager' added automatically.");
            }
            if (roleDAO.findByName("transportation manager").isEmpty()) {
                RoleDTO dto2 = new RoleDTO("transportation manager");
                roleDAO.save(dto2);
                ManagerController.getHRManager().addRole(new Role("transportation manager"));
                System.out.println("System role 'transportation manager' added automatically.");
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to ensure system roles: " + e.getMessage());
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

    public static List<String> getAllRoleNames() {
        try {
            Connection conn = Database.getConnection();
            RoleDAO roleDAO = new RoleDAOImpl(conn);

            return roleDAO.findAll().stream()
                    .map(RoleDTO::getName)
                    .map(String::toLowerCase)
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch role names", e);
        }
    }


}