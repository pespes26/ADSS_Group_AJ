package com.superli.deliveries.dataaccess.dao.del;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.superli.deliveries.dto.del.TruckDTO;

public interface TruckDAO {
    List<TruckDTO> findAll() throws SQLException;
    Optional<TruckDTO> findById(String plateNum) throws SQLException;
    Optional<TruckDTO> findByPlate(String plate) throws SQLException;
    TruckDTO save(TruckDTO truck) throws SQLException;
    void deleteById(String plateNum) throws SQLException;
    void deleteByPlate(String plate) throws SQLException;
    void updateTruckAvailability(String plateNum, boolean available) throws SQLException;
    
    // Domain-specific queries
    List<TruckDTO> findAvailableTrucks() throws SQLException;
    List<TruckDTO> findByType(String type) throws SQLException;
    List<TruckDTO> findByCapacityRange(float minCapacity, float maxCapacity) throws SQLException;
    List<TruckDTO> findByLicenseType(String licenseType) throws SQLException;
}