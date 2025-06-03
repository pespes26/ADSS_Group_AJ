package Inventory.Tests;

import Inventory.DataBase.DatabaseConnector;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseTestRunner {
    public static void main(String[] args) {
        testInventoryDB();
        System.out.println("\n" + "=".repeat(50) + "\n");
        testSuppliersDB();
    }

    private static void testInventoryDB() {
        System.out.println("Testing Inventory.db connection:");
        try (Connection conn = DatabaseConnector.connect()) {
            System.out.println("✅ Successfully connected to Inventory.db");
            printDatabaseInfo(conn);
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to Inventory.db: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testSuppliersDB() {
        System.out.println("Testing suppliers.db connection:");
        try (Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:suppliers.db")) {
            System.out.println("✅ Successfully connected to suppliers.db");
            printDatabaseInfo(conn);
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to suppliers.db: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printDatabaseInfo(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        
        // Print database version info
        System.out.println("\nDatabase Information:");
        System.out.println("-".repeat(30));
        System.out.println("Database: " + metaData.getDatabaseProductName());
        System.out.println("Version: " + metaData.getDatabaseProductVersion());
        
        // Print tables
        System.out.println("\nDatabase Tables:");
        System.out.println("-".repeat(30));
        
        try (ResultSet tables = metaData.getTables(null, null, "%", new String[] {"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("\n📋 Table: " + tableName);
                
                // Print column info for each table
                try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        String columnType = columns.getString("TYPE_NAME");
                        String isNullable = columns.getString("IS_NULLABLE");
                        System.out.printf("   %-25s %-10s %s\n", 
                            columnName, 
                            columnType, 
                            isNullable.equals("YES") ? "nullable" : "not null"
                        );
                    }
                }
            }
        }
    }
}
