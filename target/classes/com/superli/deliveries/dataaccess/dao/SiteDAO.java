package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.SiteDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SiteDAO {
    List<SiteDTO> findAll() throws SQLException;
    Optional<SiteDTO> findById(String id) throws SQLException;
    SiteDTO save(SiteDTO site) throws SQLException;
    void deleteById(String id) throws SQLException;
    
    // Domain-specific queries
    List<SiteDTO> findByZoneId(String zoneId) throws SQLException;
    List<SiteDTO> findActiveSites() throws SQLException;
    Optional<SiteDTO> findByAddress(String address) throws SQLException;
}