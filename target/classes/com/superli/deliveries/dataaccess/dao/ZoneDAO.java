package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.ZoneDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ZoneDAO {
    List<ZoneDTO> findAll() throws SQLException;
    Optional<ZoneDTO> findById(String id) throws SQLException;
    ZoneDTO save(ZoneDTO zone) throws SQLException;
    void deleteById(String id) throws SQLException;
    
    // Domain-specific queries
    List<ZoneDTO> findActiveZones() throws SQLException;
    Optional<ZoneDTO> findByName(String name) throws SQLException;
    List<ZoneDTO> findByCapacityRange(float minCapacity, float maxCapacity) throws SQLException;
}
