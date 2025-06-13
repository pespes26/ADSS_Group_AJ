package com.superli.deliveries.dataaccess.dao.HR;

import com.superli.deliveries.dto.HR.RoleDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RoleDAO {
    Optional<RoleDTO> findById(int id) throws SQLException;
    List<RoleDTO> findAll() throws SQLException;
    RoleDTO save(RoleDTO t) throws SQLException;
    void deleteById(int id) throws SQLException;
    Optional<RoleDTO> findByName(String name) throws SQLException;


}