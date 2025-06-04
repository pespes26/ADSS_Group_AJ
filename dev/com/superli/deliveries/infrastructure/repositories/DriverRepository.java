package com.superli.deliveries.infrastructure.repositories;

import com.superli.deliveries.domain.core.Driver;
import com.superli.deliveries.domain.ports.IDriverRepository;
import java.util.*;

/**
 * Manages the in-memory storage for Driver objects using a HashMap.
 * Acts as an in-memory repository for drivers.
 * This class implements the IDriverRepository interface.
 */
//             vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv הוספת implements
public class DriverRepository implements IDriverRepository {

    private final Map<String, Driver> driverMap; // Key: driverId

    /**
     * Constructs a new DriverRepository.
     */
    public DriverRepository() {
        //                    vvvvvvvv <-- שינוי ל-HashMap
        this.driverMap = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public void save(Driver driver) { // <-- שינוי שם מ-addDriver
        Objects.requireNonNull(driver, "Driver cannot be null");
        Objects.requireNonNull(driver.getDriverId(), "Driver ID cannot be null");
        driverMap.put(driver.getDriverId(), driver);
        System.out.println("Driver saved/updated: " + driver.getDriverId());
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public Optional<Driver> findById(String driverId) { // <-- שינוי שם מ-getDriverById
        return Optional.ofNullable(driverMap.get(driverId));
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public Collection<Driver> findAll() { // <-- שינוי שם מ-getAllDrivers
        return Collections.unmodifiableCollection(driverMap.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public Optional<Driver> deleteById(String driverId) { // <-- שינוי שם מ-deleteDriver
        if (driverId != null) {
            Driver removed = driverMap.remove(driverId);
            if(removed != null) {
                System.out.println("Driver removed: " + driverId);
            }
            return Optional.ofNullable(removed);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override // <-- הוספת Override
    public void clearAll() {
        driverMap.clear();
        System.out.println("DriverRepository cleared.");
    }
}