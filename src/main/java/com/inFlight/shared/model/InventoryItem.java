package com.inFlight.shared.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InventoryItem is a class that represents an item in the inventory.
 * It contains information about the item ID, name, condition, availability, owner role, and checkout status.
 * POJO (Plain Old Java Object) class.
 */
public class InventoryItem {
    private static final Logger logger = LoggerFactory.getLogger(InventoryItem.class);

    private int id; // ID of the item
    private String name; // Name of the item
    private String condition; // Condition of the item
    private boolean available; // Availability status of the item
    private String ownerRole; // Role of the owner ("Photographer" or "Attendant")
    private boolean checkedOut; // Checkout status of the item

    /**
     * Constructor to create an InventoryItem object with specified ID, name, condition, availability status, owner role, and checkout status.
     *
     * @param id         the ID of the item
     * @param name       the name of the item
     * @param condition  the condition of the item
     * @param available  the availability status of the item
     * @param ownerRole  the role of the owner ("Photographer" or "Attendant")
     * @param checkedOut the checkout status of the item
     */
    public InventoryItem(int id, String name, String condition, boolean available, String ownerRole, boolean checkedOut) {
        this.id = id;
        this.name = name;
        this.condition = condition;
        this.available = available;
        this.ownerRole = ownerRole;
        this.checkedOut = checkedOut;
    }


    /**
     * Constructor to create an InventoryItem object with specified name, condition, availability status, and owner role.
     * The ID is set to -1 and the checkout status is set to false by default.
     *
     * @param name      the name of the item
     * @param condition the condition of the item
     * @param available the availability status of the item
     * @param ownerRole the role of the owner ("Photographer" or "Attendant")
     */
    public InventoryItem(String name, String condition, boolean available, String ownerRole) {
        this(-1, name, condition, available, ownerRole, false);
    }

    /**
     * getter for id
     * @return the ID of the item
     */
    public int getId() {
        return id;
    }

    /**
     * getter for name
     * @return the name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * getter for condition
     * @return the condition of the item
     */
    public String getCondition() {
        return condition;
    }

    /**
     * getter for available
     * @return the availability status of the item
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * getter for ownerRole
     * @return the role of the owner ("Photographer" or "Attendant")
     */
    public String getOwnerRole() {
        return ownerRole;
    }

    /**
     * setter for id
     * @param id the ID of the item
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * setter for name
     * @param name the name of the item
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * setter for condition
     * @param condition the condition of the item
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * setter for available
     * @param available the availability status of the item
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * setter for ownerRole
     * @param ownerRole the role of the owner ("Photographer" or "Attendant")
     */
    public void setOwnerRole(String ownerRole) {
        this.ownerRole = ownerRole;
    }

    /**
     * getter for checkedOut
     * @return the checkout status of the item
     */
    public boolean isCheckedOut() {
        return checkedOut;
    }

    /**
     * setter for checkedOut
     * @param checkedOut the checkout status of the item
     */
    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }

    /**
     * toString method to return a string representation of the InventoryItem object.
     *
     * @return a string representation of the InventoryItem object
     */
    @Override
    public String toString() {
        return name;
    }
}
