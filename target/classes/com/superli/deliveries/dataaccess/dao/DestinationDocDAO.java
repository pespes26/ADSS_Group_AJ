package com.superli.deliveries.dataaccess.dao;

import java.util.List;
import java.util.Optional;

import com.superli.deliveries.dto.DestinationDocDTO;

public interface DestinationDocDAO {
    List<DestinationDocDTO> findAll();
    Optional<DestinationDocDTO> findById(String id);
    DestinationDocDTO save(DestinationDocDTO doc);
    void deleteById(String id);
    
    // Domain-specific queries
    List<DestinationDocDTO> findByTransportId(String transportId);
    List<DestinationDocDTO> findByStatus(String status);
    List<DestinationDocDTO> findPendingDocs();
}