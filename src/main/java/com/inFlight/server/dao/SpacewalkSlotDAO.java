package com.inFlight.server.dao;

import com.inFlight.server.db.SQLiteConnector;
import com.inFlight.shared.model.SpacewalkSlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SpacewalkSlotDAO is a Data Access Object (DAO) class that provides methods to interact with the spacewalk_slots table in the SQLite database.
 * It includes methods to insert, update, delete, and retrieve spacewalk slots.
 */
public class SpacewalkSlotDAO {
    private static final Logger logger = LoggerFactory.getLogger(SpacewalkSlotDAO.class);

    /**
     * Retrieves a list of available spacewalk slots from the database.
     *
     * @return a list of available SpacewalkSlot objects
     */
    public static List<SpacewalkSlot> getAvailableSlots() {
        List<SpacewalkSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM spacewalk_slots WHERE available = 1";

        try (Connection conn = SQLiteConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                slots.add(new SpacewalkSlot(
                        rs.getInt("slot_id"),
                        rs.getString("slot_time"),
                        rs.getInt("available") == 1
                ));
            }

        } catch (SQLException e) {
            logger.error("Failed to fetch available slots", e);
        }

        return slots;
    }

    /**
     * Inserts a new spacewalk slot into the database.
     *
     * @param slot the SpacewalkSlot object to insert
     */
    public void insertSlot(SpacewalkSlot slot) {
        String sql = "INSERT INTO spacewalk_slots (slot_time, available) VALUES (?, ?)";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, slot.getSlotTime());
            stmt.setInt(2, slot.isAvailable() ? 1 : 0);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Failed to save spacewalk slot", e);
        }
    }

    /**
     * Retrieves all spacewalk slots from the database.
     *
     * @return a list of SpacewalkSlot objects
     */
    public List<SpacewalkSlot> getAllSlots() {
        List<SpacewalkSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM spacewalk_slots";

        try (Connection conn = SQLiteConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                slots.add(new SpacewalkSlot(
                        rs.getInt("slot_id"),
                        rs.getString("slot_time"),
                        rs.getInt("available") == 1
                ));
            }

        } catch (SQLException e) {
            logger.error("Failed to fetch all slots", e);
        }

        return slots;
    }

    /**
     * Retrieves a spacewalk slot by its ID from the database.
     *
     * @param slotId the ID of the spacewalk slot to retrieve
     * @return the SpacewalkSlot object if found, null otherwise
     */
    public SpacewalkSlot getSlotById(int slotId) {
        String sql = "SELECT * FROM spacewalk_slots WHERE slot_id = ?";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, slotId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new SpacewalkSlot(
                        rs.getInt("slot_id"),
                        rs.getString("slot_time"),
                        rs.getInt("available") == 1
                );
            }

        } catch (SQLException e) {
            logger.error("Failed to fetch slot", e);
        }

        return null;
    }

    /**
     * Updates the availability of a spacewalk slot in the database.
     *
     * @param slotId    the ID of the spacewalk slot to update
     * @param available  the new availability status
     */
    public void setAvailability(int slotId, boolean available) {
        String sql = "UPDATE spacewalk_slots SET available = ? WHERE slot_id = ?";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, available ? 1 : 0);
            stmt.setInt(2, slotId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Failed to update slot availability", e);
        }
    }

    /**
     * Checks if a spacewalk slot is available.
     *
     * @param slotId the ID of the spacewalk slot to check
     * @return true if the slot is available, false otherwise
     */
    public boolean isSlotAvailable(int slotId) {
        try (Connection conn = SQLiteConnector.getConnection()) {
            String sql = "SELECT available FROM spacewalk_slots WHERE slot_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, slotId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int value = rs.getInt("available");
                return value == 1;
            }
        } catch (SQLException e) {
            logger.error("Failed to check slot availability", e);
        }
        return false;
    }

    /**
     * Deletes a spacewalk slot from the database.
     *
     * @param slotId the ID of the spacewalk slot to delete
     */
    public void deleteSlot(int slotId) {
        try (Connection conn = SQLiteConnector.getConnection()) {
            String sql = "DELETE FROM spacewalk_slots WHERE slot_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, slotId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to delete spacewalk slot", e);
        }
    }
}
