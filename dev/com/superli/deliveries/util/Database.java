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
                // Create roles table

                // Create roles table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS roles (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL UNIQUE
                    );""");

                // Create employees table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employees (
                        id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        bank_account TEXT NOT NULL,
                        salary REAL NOT NULL,
                        site_id INT NOT NULL,
                        employment_terms TEXT NOT NULL,
                        start_date TEXT NOT NULL,
                        FOREIGN KEY (site_id) REFERENCES sites(id)
                    );""");

                // Create employee_roles table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS employee_roles (
                        employee_id TEXT NOT NULL,
                        role_id INTEGER NOT NULL,
                        PRIMARY KEY (employee_id, role_id),
                        FOREIGN KEY (employee_id) REFERENCES employees(id),
                        FOREIGN KEY (role_id) REFERENCES roles(id)
                    );""");

                // Create available_shifts table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS available_shifts (
                        employee_id TEXT NOT NULL,
                        day_of_week TEXT CHECK(day_of_week IN ('SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY')) NOT NULL,
                        shift_type TEXT CHECK(shift_type IN ('MORNING','EVENING')) NOT NULL,
                        PRIMARY KEY (employee_id, day_of_week, shift_type)
                    );""");

                // Create archived_employees table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS archived_employees (
                        employee_id TEXT NOT NULL,
                        name TEXT NOT NULL,
                        bank_account TEXT,
                        salary REAL,
                        employment_terms TEXT,
                        start_date TEXT,
                        site_id INT NOT NULL,
                        archived_date TEXT NOT NULL,
                        FOREIGN KEY (employee_id) REFERENCES employees(id)
                        FOREIGN KEY (site_id) REFERENCES sites(id)
                    );""");


                // Create archived_shifts table
                st.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS archived_shifts (
                            employee_id TEXT NOT NULL,
                            day_of_week TEXT CHECK(day_of_week IN ('SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY')) NOT NULL,
                            shift_type TEXT CHECK(shift_type IN ('MORNING','EVENING')) NOT NULL,
                            date TEXT NOT NULL,
                            role_id INTEGER NOT NULL,
                            PRIMARY KEY (employee_id, day_of_week, shift_type),
                            FOREIGN KEY (employee_id) REFERENCES employees(id),
                            FOREIGN KEY (role_id) REFERENCES roles(id)
                        );""");

                // Create shift_assignments table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS shift_assignments (
                        employee_id TEXT NOT NULL,
                        day_of_week TEXT CHECK(day_of_week IN ('SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY')) NOT NULL,
                        shift_type TEXT CHECK(shift_type IN ('MORNING','EVENING')) NOT NULL,
                        date TEXT NOT NULL,
                        role_id INTEGER NOT NULL,
                        PRIMARY KEY (employee_id, day_of_week, shift_type),
                        FOREIGN KEY (employee_id) REFERENCES employees(id),
                        FOREIGN KEY (role_id) REFERENCES roles(id)
                    );""");

                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS shift_required_roles (
                        day_of_week TEXT CHECK(day_of_week IN ('SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY')) NOT NULL,
                        shift_type TEXT CHECK(shift_type IN ('MORNING','EVENING')) NOT NULL,
                        role_id INTEGER NOT NULL,
                        required_count INTEGER NOT NULL CHECK(required_count >= 1),
                        PRIMARY KEY (day_of_week, shift_type, role_id),
                        FOREIGN KEY (role_id) REFERENCES roles(id)
                    );""");

                // Create driver table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS driver (
                        employee_id TEXT PRIMARY KEY,
                        license_type TEXT CHECK(license_type IN ('A','B','C','D','E')) NOT NULL,
                        FOREIGN KEY (employee_id) REFERENCES employees(id)
                    );""");

                // Create trucks table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS trucks (
                        id TEXT PRIMARY KEY,
                        model TEXT NOT NULL,
                        license_plate TEXT NOT NULL UNIQUE,
                        net_weight REAL NOT NULL,
                        max_weight REAL NOT NULL,
                        license_type TEXT CHECK(license_type IN ('A','B','C','D','E')) NOT NULL,
                        available BOOLEAN NOT NULL DEFAULT true
                    );""");

                // Create zones table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS zones (
                        id TEXT PRIMARY KEY,
                        name TEXT NOT NULL UNIQUE,
                        description TEXT
                    );""");

                // Create sites table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS sites (
                        id TEXT PRIMARY KEY,
                        address TEXT NOT NULL,
                        phone_number TEXT NOT NULL,
                        contact_person_name TEXT NOT NULL,
                        zone_id TEXT,
                        FOREIGN KEY (zone_id) REFERENCES zones(id)
                    );""");

                // Create products table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS products (
                        id TEXT PRIMARY KEY,
                        name TEXT NOT NULL,
                        weight REAL NOT NULL
                    );""");

                // Create transports table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transports (
                        id TEXT PRIMARY KEY,
                        departure_datetime TEXT NOT NULL,
                        truck_id TEXT,
                        driver_id TEXT,
                        origin_site_id TEXT,
                        departure_weight REAL NOT NULL,
                        status TEXT CHECK(status IN ('PLANNED', 'IN_PROGRESS', 'COMPLETED')) NOT NULL,
                        FOREIGN KEY (truck_id) REFERENCES trucks(id),
                        FOREIGN KEY (driver_id) REFERENCES driver(employee_id),
                        FOREIGN KEY (origin_site_id) REFERENCES sites(id)
                    );""");

                // Create destination_docs table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS destination_docs (
                        id TEXT PRIMARY KEY,
                        transport_id TEXT NOT NULL,
                        site_id TEXT NOT NULL,
                        status TEXT CHECK(status IN ('PENDING', 'DELIVERED')) NOT NULL,
                        delivery_date TEXT,
                        FOREIGN KEY (transport_id) REFERENCES transports(id),
                        FOREIGN KEY (site_id) REFERENCES sites(id)
                    );""");

                // Create delivered_items table
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS delivered_items (
                        id TEXT PRIMARY KEY,
                        destination_doc_id TEXT NOT NULL,
                        product_id TEXT NOT NULL,
                        quantity INTEGER NOT NULL,
                        status TEXT CHECK(status IN ('PENDING', 'SHIPPED', 'RECEIVED')) NOT NULL,
                        FOREIGN KEY (destination_doc_id) REFERENCES destination_docs(id),
                        FOREIGN KEY (product_id) REFERENCES products(id)
                    );""");

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
