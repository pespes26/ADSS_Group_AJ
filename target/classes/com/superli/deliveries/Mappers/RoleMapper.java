package com.superli.deliveries.Mappers;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.*;

public class RoleMapper {

    public static Role fromDTO(RoleDTO dto) {
        if (dto == null) return null;
        return new Role(dto.getName()); // Role מכיל רק את שם התפקיד
    }

    public static RoleDTO toDTO(Role role, int id) {
        if (role == null) return null;
        return new RoleDTO(id, role.getRoleName());
    }
}
