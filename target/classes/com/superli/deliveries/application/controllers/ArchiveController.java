package com.superli.deliveries.application.controllers;

import com.superli.deliveries.dataaccess.dao.HR.ArchivedShiftDAO;
import com.superli.deliveries.dataaccess.dao.HR.ArchivedShiftDAOImpl;
import com.superli.deliveries.dataaccess.dao.HR.ArchivedEmployeeDAO;
import com.superli.deliveries.dataaccess.dao.HR.ArchivedEmployeeDAOImpl;
import com.superli.deliveries.dto.HR.ArchivedEmployeeDTO;
import com.superli.deliveries.dto.HR.ArchivedShiftDTO;
import com.superli.deliveries.util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ArchiveController {

    public static List<ArchivedShiftDTO> getAllArchivedShifts() {
        try {
            Connection conn = Database.getConnection();
            ArchivedShiftDAO dao = new ArchivedShiftDAOImpl(conn);
            return dao.findAllArchivedShifts();
        } catch (SQLException e) {
            System.err.println("Failed to fetch archived shifts: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static List<ArchivedEmployeeDTO> getAllArchivedEmployees() {
        try {
            Connection conn = Database.getConnection();
            ArchivedEmployeeDAO dao = new ArchivedEmployeeDAOImpl(conn);
            return dao.findAll();
        } catch (SQLException e) {
            System.err.println("Failed to fetch archived employees: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
