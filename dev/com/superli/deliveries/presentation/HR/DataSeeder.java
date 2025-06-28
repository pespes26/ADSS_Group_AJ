package com.superli.deliveries.presentation.HR;

import com.superli.deliveries.application.controllers.EmployeeController;
import com.superli.deliveries.application.controllers.ManagerController;
import com.superli.deliveries.application.services.*;
import com.superli.deliveries.config.DataInitializer;
import com.superli.deliveries.dataaccess.dao.HR.RoleDAO;
import com.superli.deliveries.dataaccess.dao.HR.RoleDAOImpl;
import com.superli.deliveries.dataaccess.dao.HR.ShiftDAOImpl;
import com.superli.deliveries.dataaccess.dao.del.SiteDAO;
import com.superli.deliveries.dataaccess.dao.del.SiteDAOImpl;
import com.superli.deliveries.domain.core.*;
import com.superli.deliveries.dto.del.SiteDTO;
import com.superli.deliveries.util.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
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

            List<Role> shiraRoles = new ArrayList<>(Arrays.asList(shiftManagerRole, driverRole));
            ManagerController.addNewEmployee("555555555", "Shira Cohen", "123-457", 5000, 2,
                    "Full-time", shiraRoles, LicenseType.B, true);


            for (Employee emp : hr.getEmployees()) {
                for (DayOfWeek day : DayOfWeek.values()) {
                    EmployeeController.addNewAvailability(emp, day, ShiftType.MORNING);
                    EmployeeController.addNewAvailability(emp, day, ShiftType.EVENING);
                }
            }

            // === Clear and seed Zones and Sites ===
            java.sql.Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM sites");
            stmt.executeUpdate("DELETE FROM zones");
            // === Seed Zones FIRST ===
            com.superli.deliveries.dataaccess.dao.del.ZoneDAO zoneDAO = new com.superli.deliveries.dataaccess.dao.del.ZoneDAOImpl();
            com.superli.deliveries.dto.del.ZoneDTO zone1 = new com.superli.deliveries.dto.del.ZoneDTO("1", "North");
            com.superli.deliveries.dto.del.ZoneDTO zone2 = new com.superli.deliveries.dto.del.ZoneDTO("2", "Center");
            zoneDAO.save(zone1);
            zoneDAO.save(zone2);
            // === Seed Sites, referencing zone IDs ===
            SiteDAO siteDAO = new SiteDAOImpl();
            SiteDTO site1 = new SiteDTO("1", "123 Main St", "03-5551234", "Rina Katz", "1"); // zone_id = "1"
            SiteDTO site2 = new SiteDTO("2", "456 Herzl St", "03-6667890", "Moshe Levi", "2"); // zone_id = "2"
            SiteDTO site3 = new SiteDTO("-1", "456 Herzl St", "03-6667890", "Moshe Levi", "2"); // zone_id = "2"
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
            shiftDAO.saveAssignment("111111111", "SATURDAY", "MORNING", "2025-06-28", 3,site2Int); // driver
            shiftDAO.saveAssignment("111111111", "SATURDAY", "EVENING", "2025-06-28", 3,site2Int); // driver

            // === Seed Trucks ===
            stmt.executeUpdate("DELETE FROM trucks");
            com.superli.deliveries.dataaccess.dao.del.TruckDAO truckDAO = new com.superli.deliveries.dataaccess.dao.del.TruckDAOImpl();
            com.superli.deliveries.dto.del.TruckDTO truck1 = new com.superli.deliveries.dto.del.TruckDTO("123-45-678", "Volvo FH", 8000f, 32000f, com.superli.deliveries.domain.core.LicenseType.C);
            truck1.setAvailable(true);
            com.superli.deliveries.dto.del.TruckDTO truck2 = new com.superli.deliveries.dto.del.TruckDTO("987-65-432", "Mercedes Actros", 9000f, 35000f, com.superli.deliveries.domain.core.LicenseType.E);
            truck2.setAvailable(true);
            com.superli.deliveries.dto.del.TruckDTO truck3 = new com.superli.deliveries.dto.del.TruckDTO("555-11-222", "DAF XF", 8500f, 34000f, com.superli.deliveries.domain.core.LicenseType.C1);
            truck3.setAvailable(true);
            com.superli.deliveries.dto.del.TruckDTO truck4 = new com.superli.deliveries.dto.del.TruckDTO("333-22-444", "Scania R", 8700f, 33000f, com.superli.deliveries.domain.core.LicenseType.C2);
            truck4.setAvailable(true);
            com.superli.deliveries.dto.del.TruckDTO truck5 = new com.superli.deliveries.dto.del.TruckDTO("777-88-999", "MAN TGX", 9200f, 36000f, com.superli.deliveries.domain.core.LicenseType.B);
            truck5.setAvailable(true);
            truckDAO.save(truck1);
            truckDAO.save(truck2);
            truckDAO.save(truck3);
            truckDAO.save(truck4);
            truckDAO.save(truck5);

            System.out.println("--- Seeding complete ---");

        } catch (SQLException e) {
            System.err.println("Error seeding database: " + e.getMessage());
        }
    }
}