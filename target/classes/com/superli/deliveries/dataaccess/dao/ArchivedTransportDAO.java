package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.ArchivedTransportDTO;

import java.sql.SQLException;
import java.util.List;

public interface ArchivedTransportDAO {
    void archive(ArchivedTransportDTO transport) throws SQLException;
    List<ArchivedTransportDTO> findAllArchivedTransports() throws SQLException;
}
