package com.superli.deliveries.application.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.superli.deliveries.Mappers.TruckMapper;
import com.superli.deliveries.domain.core.Truck;
import com.superli.deliveries.dataaccess.dao.del.TruckDAO;
import com.superli.deliveries.dto.del.TruckDTO;

import java.sql.SQLException;

public class TruckService {

    private final TruckDAO truckDAO;

    public TruckService(TruckDAO truckDAO) {
        this.truckDAO = truckDAO;
    }

    public List<Truck> getAllTrucks() {
        try {
            return truckDAO.findAll().stream()
                    .map(TruckMapper::fromDTO)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all trucks", e);
        }
    }

    public Optional<Truck> getTruckById(String id) {
        try {
            return truckDAO.findByPlate(id)
                    .map(TruckMapper::fromDTO);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting truck by id: " + id, e);
        }
    }

    public void saveTruck(Truck truck) {
        try {
            truckDAO.save(TruckMapper.toDTO(truck));
        } catch (SQLException e) {
            throw new RuntimeException("Error saving truck", e);
        }
    }

    public void deleteTruck(String id) {
        try {
            truckDAO.deleteByPlate(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting truck: " + id, e);
        }
    }

    public List<Truck> getAvailableTrucks() {
        try {
            return truckDAO.findAvailableTrucks().stream()
                    .map(TruckMapper::fromDTO)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting available trucks", e);
        }
    }

    public List<Truck> getTrucksByType(String type) {
        try {
            return truckDAO.findByType(type).stream()
                    .map(TruckMapper::fromDTO)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting trucks by type: " + type, e);
        }
    }

    public Optional<Truck> getTruckByPlate(String plate) {
        try {
            return truckDAO.findByPlate(plate)
                    .map(TruckMapper::fromDTO);
        } catch (SQLException e) {
            throw new RuntimeException("Error getting truck by plate: " + plate, e);
        }
    }

    public List<Truck> getTrucksByCapacityRange(float minCapacity, float maxCapacity) {
        try {
            return truckDAO.findByCapacityRange(minCapacity, maxCapacity).stream()
                    .map(TruckMapper::fromDTO)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error getting trucks by capacity range", e);
        }
    }

    public boolean markTruckAsUnavailable(String plateNum) {
        try {
            Optional<TruckDTO> truckOpt = truckDAO.findByPlate(plateNum);
            if (truckOpt.isPresent()) {
                Truck truck = TruckMapper.fromDTO(truckOpt.get());
                truck.setAvailable(false);
                truckDAO.save(TruckMapper.toDTO(truck));
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean markTruckAsAvailable(String plateNum) {
        try {
            Optional<TruckDTO> truckOpt = truckDAO.findByPlate(plateNum);
            if (truckOpt.isPresent()) {
                Truck truck = TruckMapper.fromDTO(truckOpt.get());
                truck.setAvailable(true);
                truckDAO.save(TruckMapper.toDTO(truck));
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isTruckAvailable(String plateNum) {
        try {
            return truckDAO.findByPlate(plateNum)
                    .map(dto -> dto.isAvailable())
                    .orElse(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void releaseTruck(Truck truck) {
        if (truck != null) {
            truck.setAvailable(true);
            saveTruck(truck);
        }
    }
}


///**
// * Service layer responsible for business logic related to trucks.
// * Manages truck-related operations while respecting immutability constraints.
// */
//public class TruckService {
//
//    private final ITruckRepository truckRepository;
//
//    public TruckService(ITruckRepository truckRepository) {
//        this.truckRepository = truckRepository;
//    }
//
//    /**
//     * Adds a new truck to the system.
//     *
//     * @param plateNum Unique plate number
//     * @param model Truck model
//     * @param netWeight Net weight of the truck
//     * @param maxWeight Maximum weight capacity
//     * @param requiredLicenseType Required license type
//     * @return The newly created truck
//     * @throws IllegalArgumentException if truck validation fails
//     */
//    public Truck addTruck(String plateNum, String model, float netWeight,
//                          float maxWeight, LicenseType requiredLicenseType) {
//        // Validate that a truck with this plate number doesn't already exist
//        if (truckRepository.findByPlate(plateNum).isPresent()) {
//            throw new IllegalArgumentException("Truck with plate number " + plateNum + " already exists.");
//        }
//
//        // Create and save the new truck
//        Truck newTruck = new Truck(plateNum, model, netWeight, maxWeight, requiredLicenseType);
//        truckRepository.save(newTruck);
//        return newTruck;
//    }
//
//    /**
//     * Saves a truck to the repository.
//     *
//     * @param truck The truck to save
//     */
//    public void saveTruck(Truck truck) {
//        truckRepository.save(truck);
//    }
//
//    /**
//     * Deletes a truck from the system.
//     *
//     * @param plateNum Plate number of the truck to delete
//     * @return true if truck was successfully deleted, false otherwise
//     */
//    public boolean deleteTruck(String plateNum) {
//        Optional<Truck> removedTruck = truckRepository.deleteByPlate(plateNum);
//        return removedTruck.isPresent();
//    }
//
//    /**
//     * Retrieves all trucks in the system.
//     *
//     * @return List of all trucks
//     */
//    public List<Truck> getAllTrucks() {
//        return new ArrayList<>(truckRepository.findAll());
//    }
//
//    /**
//     * Retrieves all available trucks.
//     *
//     * @return List of available trucks
//     */
//    public List<Truck> getAvailableTrucks() {
//        return truckRepository.findAll().stream()
//                .filter(Truck::isAvailable)
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * Finds a truck by its plate number.
//     *
//     * @param plateNum Plate number of the truck
//     * @return Optional containing the truck if found
//     */
//    public Optional<Truck> getTruckByPlate(String plateNum) {
//        return truckRepository.findByPlate(plateNum);
//    }
//
//    /**
//     * Updates the availability status of a truck.
//     *
//     * @param plateNum Plate number of the truck
//     * @param available New availability status
//     * @return true if update was successful, false if truck not found
//     */
//    public boolean updateTruckAvailability(String plateNum, boolean available) {
//        Optional<Truck> truckOpt = truckRepository.findByPlate(plateNum);
//
//        if (truckOpt.isPresent()) {
//            Truck truck = truckOpt.get();
//            truck.setAvailable(available);
//            truckRepository.save(truck);
//            return true;
//        }
//
//        return false;
//    }
//
//    /**
//     * Removes a truck from the system.
//     *
//     * @param plateNum Plate number of the truck to remove
//     * @return true if truck was successfully removed, false if not found
//     */
//    public boolean removeTruck(String plateNum) {
//        Optional<Truck> removedTruck = truckRepository.deleteByPlate(plateNum);
//        return removedTruck.isPresent();
//    }
//
//    /**
//     * Marks a truck as unavailable.
//     *
//     * @param plateNum Plate number of the truck
//     * @return true if successfully marked unavailable, false if truck not found
//     */
//    public boolean markTruckAsUnavailable(String plateNum) {
//        return updateTruckAvailability(plateNum, false);
//    }
//
//    /**
//     * Marks a truck as available.
//     *
//     * @param plateNum Plate number of the truck
//     * @return true if successfully marked available, false if truck not found
//     */
//    public boolean markTruckAsAvailable(String plateNum) {
//        return updateTruckAvailability(plateNum, true);
//    }
//
//    /**
//     * Checks if a truck is available.
//     *
//     * @param plateNum Plate number of the truck
//     * @return true if truck is available, false otherwise
//     */
//    public boolean isTruckAvailable(String plateNum) {
//        Optional<Truck> truckOpt = truckRepository.findByPlate(plateNum);
//        return truckOpt.map(Truck::isAvailable).orElse(false);
//    }
//}