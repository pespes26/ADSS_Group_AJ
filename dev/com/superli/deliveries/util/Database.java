package com.superli.deliveries.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class);
    private static final String DB_URL = "jdbc:sqlite:deliveries.db";
    private static Connection conn;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            log.info("Connected to SQLite at {}", DB_URL);

            try (Statement st = conn.createStatement()) {
                // Create zones table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS zones (
                        zone_id TEXT PRIMARY KEY,
                        name TEXT NOT NULL UNIQUE
                    );""");

                // Create sites table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS sites (
                        site_id TEXT PRIMARY KEY,
                        address TEXT NOT NULL,
                        phone_number TEXT,
                        contact_person_name TEXT,
                        zone_id TEXT NOT NULL,
                        FOREIGN KEY (zone_id) REFERENCES zones(zone_id)
                    );""");

                // Create products table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS products (
                        product_id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        weight REAL NOT NULL CHECK(weight >= 0)
                    );""");

                // Create trucks table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS trucks (
                        plate_num TEXT PRIMARY KEY,
                        model TEXT NOT NULL,
                        net_weight REAL NOT NULL CHECK(net_weight > 0),
                        max_weight REAL NOT NULL CHECK(max_weight >= net_weight),
                        license_type TEXT NOT NULL CHECK(license_type IN ('B','C','C1','C2','E')),
                        available BOOLEAN NOT NULL DEFAULT true
                    );""");

                // Create employees table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employees (
                        id TEXT PRIMARY KEY,
                        full_name TEXT NOT NULL,
                        bank_account TEXT NOT NULL,
                        salary REAL NOT NULL,
                        site_id INTEGER NOT NULL,
                        employee_terms TEXT NOT NULL,
                        employee_start_date DATE NOT NULL
                    );""");

                // Create drivers table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS drivers (
                        id TEXT PRIMARY KEY,
                        license_type TEXT NOT NULL CHECK(license_type IN ('B','C','C1','C2','E')),
                        available BOOLEAN NOT NULL DEFAULT true,
                        FOREIGN KEY (id) REFERENCES employees(id) ON DELETE CASCADE
                    );""");

                // Create employee_roles table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employee_roles (
                        employee_id TEXT NOT NULL,
                        role_name TEXT NOT NULL,
                        PRIMARY KEY (employee_id, role_name),
                        FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
                    );""");

                // Create available_shifts table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS available_shifts (
                        employee_id TEXT NOT NULL,
                        day_of_week TEXT NOT NULL CHECK(day_of_week IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
                        shift_type TEXT NOT NULL CHECK(shift_type IN ('MORNING','AFTERNOON','NIGHT')),
                        PRIMARY KEY (employee_id, day_of_week, shift_type),
                        FOREIGN KEY (employee_id) REFERENCES employees(id) ON DELETE CASCADE
                    );""");

                // Drop and recreate transports table
                st.executeUpdate("DROP TABLE IF EXISTS transports;");
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transports (
                        transport_id TEXT PRIMARY KEY,
                        departure_datetime TEXT NOT NULL,
                        truck_plate_num TEXT NOT NULL,
                        driver_id TEXT NOT NULL,
                        origin_site_id TEXT NOT NULL,
                        departure_weight REAL NOT NULL CHECK(departure_weight >= 0),
                        status TEXT NOT NULL CHECK(status IN ('PLANNED', 'DISPATCHED', 'COMPLETED', 'CANCELLED', 'SELFDELIVERY')),
                        FOREIGN KEY (truck_plate_num) REFERENCES trucks(plate_num),
                        FOREIGN KEY (driver_id) REFERENCES drivers(driver_id),
                        FOREIGN KEY (origin_site_id) REFERENCES sites(site_id)
                    );""");

                // Create destination_docs table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS destination_docs (
                        destination_doc_id TEXT PRIMARY KEY,
                        transport_id TEXT NOT NULL,
                        destination_site_id TEXT NOT NULL,
                        status TEXT NOT NULL CHECK(status IN ('PENDING','IN_DELIVERY','DELIVERED','CANCELLED')),
                        FOREIGN KEY (transport_id) REFERENCES transports(transport_id),
                        FOREIGN KEY (destination_site_id) REFERENCES sites(site_id)
                    );""");

                // Drop and recreate delivered_items table
                st.executeUpdate("DROP TABLE IF EXISTS delivered_items;");
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS delivered_items (
                        item_id TEXT PRIMARY KEY,
                        destination_doc_id TEXT NOT NULL,
                        product_id TEXT NOT NULL,
                        quantity INTEGER NOT NULL CHECK(quantity >= 0),
                        FOREIGN KEY (destination_doc_id) REFERENCES destination_docs(destination_doc_id),
                        FOREIGN KEY (product_id) REFERENCES products(product_id)
                    );""");

                // Create archived_transports table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS archived_transports (
                        transport_id TEXT PRIMARY KEY,
                        archived_datetime DATETIME NOT NULL,
                        final_status TEXT NOT NULL,
                        FOREIGN KEY (transport_id) REFERENCES transports(transport_id)
                    );""");

                // Create archive_notes table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS archive_notes (
                        transport_id TEXT NOT NULL,
                        note TEXT NOT NULL,
                        PRIMARY KEY (transport_id, note),
                        FOREIGN KEY (transport_id) REFERENCES archived_transports(transport_id)
                    );""");

                // Create indexes
                st.execute("CREATE INDEX IF NOT EXISTS idx_employees_id ON employees(id)");
                st.execute("CREATE INDEX IF NOT EXISTS idx_drivers_id ON drivers(id)");
                st.execute("CREATE INDEX IF NOT EXISTS idx_drivers_license_type ON drivers(license_type)");
                st.execute("CREATE INDEX IF NOT EXISTS idx_drivers_available ON drivers(available)");
                st.execute("CREATE INDEX IF NOT EXISTS idx_employee_roles_employee_id ON employee_roles(employee_id)");

                log.info("Ensured database schema exists");
            }
        } catch (Exception e) {
            log.error("Database initialization failed", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        return conn;
    }
}
