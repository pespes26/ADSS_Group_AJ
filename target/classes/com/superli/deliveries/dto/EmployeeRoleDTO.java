package com.superli.deliveries.dto;

public class EmployeeRoleDTO {
    private int employeeId;
    private int roleId;
    private String roleName;

    public EmployeeRoleDTO() {}

    public EmployeeRoleDTO(int employeeId, int roleId, String roleName) {
        this.employeeId = employeeId;
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}