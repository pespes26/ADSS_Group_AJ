package com.superli.deliveries.dataaccess.dao.del;

import com.superli.deliveries.dto.del.ArchivedTransportDTO;

import java.sql.SQLException;
import java.util.List;

public interface ArchivedTransportDAO {
    void archive(ArchivedTransportDTO transport) throws SQLException;
    List<ArchivedTransportDTO> findAllArchivedTransports() throws SQLException;
}
