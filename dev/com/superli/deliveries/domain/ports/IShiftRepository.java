package com.superli.deliveries.domain.ports;

import com.superli.deliveries.domain.core.Employee;
import com.superli.deliveries.domain.core.Role;
import com.superli.deliveries.domain.core.Shift;
import com.superli.deliveries.domain.core.ShiftType;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

/**
 * Interface defining the contract for accessing Shift data.
 */
public interface IShiftRepository {

    /**
     * Saves (adds or updates) a shift. Identified by shift ID.
     *
     * @param shift The Shift object to save. Cannot be null.
     */
    void save(Shift shift);

    /**
     * Finds a shift by its unique ID.
     *
     * @param shiftId The ID of the shift to find.
     * @return An Optional containing the Shift if found, or an empty Optional otherwise.
     */
    Optional<Shift> findById(String shiftId);

    /**
     * Finds all shifts currently stored.
     *
     * @return A Collection of all Shift objects. Maybe empty but not null.
     */
    Collection<Shift> findAll();

    /**
     * Deletes a shift by its unique ID.
     *
     * @param shiftId The ID of the shift to delete.
     * @return An Optional containing the removed shift if it was found and deleted,
     * or an empty Optional otherwise.
     */
    Optional<Shift> deleteById(String shiftId);

    /**
     * Clears all shift data. Mainly for testing purposes.
     */
    void clearAll();

    /**
     * Finds shifts by date.
     *
     * @param date The date to search for shifts.
     * @return A Collection of Shift objects for the specified date.
     */
    Collection<Shift> findByDate(LocalDate date);

    /**
     * Finds shifts by employee ID.
     *
     * @param employeeId The ID of the employee.
     * @return A Collection of Shift objects assigned to the specified employee.
     */
    Collection<Shift> findByEmployeeId(String employeeId);

    List<Shift> findByDay(DayOfWeek day);

    List<Shift> findByDateRange(Date startDate, Date endDate);

    List<Shift> findByType(ShiftType type);

    List<Shift> findArchived();

    List<Shift> findByAssignedEmployee(Employee employee);

    List<Shift> findActive();

    Shift update(Shift shift);

    List<Shift> findByRequiredRole(Role role);

    List<Shift> findByManager(Employee manager);
}