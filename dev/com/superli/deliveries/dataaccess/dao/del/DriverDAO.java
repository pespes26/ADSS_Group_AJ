package com.superli.deliveries.dataaccess.dao.del;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.superli.deliveries.dto.del.DriverDTO;

public interface DriverDAO {
    Optional<DriverDTO> findById(String id) throws SQLException;
    List<DriverDTO> findAll() throws SQLException;
    DriverDTO save(DriverDTO driver) throws SQLException;
    void delete(String id) throws SQLException;
    List<DriverDTO> findAvailableDrivers() throws SQLException;
    List<DriverDTO> findUnavailableDrivers() throws SQLException;
    List<DriverDTO> findByLicenseType(String licenseType) throws SQLException;
    Optional<DriverDTO> findByLicenseNumber(String licenseNumber) throws SQLException;
    void updateDriverAvailability(String id, boolean available) throws SQLException;
}