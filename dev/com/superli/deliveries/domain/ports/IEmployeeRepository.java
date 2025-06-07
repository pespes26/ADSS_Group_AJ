package com.superli.deliveries.domain.ports;

import com.superli.deliveries.domain.core.Employee;
import com.superli.deliveries.domain.core.Role;

import java.util.Collection;
import java.util.Optional;

/**
 * Interface defining the contract for accessing Employee data.
 */
public interface IEmployeeRepository {

    /**
     * Saves (adds or updates) an employee. Identified by employee ID.
     *
     * @param employee The Employee object to save. Cannot be null.
     */
    void save(Employee employee);

    /**
     * Finds an employee by their unique ID.
     *
     * @param employeeId The ID of the employee to find.
     * @return An Optional containing the Employee if found, or an empty Optional otherwise.
     */
    Optional<Employee> findById(String employeeId);

    /**
     * Finds all employees currently stored.
     *
     * @return A Collection of all Employee objects. Maybe empty but not null.
     */
    Collection<Employee> findAll();

    /**
     * Deletes an employee by their unique ID.
     *
     * @param employeeId The ID of the employee to delete.
     * @return An Optional containing the removed employee if it was found and deleted,
     * or an empty Optional otherwise.
     */
    Optional<Employee> deleteById(String employeeId);

    /**
     * Clears all employee data. Mainly for testing purposes.
     */
    void clearAll();

    /**
     * Finds employees by their role.
     *
     * @param role The role to search for.
     * @return A Collection of Employee objects with the specified role.
     */
    Collection<Employee> findByRole(Role role);

    void update(Employee employee);
}