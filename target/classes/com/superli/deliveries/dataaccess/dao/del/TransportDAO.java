package com.superli.deliveries.dataaccess.dao.del;

import com.superli.deliveries.dto.del.TransportDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface TransportDAO {
    List<TransportDTO> findAll() throws SQLException;
    Optional<TransportDTO> findById(String id) throws SQLException;
    TransportDTO save(TransportDTO transport) throws SQLException;
    void deleteById(String id) throws SQLException;
    void updateStatus(String transportId, String newStatus) throws SQLException;

        // Domain-specific queries
    List<TransportDTO> findByDriverId(String driverId) throws SQLException;
    List<TransportDTO> findByTruckPlate(String truckPlate) throws SQLException;
    List<TransportDTO> findByStatus(String status) throws SQLException;
    List<TransportDTO> findActiveTransports() throws SQLException;

}
