package com.superli.deliveries.infrastructure.repositories;

import com.superli.deliveries.domain.Truck;
import com.superli.deliveries.domain.ports.ITruckRepository;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the in-memory storage for Truck objects.
 * Acts as an in-memory repository for trucks.
 * This class implements the ITruckRepository interface.
 */
public class TruckRepository implements ITruckRepository {

    // Key: license plate number
    private final Map<String, Truck> truckMapByPlate;

    /**
     * Constructs a new TruckRepository.
     */
    public TruckRepository() {
        this.truckMapByPlate = new ConcurrentHashMap<>();
    }

    @Override
    public void save(Truck truck) {
        Objects.requireNonNull(truck, "Truck cannot be null");
        Objects.requireNonNull(truck.getPlateNum(), "Truck license plate number cannot be null");
        truckMapByPlate.put(truck.getPlateNum(), truck);
        System.out.println("Truck saved/updated: " + truck.getPlateNum());
    }

    @Override
    public Optional<Truck> findByPlate(String licensePlateNumber) {
        return Optional.ofNullable(truckMapByPlate.get(licensePlateNumber));
    }

    @Override
    public Collection<Truck> findAll() {
        return Collections.unmodifiableCollection(truckMapByPlate.values());
    }

    @Override
    public Optional<Truck> deleteByPlate(String licensePlateNumber) {
        if (licensePlateNumber != null) {
            Truck removed = truckMapByPlate.remove(licensePlateNumber);
            if (removed != null) {
                System.out.println("Truck removed: " + licensePlateNumber);
            }
            return Optional.ofNullable(removed);
        }
        return Optional.empty();
    }

    @Override
    public void clearAll() {
        truckMapByPlate.clear();
        System.out.println("TruckRepository cleared.");
    }
}