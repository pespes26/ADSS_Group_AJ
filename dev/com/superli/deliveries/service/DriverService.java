package com.superli.deliveries.service;

import com.superli.deliveries.domain.Driver;
import com.superli.deliveries.domain.LicenseType;
import com.superli.deliveries.domain.Truck;
import com.superli.deliveries.domain.ports.IDriverRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer responsible for business logic related to drivers.
 */
public class DriverService {

    private final IDriverRepository driverRepository;
    private final List<String> unavailableDriverIds; // IDs of drivers currently unavailable

    public DriverService(IDriverRepository driverRepository) {
        this.driverRepository = driverRepository;
        this.unavailableDriverIds = new ArrayList<>();
    }

    /**
     * Returns all drivers in the system.
     * @return List of all drivers.
     */
    public List<Driver> getAllDrivers() {
        return new ArrayList<>(driverRepository.findAll());
    }

    /**
     * Returns all available drivers (those not currently marked as unavailable).
     * @return List of available drivers.
     */
    public List<Driver> getAvailableDrivers() {
        return driverRepository.findAll().stream()
                .filter(driver -> !unavailableDriverIds.contains(driver.getDriverId()))
                .collect(Collectors.toList());
    }

    /**
     * Finds a driver by ID.
     * @param driverId The ID of the driver.
     * @return Optional containing the driver, if found.
     */
    public Optional<Driver> getDriverById(String driverId) {
        return driverRepository.findById(driverId);
    }

    /**
     * Adds or updates a driver in the repository.
     * @param driver The driver to save.
     */
    public void saveDriver(Driver driver) {
        driverRepository.save(driver);
    }

    /**
     * Removes a driver from the system.
     * @param driverId The ID of the driver to remove.
     */
    public void deleteDriver(String driverId) {
        driverRepository.deleteById(driverId);
        unavailableDriverIds.remove(driverId);
    }

    /**
     * Marks a driver as currently unavailable (e.g., assigned to a transport).
     * @param driverId The driver's ID.
     */
    public void markDriverAsUnavailable(String driverId) {
        if (!unavailableDriverIds.contains(driverId)) {
            unavailableDriverIds.add(driverId);
        }
    }

    /**
     * Marks a driver as available again (e.g., transport completed).
     * @param driverId The driver's ID.
     */
    public void markDriverAsAvailable(String driverId) {
        unavailableDriverIds.remove(driverId);
    }

    /**
     * Checks if a driver is qualified to drive a given truck.
     * @param driver The driver.
     * @param truck The truck.
     * @return true if license types match.
     */
    public boolean isDriverQualifiedForTruck(Driver driver, Truck truck) {
        return String.valueOf(driver.getLicenseType()).equals(truck.getRequiredLicenseType());
    }

    /**
     * Updates a driver's name.
     * @param driverId The ID of the driver to update.
     * @param newName The new name for the driver.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateDriverName(String driverId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return false; // Invalid name
        }

        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        if (driverOpt.isPresent()) {
            Driver driver = driverOpt.get();
            driver.setName(newName);
            driverRepository.save(driver);
            return true;
        }
        return false;
    }

    /**
     * Updates a driver's license type.
     * @param driverId The ID of the driver to update.
     * @param newLicenseType The new license type for the driver.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateDriverLicenseType(String driverId, LicenseType newLicenseType) {
        if (newLicenseType == null) {
            return false; // Invalid license type
        }

        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        if (driverOpt.isPresent()) {
            Driver driver = driverOpt.get();
            driver.setLicenseType(newLicenseType);
            driverRepository.save(driver);
            return true;
        }
        return false;
    }

    /**
     * Updates a driver's availability status.
     * @param driverId The ID of the driver to update.
     * @param isAvailable The new availability status.
     * @return true if the update was successful, false otherwise.
     */
    public boolean updateDriverAvailability(String driverId, boolean isAvailable) {
        Optional<Driver> driverOpt = driverRepository.findById(driverId);
        if (driverOpt.isPresent()) {
            Driver driver = driverOpt.get();
            driver.setAvailable(isAvailable);
            driverRepository.save(driver);

            // Update our tracking of unavailable drivers
            if (isAvailable) {
                unavailableDriverIds.remove(driverId);
            } else {
                if (!unavailableDriverIds.contains(driverId)) {
                    unavailableDriverIds.add(driverId);
                }
            }
            return true;
        }
        return false;
    }
}