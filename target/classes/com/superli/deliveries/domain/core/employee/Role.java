package domain.core.employee;

/**
 * Represents a role that an employee can have in the system.
 */
public class Role {
    private final String roleName;

    /**
     * Constructs a new Role.
     *
     * @param roleName Name of the role. Cannot be null or empty.
     */
    public Role(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty.");
        }
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleName='" + roleName + '\'' +
                '}';
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
        return roleName.hashCode();
    }
}
