package com.superli.deliveries.dataaccess.dao;

import com.superli.deliveries.dto.SiteDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SiteDAO {
    Optional<SiteDTO> findById(int id) throws SQLException;
    List<SiteDTO> findAll() throws SQLException;
    SiteDTO save(SiteDTO t) throws SQLException;
    void deleteById(int id) throws SQLException;
}