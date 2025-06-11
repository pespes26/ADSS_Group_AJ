package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.TruckDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TruckDAO {
    List<TruckDTO> findAll() throws SQLException;
    Optional<TruckDTO> findById(int id) throws SQLException;
    Optional<TruckDTO> findByPlate(String plate) throws SQLException;
    TruckDTO save(TruckDTO truck) throws SQLException;
    void deleteById(int id) throws SQLException;
    void deleteByPlate(String plate) throws SQLException;
    
    // Domain-specific queries
    List<TruckDTO> findAvailableTrucks() throws SQLException;
    List<TruckDTO> findByType(String type) throws SQLException;
    List<TruckDTO> findByCapacityRange(float minCapacity, float maxCapacity) throws SQLException;
}