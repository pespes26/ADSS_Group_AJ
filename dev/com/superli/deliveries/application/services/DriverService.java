package com.superli.deliveries.application.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.superli.deliveries.Mappers.DriverMapper;
import com.superli.deliveries.dataaccess.dao.HR.EmployeeDAOImpl;
import com.superli.deliveries.dataaccess.dao.del.DriverDAO;
import com.superli.deliveries.dataaccess.dao.del.DriverDAOImpl;
import com.superli.deliveries.domain.core.Driver;
import com.superli.deliveries.domain.core.LicenseType;
import com.superli.deliveries.domain.core.Truck;
import com.superli.deliveries.domain.core.Transport;
import com.superli.deliveries.domain.core.TransportStatus;
import com.superli.deliveries.dto.HR.EmployeeDTO;

/**
 * Service layer responsible for business logic related to drivers.
 */
public class DriverService {

    private final DriverDAO driverDAO;
    private final List<String> unavailableDriverIds;
    private final TransportService transportService;

    public DriverService(TransportService transportService) {
        this.driverDAO = new DriverDAOImpl();
        this.unavailableDriverIds = new ArrayList<>();
        this.transportService = transportService;
    }
    // Get all drivers from DB
    public List<Driver> getAllDrivers() {
        try {
            List<EmployeeDTO> drivers = new ArrayList<>();
            EmployeeDAOImpl empService = new EmployeeDAOImpl();
            return empService.findByRole("driver").stream()
                    .map(DriverMapper::fromDTO)  // ממפה כל EmployeeDTO ל-Driver
                    .collect(Collectors.toList());        } catch (SQLException e) {
            throw new RuntimeException("Error getting all drivers", e);
        }
    }

