package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.ZoneDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ZoneDAO {
    Optional<ZoneDTO> findById(int id) throws SQLException;
    List<ZoneDTO> findAll() throws SQLException;
    ZoneDTO save(ZoneDTO t) throws SQLException;
    void deleteById(int id) throws SQLException;
}
