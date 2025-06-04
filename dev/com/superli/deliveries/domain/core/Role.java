package com.superli.deliveries.domain.core;

import java.util.Objects;

/**
 * Represents a role or qualification that an employee can have.
 */
public class Role {
    private String roleName;

    public Role(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }
        this.roleName = roleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return roleName.equals(role.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleName);
    }

    @Override
    public String toString() {
        return roleName;
    }
} 