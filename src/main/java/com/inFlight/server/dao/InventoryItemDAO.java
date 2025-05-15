package com.inFlight.server.dao;

import com.inFlight.shared.model.InventoryItem;
import com.inFlight.server.db.SQLiteConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * InventoryItemDAO is a Data Access Object (DAO) class that provides methods to interact with the inventory_item table in the SQLite database.
 * It includes methods to insert, update, and retrieve inventory items.
 */
public class InventoryItemDAO {
    private static final Logger logger = LoggerFactory.getLogger(InventoryItemDAO.class);

    /**
     * Retrieves an inventory item by its ID from the database.
     *
     * @param item object of the inventory item to retrieve
     * @return the InventoryItem object if found, null otherwise
     */
    public void insertItem(InventoryItem item) {
        String sql = "INSERT INTO inventory_item (name, condition, available, owner_role, checked_out) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getName());
            stmt.setString(2, item.getCondition());
            stmt.setInt(3, item.isAvailable() ? 1 : 0);
            stmt.setString(4, item.getOwnerRole());
            stmt.setInt(5, item.isCheckedOut() ? 1 : 0);

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Failed to insert inventory item", e);
        }
    }

    /**
     * Retrieves an inventory item by its ID from the database.
     * @param ownerRole the role of the owner of the item
     * @return the InventoryItem object if found, null otherwise
     */
    public List<InventoryItem> getItemsByRole(String ownerRole) {
        List<InventoryItem> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory_item WHERE owner_role = ?";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ownerRole);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                items.add(new InventoryItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("condition"),
                        rs.getInt("available") == 1,
                        rs.getString("owner_role"),
                        rs.getInt("checked_out") == 1
                ));
            }

        } catch (SQLException e) {
            logger.error("Failed to fetch inventory items", e);
        }

        return items;
    }

    /**
     * Retrieves an inventory item by its ID from the database.
     *
     * @param item the InventoryItem object to retrieve
     */
    public void updateItem(InventoryItem item) {
        String sql = "UPDATE inventory_item SET condition = ?, available = ?, checked_out = ? WHERE id = ?";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getCondition());
            stmt.setInt(2, item.isAvailable() ? 1 : 0);
            stmt.setInt(3, item.isCheckedOut() ? 1 : 0);
            stmt.setInt(4, item.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Failed to update inventory item", e);
        }
    }
}
