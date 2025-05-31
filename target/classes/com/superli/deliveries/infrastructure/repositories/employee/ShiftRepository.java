package com.superli.deliveries.infrastructure.repositories.employee;

import com.superli.deliveries.domain.enums.employee.Employee;
import com.superli.deliveries.domain.enums.employee.Role;
import com.superli.deliveries.domain.enums.employee.Shift;
import com.superli.deliveries.domain.enums.employee.ShiftType;
import com.superli.deliveries.domain.ports.employee.IShiftRepository;

import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of IShiftRepository that manages shifts in memory.
 */
public class ShiftRepository implements IShiftRepository {
    private final Map<String, Shift> shifts;

    public ShiftRepository() {
        this.shifts = new HashMap<>();
    }

    @Override
    public Shift save(Shift shift) {
        shifts.put(shift.getId(), shift);
        return shift;
    }

    @Override
    public Optional<Shift> findById(String id) {
        return Optional.ofNullable(shifts.get(id));
    }

    @Override
    public List<Shift> findAll() {
        return new ArrayList<>(shifts.values());
    }

    @Override
    public List<Shift> findByType(ShiftType type) {
        return shifts.values().stream()
                .filter(shift -> shift.getShiftType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Shift> findByDay(DayOfWeek day) {
        return shifts.values().stream()
                .filter(shift -> shift.getShiftDay() == day)
                .collect(Collectors.toList());
    }

    @Override
    public List<Shift> findByDateRange(Date startDate, Date endDate) {
        return shifts.values().stream()
                .filter(shift -> !shift.getShiftDate().before(startDate) && !shift.getShiftDate().after(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<Shift> findByRequiredRole(Role role) {
        return shifts.values().stream()
                .filter(shift -> shift.getShiftRequiredRoles().contains(role))
                .collect(Collectors.toList());
    }

    @Override
    public List<Shift> findByAssignedEmployee(Employee employee) {
        return shifts.values().stream()
                .filter(shift -> shift.getShiftEmployees().containsKey(employee))
                .collect(Collectors.toList());
    }

    @Override
    public List<Shift> findByManager(Employee manager) {
        return shifts.values().stream()
                .filter(shift -> shift.getShiftManager().equals(manager))
                .collect(Collectors.toList());
    }

    @Override
    public List<Shift> findArchived() {
        return shifts.values().stream()
                .filter(Shift::isArchived)
                .collect(Collectors.toList());
    }

    @Override
    public List<Shift> findActive() {
        return shifts.values().stream()
                .filter(shift -> !shift.isArchived())
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(String id) {
        return shifts.remove(id) != null;
    }

    @Override
    public Shift update(Shift shift) {
        if (!shifts.containsKey(shift.getId())) {
            throw new IllegalArgumentException("Shift with ID " + shift.getId() + " does not exist");
        }
        shifts.put(shift.getId(), shift);
        return shift;
    }
} 