package Inventory.Tests;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;

public class DatabaseConnectionTest {
    private static final String INVENTORY_DB_URL = "jdbc:sqlite:Inventory.db";
    private static final String SUPPLIERS_DB_URL = "jdbc:sqlite:suppliers.db";

    public static void main(String[] args) {
        System.out.println("Testing database connections...\n");

        // Test Inventory DB Connection
        try (Connection inventoryConn = DriverManager.getConnection(INVENTORY_DB_URL)) {
            System.out.println("✅ Successfully connected to Inventory.db");
            printDatabaseInfo("Inventory.db", inventoryConn);
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to Inventory.db: " + e.getMessage());
        }

        System.out.println("\n" + "=".repeat(50) + "\n");

        // Test Suppliers DB Connection
        try (Connection suppliersConn = DriverManager.getConnection(SUPPLIERS_DB_URL)) {
            System.out.println("✅ Successfully connected to suppliers.db");
            printDatabaseInfo("suppliers.db", suppliersConn);
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to suppliers.db: " + e.getMessage());
        }
    }

    private static void printDatabaseInfo(String dbName, Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        
        // Print tables
        System.out.println("\nTables in " + dbName + ":");
        System.out.println("-".repeat(30));
        
        try (ResultSet tables = metaData.getTables(null, null, "%", new String[] {"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("📋 " + tableName);
                
                // Print column info for each table
                try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                    System.out.println("   Columns:");
                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        String columnType = columns.getString("TYPE_NAME");
                        System.out.printf("   - %-20s (%s)\n", columnName, columnType);
                    }
                }
                System.out.println();
            }
        }
    }
}
