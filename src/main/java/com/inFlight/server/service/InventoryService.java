package com.inFlight.server.service;

import com.inFlight.server.dao.InventoryItemDAO;
import com.inFlight.shared.model.InventoryItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * InventoryService is a service class that handles inventory-related operations.
 * It interacts with the InventoryItemDAO to manage inventory items.
 */
public class InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryItemDAO dao; // DAO for inventory item operations

    /**
     * Default constructor initializes the DAO.
     */
    public InventoryService () {
        this.dao = new InventoryItemDAO();
    }

    /**
     * Constructor for dependency injection
     */
    public InventoryService (InventoryItemDAO dao) {
        this.dao = dao;
    }

    /**
     * Adds an inventory item to the database.
     *
     * @param name      the name of the inventory item
     * @param condition the condition of the inventory item
     * @param available whether the item is available
     * @param ownerRole the role of the owner
     */
    public void addItem(String name, String condition, boolean available, String ownerRole) {
        dao.insertItem(new InventoryItem(name, condition, available, ownerRole));
    }

    /**
     * Retrieves a list of inventory items by their owner role.
     *
     * @param role the role of the owner
     * @return a list of InventoryItem objects associated with the specified owner role
     */
    public List<InventoryItem> getItemsByRole(String role) {
        return dao.getItemsByRole(role);
    }

    /**
     * updates an inventory item in the database.
     * @param item the InventoryItem object to update
     */
    public void updateItem(InventoryItem item) {
        dao.updateItem(item);
    }
}
