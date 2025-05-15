package com.inFlight.server.dao;

import com.inFlight.shared.model.Photographer;
import com.inFlight.server.db.SQLiteConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * PhotographerDAO is a Data Access Object (DAO) class that provides methods to interact with the photographer table in the SQLite database.
 * It includes methods to insert, update, and retrieve photographer records.
 */
public class PhotographerDAO {
    private static final Logger logger = LoggerFactory.getLogger(PhotographerDAO.class);

    /**
     * Inserts a new photographer into the database.
     *
     * @param photographer the Photographer object to insert
     */
    public void insertPhotographer(Photographer photographer) {
        String sql = "INSERT INTO photographer (name, checked_out) VALUES (?, ?)";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, photographer.getName());
            stmt.setInt(2, photographer.isCheckedOut() ? 1 : 0);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Failed to insert photographer", e);
        }
    }

    /**
     * Retrieves a photographer by their name from the database.
     *
     * @param name the name of the photographer to retrieve
     * @return the Photographer object if found, null otherwise
     */
    public Photographer getPhotographerByName(String name) {
        String sql = "SELECT * FROM photographer WHERE name = ?";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Photographer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("checked_out") == 1
                );
            }

        } catch (SQLException e) {
            logger.error("Failed to fetch photographer by name", e);
        }

        return null;
    }

    /**
     * Retrieves a photographer by their ID from the database.
     *
     * @param id the ID of the photographer to retrieve
     * @return the Photographer object if found, null otherwise
     */
    public Photographer getPhotographerById(int id) {
        String sql = "SELECT * FROM photographer WHERE id = ?";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Photographer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("checked_out") == 1
                );
            }

        } catch (SQLException e) {
            logger.error("Failed to fetch photographer by ID", e);
        }

        return null;
    }

    /**
     * Updates the check-out status of a photographer in the database.
     *
     * @param id         the ID of the photographer to update
     * @param checkedOut the new check-out status
     */
    public void setCheckedOutStatus(int id, boolean checkedOut) {
        String sql = "UPDATE photographer SET checked_out = ? WHERE id = ?";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, checkedOut ? 1 : 0);
            stmt.setInt(2, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Failed to update photographer check-out status", e);
        }
    }
}
