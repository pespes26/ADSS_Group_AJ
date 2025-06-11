package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.*;

public class EmployeeRoleMapper {

    public static EmployeeRoleDTO toDTO(Employee employee, Role role, int roleId) {
        if (employee == null || role == null) return null;
        return new EmployeeRoleDTO(
                Integer.parseInt(employee.getId()),
                roleId,
                role.getRoleName()
        );
    }

    public static Role fromDTO(EmployeeRoleDTO dto) {
        if (dto == null) return null;
        return new Role(dto.getRoleName());
    }
}