    // Get only available drivers (not marked as unavailable)
    public List<Driver> getAvailableDrivers() {
        return getAllDrivers().stream()
                .filter(driver -> !isDriverAssignedToTransport(driver.getDriverId()))
                .filter(Driver::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Driver> getUnavailableDrivers() {
        try {
            return driverDAO.findUnavailableDrivers().stream()
                    .map(DriverMapper::fromDTO)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting unavailable drivers", e);
        }
    }

    // Find specific driver by ID
    public Optional<Driver> getDriverById(String Employee_id) {
        try {
            return driverDAO.findById(Employee_id)
                    .map(DriverMapper::fromDTO);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting driver by id: " + Employee_id, e);
        }
    }

    // Save driver to DB
    public void saveDriver(Driver driver) {
        try {
            driverDAO.save(DriverMapper.toDTO(driver));
        } catch (SQLException e) {
            throw new RuntimeException("Error saving driver", e);
        }
    }

    // Delete driver from DB
    public void deleteDriver(String Employee_id) {
        try {
            driverDAO.delete(Employee_id);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting driver: " + Employee_id, e);
        }
    }

    /**
     * Checks if a driver is assigned to any active transport.
     * @param driverId The ID of the driver to check
     * @return true if the driver is assigned to an active transport, false otherwise
     */
    public boolean isDriverAssignedToTransport(String driverId) {
        try {
            List<Transport> transports = transportService.getAllTransports();
            return transports.stream()
                    .filter(t -> t.getDriver().getDriverId().equals(driverId))
                    .anyMatch(t -> t.getStatus() != TransportStatus.COMPLETED && 
                                 t.getStatus() != TransportStatus.CANCELLED);
        } catch (Exception e) {
            throw new RuntimeException("Error checking driver assignment", e);
        }
    }

    /**
     * Updates a driver's availability status.
     * @param driverId The ID of the driver
     * @param available The new availability status
     * @throws IllegalStateException if trying to make a driver available while assigned to an active transport
     */
    public void updateDriverAvailability(String driverId, boolean available) {
        try {
            if (available && isDriverAssignedToTransport(driverId)) {
                throw new IllegalStateException("Driver is assigned to an active transport and cannot be made available");
            }
            driverDAO.updateDriverAvailability(driverId, available);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating driver availability", e);
        }
    }

    /**
     * Marks a driver as unavailable (e.g., when assigned to a transport).
     * @param driverId The ID of the driver
     */
    public void markDriverAsUnavailable(String driverId) {
        updateDriverAvailability(driverId, false);
    }

    /**
     * Marks a driver as available (e.g., when transport is completed/cancelled).
     * @param driverId The ID of the driver
     */
    public void markDriverAsAvailable(String driverId) {
        updateDriverAvailability(driverId, true);
    }

    // Check if driver can drive a specific truck
    public boolean isDriverQualifiedForTruck(Driver driver, Truck truck) {
        return driver.getLicenseType() == truck.getRequiredLicenseType();
    }

    // Update driver's license type
    public void updateDriverLicenseType(String Employee_id, LicenseType licenseType) {
        Optional<Driver> driver = getDriverById(Employee_id);
        driver.ifPresent(d -> {
            d.setLicenseType(licenseType);
            saveDriver(d);
        });
    }

    public List<Driver> getDriversByLicenseType(String licenseType) {
        try {
            return driverDAO.findAll().stream()
                    .map(DriverMapper::fromDTO)
                    .filter(d -> d.getLicenseType().toString().equals(licenseType))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting drivers by license type: " + licenseType, e);
        }
    }

    public boolean isDriverAvailable(String Employee_id) {
        Optional<Driver> driver = getDriverById(Employee_id);
        return driver.map(Driver::isAvailable).orElse(false);
    }

    public boolean hasValidLicense(String Employee_id, LicenseType requiredType) {
        Optional<Driver> driver = getDriverById(Employee_id);
        return driver.map(d -> d.getLicenseType() == requiredType).orElse(false);
    }

    public void releaseDriver(Driver driver) {
        if (driver != null) {
            driver.setAvailable(true);
            saveDriver(driver);
        }
    }
}

///**
// * Service layer responsible for business logic related to drivers.
// */
//public class DriverService {
//
//    private final IDriverRepository driverRepository;
//    private final List<String> unavailableDriverIds; // IDs of drivers currently unavailable
//
//    public DriverService(IDriverRepository driverRepository) {
//        this.driverRepository = driverRepository;
//        this.unavailableDriverIds = new ArrayList<>();
//    }
//
//    /**
//     * Returns all drivers in the system.
//     * @return List of all drivers.
//     */
//    public List<Driver> getAllDrivers() {
//        return new ArrayList<>(driverRepository.findAll());
//    }
//
//    /**
//     * Returns all available drivers (those not currently marked as unavailable).
//     * @return List of available drivers.
//     */
//    public List<Driver> getAvailableDrivers() {
//        return driverRepository.findAll().stream()
//                .filter(driver -> !unavailableDriverIds.contains(driver.getDriverId()))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Finds a driver by ID.
//     * @param driverId The ID of the driver.
//     * @return Optional containing the driver, if found.
//     */
//    public Optional<Driver> getDriverById(String driverId) {
//        return driverRepository.findById(driverId);
//    }
//
//    /**
//     * Adds or updates a driver in the repository.
//     * @param driver The driver to save.
//     */
//    public void saveDriver(Driver driver) {
//        driverRepository.save(driver);
//    }
//
//    /**
//     * Removes a driver from the system.
//     * @param driverId The ID of the driver to remove.
//     */
//    public void deleteDriver(String driverId) {
//        driverRepository.deleteById(driverId);
//        unavailableDriverIds.remove(driverId);
//    }
//
//    /**
//     * Marks a driver as currently unavailable (e.g., assigned to a transport).
//     * @param driverId The driver's ID.
//     */
//    public void markDriverAsUnavailable(String driverId) {
//        if (!unavailableDriverIds.contains(driverId)) {
//            unavailableDriverIds.add(driverId);
//        }
//    }
//
//    /**
//     * Marks a driver as available again (e.g., transport completed).
//     * @param driverId The driver's ID.
//     */
//    public void markDriverAsAvailable(String driverId) {
//        unavailableDriverIds.remove(driverId);
//    }
//
//    /**
//     * Checks if a driver is qualified to drive a given truck.
//     * @param driver The driver.
//     * @param truck The truck.
//     * @return true if license types match.
//     */
//    public boolean isDriverQualifiedForTruck(Driver driver, Truck truck) {
//        return String.valueOf(driver.getLicenseType()).equals(truck.getRequiredLicenseType());
//    }
//
//    /**
//     * Updates a driver's license type.
//     * @param driverId The ID of the driver to update.
//     * @param newLicenseType The new license type for the driver.
//     * @return true if the update was successful, false otherwise.
//     */
//    public boolean updateDriverLicenseType(String driverId, LicenseType newLicenseType) {
//        if (newLicenseType == null) {
//            return false; // Invalid license type
//        }
//
//        Optional<Driver> driverOpt = driverRepository.findById(driverId);
//        if (driverOpt.isPresent()) {
//            Driver driver = driverOpt.get();
//            driver.setLicenseType(newLicenseType);
//            driverRepository.save(driver);
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Updates a driver's availability status.
//     * @param driverId The ID of the driver to update.
//     * @param isAvailable The new availability status.
//     * @return true if the update was successful, false otherwise.
//     */
//    public boolean updateDriverAvailability(String driverId, boolean isAvailable) {
//        Optional<Driver> driverOpt = driverRepository.findById(driverId);
//        if (driverOpt.isPresent()) {
//            Driver driver = driverOpt.get();
//            driver.setAvailable(isAvailable);
//            driverRepository.save(driver);
//
//            // Update our tracking of unavailable drivers
//            if (isAvailable) {
//                unavailableDriverIds.remove(driverId);
//            } else {
//                if (!unavailableDriverIds.contains(driverId)) {
//                    unavailableDriverIds.add(driverId);
//                }
//            }
//            return true;
//        }
//        return false;
//    }
//}