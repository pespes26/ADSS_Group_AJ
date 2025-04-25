package com.superli.deliveries.service;

import com.superli.deliveries.domain.Truck;
import com.superli.deliveries.domain.ports.ITruckRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer responsible for business logic related to trucks.
 */
public class TruckService {

    private final ITruckRepository truckRepository;
    private final List<String> unavailableTruckPlates; // משאיות תפוסות

    public TruckService(ITruckRepository truckRepository) {
        this.truckRepository = truckRepository;
        this.unavailableTruckPlates = new ArrayList<>();
    }

    public List<Truck> getAllTrucks() {
        return new ArrayList<>(truckRepository.findAll());
    }

    public List<Truck> getAvailableTrucks() {
        return truckRepository.findAll().stream()
                .filter(truck -> !unavailableTruckPlates.contains(truck.getPlateNum()))
                .collect(Collectors.toList());
    }

    public Optional<Truck> getTruckByPlate(String plate) {
        return truckRepository.findByPlate(plate);
    }

    public void saveTruck(Truck truck) {
        truckRepository.save(truck);
    }

    public void deleteTruck(String plate) {
        truckRepository.deleteByPlate(plate);
        unavailableTruckPlates.remove(plate);
    }

    public void markTruckAsUnavailable(String plate) {
        if (!unavailableTruckPlates.contains(plate)) {
            unavailableTruckPlates.add(plate);
        }
    }

    public void markTruckAsAvailable(String plate) {
        unavailableTruckPlates.remove(plate);
    }

    public boolean isTruckAvailable(String plate) {
        return !unavailableTruckPlates.contains(plate);
    }
}
