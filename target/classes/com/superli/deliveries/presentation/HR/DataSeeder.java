package com.superli.deliveries.presentation.HR;

import com.superli.deliveries.application.controllers.EmployeeController;
import com.superli.deliveries.application.controllers.ManagerController;
import com.superli.deliveries.application.services.RoleService;
import com.superli.deliveries.application.services.ShiftService;
import com.superli.deliveries.dataaccess.dao.HR.RoleDAO;
import com.superli.deliveries.dataaccess.dao.HR.RoleDAOImpl;
import com.superli.deliveries.dataaccess.dao.HR.ShiftDAOImpl;
import com.superli.deliveries.dataaccess.dao.del.SiteDAO;
import com.superli.deliveries.dataaccess.dao.del.SiteDAOImpl;
import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.del.SiteDTO;
import com.superli.deliveries.util.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.*;

public class DataSeeder {

    public static void seedAllData() {
        try {
            Connection conn = Database.getConnection();
            RoleDAO roleDAO = new RoleDAOImpl(conn);
            new RoleService(roleDAO);

            System.out.println("--- Seeding database with default data ---");


            RoleService.ensureSystemRolesExist();

            List<String> additionalRoles = Arrays.asList("driver", "warehouse worker", "cashier", "cleaner");
            for (String role : additionalRoles) {
                RoleService.addNewRole(role);
            }

            HRManager hr = ManagerController.getHRManager();

            Role shiftManagerRole =  new Role(roleDAO.findByName("shift manager").orElseThrow().getName());
            Role transportationManagerRole = new Role(roleDAO.findByName("transportation manager").orElseThrow().getName());
            Role driverRole = new Role(roleDAO.findByName("driver").orElseThrow().getName());
            Role warehouseWorkerRole = new Role(roleDAO.findByName("warehouse worker").orElseThrow().getName());
            Role cashierRole = new Role(roleDAO.findByName("cashier").orElseThrow().getName());
            Role cleanerRole = new Role(roleDAO.findByName("cleaner").orElseThrow().getName());



            hr.addRole(shiftManagerRole);
            hr.addRole(transportationManagerRole);
            hr.addRole(driverRole);
            hr.addRole(warehouseWorkerRole);
            hr.addRole(cashierRole);
            hr.addRole(cleanerRole);


            List<Role> aviRoles = new ArrayList<>(Arrays.asList(shiftManagerRole, driverRole));
            ManagerController.addNewEmployee("111111111", "Avi Cohen", "123-456", 5000, 2,
                    "Full-time", aviRoles, LicenseType.C, true);

            List<Role> danaRoles = new ArrayList<>(Arrays.asList(warehouseWorkerRole,shiftManagerRole));
            ManagerController.addNewEmployee("222222222", "Dana Levi", "456-789", 4800, 2,
                    "Part-time", danaRoles, null, false);

            List<Role> saraRoles = new ArrayList<>(Collections.singletonList(cleanerRole));
            ManagerController.addNewEmployee("333333333", "Sara Ben-David", "321-654", 4600, 2,
                    "Full-time", saraRoles, null, false);

            List<Role> itayRoles = new ArrayList<>(Arrays.asList(cleanerRole, transportationManagerRole));
            ManagerController.addNewEmployee("444444444", "Itay Malka", "987-654", 5100, 2,
                    "Full-time", itayRoles, null, false);


            for (Employee emp : hr.getEmployees()) {
                for (DayOfWeek day : DayOfWeek.values()) {
                    EmployeeController.addNewAvailability(emp, day, ShiftType.MORNING);
                    EmployeeController.addNewAvailability(emp, day, ShiftType.EVENING);
                }
            }
            SiteDAO siteDAO = new SiteDAOImpl();

            SiteDTO site1 = new SiteDTO("1", "123 Main St", "03-5551234", "Rina Katz", "North");
            SiteDTO site2 = new SiteDTO("2", "456 Herzl St", "03-6667890", "Moshe Levi", "Center");
            SiteDTO site3 = new SiteDTO("-1", "456 Herzl St", "03-6667890", "Moshe Levi", "Center");
            siteDAO.save(site1);
            siteDAO.save(site2);
            siteDAO.save(site3);

            Map<String, Integer> requiredRoles = new HashMap<>();
            requiredRoles.put("shift manager", 1);
            requiredRoles.put("driver", 1);
            requiredRoles.put("warehouse worker", 1);
            requiredRoles.put("cashier", 1);
            requiredRoles.put("cleaner", 1);
            requiredRoles.put("transportation manager", 2);
            ShiftService.createShiftsForTheWeek();


            ShiftService.defineRolesForSpecificShift(
                    DayOfWeek.SUNDAY,
                    ShiftType.MORNING,
                    requiredRoles,
                    2
            );




            int site1Int = Integer.parseInt(site1.getSiteId());
            int site2Int = Integer.parseInt(site2.getSiteId());

            ShiftDAOImpl shiftDAO = new ShiftDAOImpl(conn);
            shiftDAO.saveAssignment("222222222", "SUNDAY", "MORNING", "2025-06-22", 1,site1Int); // shift manager
            shiftDAO.saveAssignment("111111111", "SUNDAY", "MORNING", "2025-06-22", 3,site2Int); // driver

            System.out.println("--- Seeding complete ---");

        } catch (SQLException e) {
            System.err.println("Error seeding database: " + e.getMessage());
        }
    }
}