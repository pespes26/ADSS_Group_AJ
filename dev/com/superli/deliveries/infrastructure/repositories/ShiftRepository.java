package com.superli.deliveries.infrastructure.repositories;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.domain.ports.IShiftRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
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
    public void save(Shift shift) {
        shifts.put(shift.getShiftId(), shift);
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
                .filter(Shift::isPastShift)
                .collect(Collectors.toList());
    }

    @Override
    public List<Shift> findActive() {
        return shifts.values().stream()
                .filter(shift -> !shift.isPastShift())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Shift> deleteById(String id) {
        return Optional.ofNullable(shifts.remove(id));
    }

    @Override
    public Shift update(Shift shift) {
        if (!shifts.containsKey(shift.getShiftId())) {
            throw new IllegalArgumentException("Shift with ID " + shift.getShiftId() + " does not exist");
        }
        shifts.put(shift.getShiftId(), shift);
        return shift;
    }

    @Override
    public void clearAll() {
        shifts.clear();
    }

    @Override
    public Collection<Shift> findByEmployeeId(String employeeId) {
        return List.of();
    }

    @Override
    public List<Shift> findByDate(LocalDate date) {
        return shifts.values().stream()
                .filter(shift -> shift.getShiftDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(date))
                .collect(Collectors.toList());
    }
}