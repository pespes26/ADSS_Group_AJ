package com.superli.deliveries.application.services;

import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.domain.ports.IEmployeeRepository;
import com.superli.deliveries.domain.ports.IShiftRepository;
import com.superli.deliveries.dto.ShiftDTO;
import com.superli.deliveries.Mappers.ShiftMapper;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class ShiftService {

    private final IShiftRepository shiftRepository;
    private final IEmployeeRepository employeeRepository;

    public ShiftService(IShiftRepository shiftRepository, IEmployeeRepository employeeRepository) {
        this.shiftRepository = shiftRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<ShiftDTO> getAllShifts() {
        return shiftRepository.findAll().stream()
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ShiftDTO> getShiftById(int shiftId) {
        return shiftRepository.findById(String.valueOf(shiftId))
                .map(ShiftMapper::toDTO);
    }

    public void saveShift(ShiftDTO shiftDTO, List<Role> requiredRoles,
                          Map<Employee, Role> assignedEmployees, Employee manager) {
        Shift shift = ShiftMapper.fromDTO(shiftDTO, requiredRoles, assignedEmployees, manager);
        shiftRepository.save(shift);
    }

    public boolean deleteShift(int shiftId) {
        Optional<Shift> shiftOpt = shiftRepository.findById(String.valueOf(shiftId));
        if (shiftOpt.isPresent()) {
            shiftRepository.deleteById(String.valueOf(shiftId));
            return true;
        }
        return false;
    }

    public boolean addEmployeeToShift(int shiftId, Employee employee, Role role) {
        Optional<Shift> shiftOpt = shiftRepository.findById(String.valueOf(shiftId));
        if (shiftOpt.isPresent()) {
            Shift shift = shiftOpt.get();
            shift.addEmployeeToShift(employee, role);
            shiftRepository.save(shift);
            return true;
        }
        return false;
    }

    public boolean removeEmployeeFromShift(int shiftId, String employeeId) {
        Optional<Shift> shiftOpt = shiftRepository.findById(String.valueOf(shiftId));
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);

        if (shiftOpt.isPresent() && employeeOpt.isPresent()) {
            Shift shift = shiftOpt.get();
            Employee employee = employeeOpt.get();

            shift.removeEmployeeFromShift(employee);
            shiftRepository.save(shift);
            return true;
        }
        return false;
    }

    public List<ShiftDTO> getShiftsByDate(LocalDate date) {
        return shiftRepository.findAll().stream()
                .filter(s -> s.getShiftDate().equals(date))
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ShiftDTO> getShiftsByDayOfWeek(DayOfWeek day) {
        return shiftRepository.findAll().stream()
                .filter(s -> s.getShiftDay().equals(day))
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ShiftDTO> getShiftsForEmployee(String employeeId) {
        return shiftRepository.findAll().stream()
                .filter(s -> s.getShiftEmployees().keySet().stream()
                        .anyMatch(e -> e.getId().equals(employeeId)))
                .map(ShiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    public boolean isShiftInPast(Shift shift) {
        if (shift == null || shift.getShiftDate() == null) return true;
//        return shift.getShiftDate().isBefore(LocalDate.now()); // didn't work so i switched to this:
        return shift.getShiftDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now());

    }

    public Shift createShift(Date date, ShiftType type, DayOfWeek day,
                             List<Role> requiredRoles,
                             Map<Employee, Role> assignedEmployees,
                             Employee manager) {
        Shift shift = new Shift(
                generateNewShiftId(),
                date,
                type,
                day,
                requiredRoles,
                assignedEmployees,
                manager
        );
        shiftRepository.save(shift);
        return shift;
    }
    private int generateNewShiftId() {
        return shiftRepository.findAll().stream()
                .mapToInt(Shift::getShiftId)
                .max()
                .orElse(0) + 1;
    }

    //    public boolean updateShiftManager(int shiftId, Employee newManager) { // isn't a demand
//        Optional<Shift> shiftOpt = shiftRepository.findById(String.valueOf(shiftId));
//        if (shiftOpt.isPresent()) {
//            Shift shift = shiftOpt.get();
//            shift.setShiftManager(newManager);
//            shiftRepository.save(shift);
//            return true;
//        }
//        return false;
//    }

//    public boolean updateShiftRequiredRoles(int shiftId, List<Role> newRoles) { // isn't a demand
//        Optional<Shift> shiftOpt = shiftRepository.findById(String.valueOf(shiftId));
//        if (shiftOpt.isPresent()) {
//            Shift shift = shiftOpt.get();
//            shift.setShiftRequiredRoles(newRoles);
//            shiftRepository.save(shift);
//            return true;
//        }
//        return false;
//    }
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