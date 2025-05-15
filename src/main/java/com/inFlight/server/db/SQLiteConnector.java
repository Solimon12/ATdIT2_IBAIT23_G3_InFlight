package com.inFlight.server.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * SQLiteConnector is a utility class that provides a method to establish a connection to the SQLite database.
 * It uses the JDBC driver to connect to the database located at the specified path.
 */
public class SQLiteConnector {
    private static final Logger logger = LoggerFactory.getLogger(SQLiteConnector.class); // Logger for logging messages

    private static final String DB_PATH = "src/main/resources/inFlightDB.db"; // Path to the SQLite database file
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH; // JDBC URL for SQLite database

    /**
     * Establishes a connection to the SQLite database.
     *
     * @return a Connection object representing the connection to the database.
     * @throws SQLException if there is an error establishing the connection.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}

