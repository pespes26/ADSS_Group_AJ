package com.superli.deliveries.infrastructure.repositories.employee;

import com.superli.deliveries.domain.enums.employee.*;
import com.superli.deliveries.domain.ports.employee.IEmployeeRepository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the employee repository.
 */
public class EmployeeRepository implements IEmployeeRepository {
    private final Map<String, Employee> employees;

    public EmployeeRepository() {
        this.employees = new HashMap<>();
    }

    @Override
    public Employee save(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        employees.put(employee.getId(), employee);
        return employee;
    }

    @Override
    public Optional<Employee> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
        return Optional.ofNullable(employees.get(id));
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(employees.values());
    }

    @Override
    public List<Employee> findByType(EmployeeType type) {
        if (type == null) {
            throw new IllegalArgumentException("Employee type cannot be null");
        }
        return employees.values().stream()
                .filter(employee -> employee.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByLicenseType(LicenseType licenseType) {
        if (licenseType == null) {
            throw new IllegalArgumentException("License type cannot be null");
        }
        return employees.values().stream()
                .filter(employee -> employee.getType() == EmployeeType.DRIVER && 
                        employee.getLicenseType() == licenseType)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        return employees.values().stream()
                .filter(employee -> employee.getRoleQualifications().contains(role))
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findAvailable() {
        return employees.values().stream()
                .filter(Employee::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be null or empty");
        }
        return employees.remove(id) != null;
    }

    @Override
    public Employee update(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }
        if (!employees.containsKey(employee.getId())) {
            throw new IllegalArgumentException("Employee with ID " + employee.getId() + " does not exist");
        }
        employees.put(employee.getId(), employee);
        return employee;
    }
} 