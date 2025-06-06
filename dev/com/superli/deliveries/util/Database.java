package com.superli.deliveries.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class);
    private static final String DB_URL = "jdbc:sqlite:stocks.db";
    private static Connection conn;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            log.info("Connected to SQLite at {}", DB_URL);

            try (Statement st = conn.createStatement()) {
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS Employee (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL,
                            id_number TEXT UNIQUE NOT NULL,
                            bank_account TEXT NOT NULL,
                            salary REAL NOT NULL,
                            employment_terms TEXT NOT NULL,
                            start_date TEXT NOT NULL
                        );""");

                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS Role (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL UNIQUE
                        );""");

                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS QualifiedFor (
                            employee_id INTEGER NOT NULL,
                            role_id INTEGER NOT NULL,
                            PRIMARY KEY (employee_id, role_id),
                            FOREIGN KEY (employee_id) REFERENCES Employee(id),
                            FOREIGN KEY (role_id) REFERENCES Role(id)
                        );""");

                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS Availability (
                            employee_id INTEGER NOT NULL,
                            day_of_week TEXT CHECK(day_of_week IN ('SUNDAY','MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY')) NOT NULL,
                            shift_type TEXT CHECK(shift_type IN ('MORNING', 'EVENING')) NOT NULL,
                            PRIMARY KEY (employee_id, day_of_week, shift_type),
                            FOREIGN KEY (employee_id) REFERENCES Employee(id)
                        );""");

                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS Shift (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            date TEXT NOT NULL,
                            type TEXT CHECK(type IN ('MORNING', 'EVENING')) NOT NULL
                        );""");

                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS AssignedEmployee (
                            shift_id INTEGER NOT NULL,
                            employee_id INTEGER NOT NULL,
                            role_id INTEGER NOT NULL,
                            is_shift_manager INTEGER DEFAULT 0 CHECK(is_shift_manager IN (0,1)),
                            PRIMARY KEY (shift_id, employee_id),
                            FOREIGN KEY (shift_id) REFERENCES Shift(id),
                            FOREIGN KEY (employee_id) REFERENCES Employee(id),
                            FOREIGN KEY (role_id) REFERENCES Role(id)
                        );""");

                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS Zone (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL UNIQUE
                        );""");

                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS Product (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL
                        );""");

                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS Site (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            address TEXT NOT NULL,
                            contact TEXT NOT NULL,
                            zone_id INTEGER,
                            FOREIGN KEY (zone_id) REFERENCES Zone(id)
                        );""");

                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS Driver (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL,
                            license TEXT NOT NULL
                        );""");

                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS Truck (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            model TEXT NOT NULL,
                            license_plate TEXT NOT NULL
                        );""");

                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS Transport (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            date TEXT NOT NULL,
                            driver_id INTEGER,
                            truck_id INTEGER,
                            FOREIGN KEY (driver_id) REFERENCES Driver(id),
                            FOREIGN KEY (truck_id) REFERENCES Truck(id)
                        );""");

                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS DestinationDoc (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            transport_id INTEGER NOT NULL,
                            site_id INTEGER NOT NULL,
                            status TEXT NOT NULL,
                            FOREIGN KEY (transport_id) REFERENCES Transport(id),
                            FOREIGN KEY (site_id) REFERENCES Site(id)
                        );""");

                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS DeliveryItem (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            destination_doc_id INTEGER NOT NULL,
                            product_id INTEGER NOT NULL,
                            quantity INTEGER NOT NULL,
                            FOREIGN KEY (destination_doc_id) REFERENCES DestinationDoc(id),
                            FOREIGN KEY (product_id) REFERENCES Product(id)
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