package com.inFlight.shared.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Photographer is a class that represents a photographer in the application.
 * It contains information about the photographer ID, name, and checkout status.
 * POJO (Plain Old Java Object) class.
 */
public class Photographer {
    private static final Logger logger = LoggerFactory.getLogger(Photographer.class);
    private int id; // ID of the photographer
    private String name; // Name of the photographer
    private boolean checkedOut; // Checkout status of the photographer

    /**
     * Constructor to create a Photographer object with specified ID, name, and checkout status.
     *
     * @param id         the ID of the photographer
     * @param name       the name of the photographer
     * @param checkedOut the checkout status of the photographer
     */
    public Photographer(int id, String name, boolean checkedOut) {
        this.id = id;
        this.name = name;
        this.checkedOut = checkedOut;
    }

    /**
     * Constructor to create a Photographer object with specified name and default ID as -1 and checkout status as false.
     *
     * @param name the name of the photographer
     */
    public Photographer(String name) {
        this(-1, name, false);
    }

    /**getter for id
     * @return the ID of the photographer
     */
    public int getId() {
        return id;
    }

    /**
     * getter for name
     * @return the name of the photographer
     */
    public String getName() {
        return name;
    }

    /**
     * chek if the photographer is checked out
     * @return true if the photographer is checked out, false otherwise
     */
    public boolean isCheckedOut() {
        return checkedOut;
    }

    /**
     * setter for checkedOut
     * @param checkedOut the checkout status of the photographer
     */
    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }

    /**
     * string representation of the photographer
     * @return the name of the photographer
     */
    @Override
    public String toString() {
        return name;
    }
}

