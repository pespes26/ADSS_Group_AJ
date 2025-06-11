package com.superli.deliveries.application.services;

import com.superli.deliveries.domain.core.Employee;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.domain.core.ShiftType;
import com.superli.deliveries.domain.core.*;

//import java.time.DayOfWeek;
import java.time.DayOfWeek;
import java.util.*;

public class EmployeeService {
    private final Map<String, Employee> employees = new HashMap<>();

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees.values());
    }

    public Optional<Employee> findById(String id) {
        return Optional.ofNullable(employees.get(id));
    }

    public void addEmployee(Employee employee) {
        if (employees.containsKey(employee.getId())) {
            throw new IllegalArgumentException("Employee with ID " + employee.getId() + " already exists");
        }
        employees.put(employee.getId(), employee);
    }

    public void updateEmployee(Employee employee) {
        if (!employees.containsKey(employee.getId())) {
            throw new IllegalArgumentException("Employee with ID " + employee.getId() + " not found");
        }
        employees.put(employee.getId(), employee);
    }

    public boolean removeEmployee(String id) {
        return employees.remove(id) != null;
    }

    public List<Employee> findByRole(Role role) {
        return employees.values().stream()
                .filter(e -> e.getRoleQualifications().contains(role))
                .toList();
    }

    public List<Employee> findAvailableOn(DayOfWeek day, Role requiredRole, ShiftType shiftType) {
        return employees.values().stream()
                .filter(e -> e.isQualified(requiredRole) && e.isAvailable(day, shiftType))
                .toList();
    }

    public void clearAllEmployees() {
        employees.clear();
    }
}