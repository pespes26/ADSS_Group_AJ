package com.superli.deliveries.application.services;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dataaccess.dao.ShiftDAO;
import com.superli.deliveries.dataaccess.dao.EmployeeDAO;
import com.superli.deliveries.dto.ShiftDTO;
import com.superli.deliveries.Mappers.ShiftMapper;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ShiftService {

    private final ShiftDAO shiftDAO;
    private final EmployeeDAO employeeDAO;

    public ShiftService(ShiftDAO shiftDAO, EmployeeDAO employeeDAO) {
        this.shiftDAO = shiftDAO;
        this.employeeDAO = employeeDAO;
    }

    public List<Shift> getAllShifts() {
        try {
            return shiftDAO.findAll().stream()
                    .<Shift>map(dto -> ShiftMapper.toDomain(dto))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all shifts", e);
        }
    }

    public Optional<Shift> getShiftById(String shiftId) {
        try {
            return shiftDAO.findById(shiftId)
                    .map(dto -> ShiftMapper.toDomain(dto));
        } catch (SQLException e) {
            throw new RuntimeException("Error getting shift by id: " + shiftId, e);
        }
    }

    public void saveShift(Shift shift) {
        try {
            ShiftDTO dto = ShiftMapper.toDTO(shift);
            shiftDAO.save(dto);
        } catch (SQLException e) {
            throw new RuntimeException("Error saving shift", e);
        }
    }

    public boolean deleteShift(String shiftId) {
        try {
            Optional<ShiftDTO> shiftOpt = shiftDAO.findById(shiftId);
            if (shiftOpt.isPresent()) {
                shiftDAO.deleteById(shiftId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting shift: " + shiftId, e);
        }
    }

    public List<Shift> getShiftsByDate(LocalDate date) {
        try {
            return shiftDAO.findByDate(date).stream()
                    .<Shift>map(dto -> ShiftMapper.toDomain(dto))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting shifts by date: " + date, e);
        }
    }

    public List<Shift> getShiftsByDayOfWeek(DayOfWeek day) {
        try {
            return shiftDAO.findByDayOfWeek(day).stream()
                    .<Shift>map(dto -> ShiftMapper.toDomain(dto))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting shifts by day: " + day, e);
        }
    }

    public List<Shift> getShiftsForEmployee(String employeeId) {
        try {
            return shiftDAO.findByEmployeeId(employeeId).stream()
                    .<Shift>map(dto -> ShiftMapper.toDomain(dto))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting shifts for employee: " + employeeId, e);
        }
    }

    public boolean isShiftInPast(Shift shift) {
        if (shift == null || shift.getShiftDate() == null) return true;
        return shift.getShiftDate().before(new Date());
    }

    public Shift createShift(LocalDate date, ShiftType type, DayOfWeek day, List<Role> requiredRoles, Map<Employee, Role> assignedEmployees, Employee manager) {
        try {
            Shift shift = new Shift(
                    generateNewShiftId(),
                    Date.from(date.atStartOfDay().toInstant(java.time.ZoneOffset.UTC)),
                    type,
                    day,
                    requiredRoles,
                    assignedEmployees,
                    manager
            );
            saveShift(shift);
            return shift;
        } catch (Exception e) {
            throw new RuntimeException("Error creating shift", e);
        }
    }

    private String generateNewShiftId() {
        try {
            return UUID.randomUUID().toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating new shift ID", e);
        }
    }
}

//public class ShiftService {
//
//    /**
//     * Creates a new shift with required roles and manager.
//     */
//    public Shift createShift(Date date, ShiftType type, DayOfWeek day,
//                             List<Role> requiredRoles, Map<Employee, Role> assignedEmployees, Employee manager) {
//        int shiftId = UUID.randomUUID().hashCode();
//        return new Shift(shiftId, date, type, day, requiredRoles, assignedEmployees, manager);
//    }
//
//    /**
//     * Assigns an employee to a shift, if valid.
//     */
//    public boolean assignEmployee(Shift shift, Employee employee, Role role) {
//        if (shift.isEmployeeAssigned(employee)) return false;
//        if (!shift.getShiftRequiredRoles().contains(role)) return false;
//
//        shift.addEmployeeToShift(employee, role);
//        return true;
//    }
//
//    /**
//     * Removes an employee from a shift.
//     */
//    public boolean removeEmployee(Shift shift, Employee employee) {
//        if (!shift.isEmployeeAssigned(employee)) return false;
//
//        shift.removeEmployeeFromShift(employee);
//        return true;
//    }
//
//    /**
//     * Checks if a shift is fully staffed.
//     */
//    public boolean isFullyAssigned(Shift shift) {
//        return shift.isShiftFullyAssigned();
//    }
//
//    /**
//     * Checks if a shift already ended.
//     */
//    public boolean isShiftInPast(Shift shift) {
//        return shift.isPastShift();
//    }
//}