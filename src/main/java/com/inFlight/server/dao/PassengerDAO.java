package com.inFlight.server.dao;

import com.inFlight.server.db.SQLiteConnector;
import com.inFlight.shared.model.Passenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PassengerDAO is a Data Access Object (DAO) class that provides methods to interact with the passengers table in the SQLite database.
 * It includes methods to insert, update, and retrieve passenger records.
 */
public class PassengerDAO {
    private static final Logger logger = LoggerFactory.getLogger(PassengerDAO.class);

    /**
     * Inserts a new passenger into the database.
     *
     * @param passenger the Passenger object to insert
     */
    public void insertPassenger(Passenger passenger) {
        String sql = "INSERT INTO passengers (username, password, novaCredits) VALUES (?, ?, ?)";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, passenger.getUsername());
            stmt.setString(2, passenger.getPassword());
            stmt.setInt(3, passenger.getNovaCredits());

            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to save passenger", e);
        }
    }

    /**
     * Retrieves a passenger by their username from the database.
     *
     * @param username the username of the passenger to retrieve
     * @return the Passenger object if found, null otherwise
     */
    public Passenger getPassengerByUsername(String username) {
        String sql = "SELECT * FROM passengers WHERE username = ?";
        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Passenger(
                        rs.getInt("passenger_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("novaCredits"),
                        rs.getInt("checked_out") == 1
                );
            }

        } catch (SQLException e) {
          logger.error("Failed to fetch passenger by Username", e);
        }
        return null;
    }

    /**
     * Retrieves a passenger by their ID from the database.
     *
     * @param id the ID of the passenger to retrieve
     * @return the Passenger object if found, null otherwise
     */
    public Passenger getPassengerById(int id) {
        String sql = "SELECT * FROM passengers WHERE passenger_id = ?";
        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Passenger(
                        rs.getInt("passenger_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("novaCredits"),
                        rs.getInt("checked_out") == 1
                );
            }

        } catch (SQLException e) {
            logger.error("Failed to fetch passenger by ID", e);
        }
        return null;
    }

    /**
     * Retrieves all passengers from the database.
     *
     * @return a list of Passenger objects
     */
    public List<Passenger> getAllPassengers() {
        List<Passenger> passengers = new ArrayList<>();
        String sql = "SELECT * FROM passengers";

        try (Connection conn = SQLiteConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                passengers.add(new Passenger(
                        rs.getInt("passenger_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("novaCredits"),
                        rs.getInt("checked_out") == 1
                ));
            }

        } catch (SQLException e) {
            logger.error("Failed to fetch all passengers", e);
        }
        return passengers;
    }

    /**
     * Updates the Nova credits of a passenger in the database.
     *
     * @param passengerId
     * @param newCredits
     */
    public void updateNovaCredits(int passengerId, int newCredits) {
        String sql = "UPDATE passengers SET novaCredits = ? WHERE passenger_id = ?";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newCredits);
            stmt.setInt(2, passengerId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Failed to update Nova credits", e);
        }
    }

    /**
     * Updates the checked_out status of a passenger in the database.
     *
     * @param passengerId the ID of the passenger to update
     * @param status      the new checked_out status to set
     */
    public void setCheckedOut(int passengerId, boolean status) {
        try (Connection conn = SQLiteConnector.getConnection()) {
            String sql = "UPDATE passengers SET checked_out = ? WHERE passenger_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, status ? 1 : 0);
            stmt.setInt(2, passengerId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to update checked_out status", e);
        }
    }
}