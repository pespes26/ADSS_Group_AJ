package domain.core.employee;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages archived employees in the system.
 */
public class ArchivedEmployee {
    private final List<Employee> archivedEmployees;

    /**
     * Constructs a new ArchivedEmployee with the given list of archived employees.
     *
     * @param archivedEmployees List of archived employees.
     */
    public ArchivedEmployee(List<Employee> archivedEmployees) {
        if (archivedEmployees == null) {
            throw new IllegalArgumentException("Archived employees list cannot be null.");
        }
        this.archivedEmployees = archivedEmployees;
    }

    /**
     * Constructs a new ArchivedEmployee with an empty list.
     */
    public ArchivedEmployee() {
        this.archivedEmployees = new ArrayList<>();
    }

    /**
     * Adds an employee to the archive if they are not already archived.
     *
     * @param employee The employee to archive.
     */
    public void addArchivedEmployee(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null.");
        }
        if (!archivedEmployees.contains(employee)) {
            archivedEmployees.add(employee);
        }
    }

    /**
     * Gets the list of archived employees.
     *
     * @return A copy of the list of archived employees.
     */
    public List<Employee> getArchivedEmployees() {
        return List.copyOf(archivedEmployees);
    }

    @Override
    public String toString() {
        return "ArchivedEmployee{" +
                "archivedEmployees=" + archivedEmployees +
                '}';
    }
}
