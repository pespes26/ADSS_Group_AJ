package com.superli.deliveries.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseResetUtil {

    private static final String DB_URL = "jdbc:sqlite:deliveries.db";

    public static void resetAllTables() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            System.out.println("Clearing all tables...");


            stmt.execute("PRAGMA foreign_keys = OFF;");

            String[] tables = {
                    "delivered_items",
                    "destination_docs",
                    "transports",
                    "products",
                    "sites",
                    "zones",
                    "trucks",
                    "drivers",
                    "shift_assignments",
                    "shift_required_roles",
                    "archived_shifts",
                    "available_shifts",
                    "employee_roles",
                    "archived_employees",
                    "employees",
                    "roles"
            };

            for (String table : tables) {
                try {
                    stmt.execute("DELETE FROM " + table + ";");
                } catch (SQLException e) {
                    System.err.println("Skipping table '" + table + "': " + e.getMessage());
                }
            }


            try {
                stmt.execute("DELETE FROM sqlite_sequence;");
            } catch (SQLException e) {
                System.err.println("Could not reset AUTOINCREMENT counters: " + e.getMessage());
            }

            stmt.execute("PRAGMA foreign_keys = ON;");

            System.out.println("All existing tables cleared and AUTOINCREMENT reset successfully.");

        } catch (SQLException e) {
            System.err.println("Error during database reset: " + e.getMessage());
        }
    }
}
