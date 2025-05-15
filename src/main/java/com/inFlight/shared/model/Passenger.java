package com.inFlight.shared.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Passenger is a class that represents a passenger in the application.
 * It contains information about the passenger ID, username, password, nova credits, and checkout status.
 * POJO (Plain Old Java Object) class.
 */
public class Passenger {
    private static final Logger logger = LoggerFactory.getLogger(Passenger.class);
    private int passengerId; // ID of the passenger
    private String username; // Username of the passenger
    private String password; // Password of the passenger
    private int novaCredits; // Nova credits (in Application currency) of the passenger
    private boolean checkedOut; // Checkout status of the passenger

    public Passenger(int passengerId, String username, String password, int novaCredits) {
        this.passengerId = passengerId; // ID of the passenger
        this.username = username; // Username of the passenger
        this.password = password; // Password of the passenger
        this.novaCredits = novaCredits; // Nova credits (in Application currency) of the passenger
        this.checkedOut = false; // Checkout status of the passenger, false by default
    }

    /**
     * Constructor to create a Passenger object with specified passenger ID, username, password, nova credits, and checkout status.
     *
     * @param passengerId the ID of the passenger
     * @param username    the username of the passenger
     * @param password    the password of the passenger
     * @param novaCredits the nova credits (in Application currency) of the passenger
     * @param checkedOut  the checkout status of the passenger
     */
    public Passenger(int passengerId, String username, String password, int novaCredits, boolean checkedOut) {
        this.passengerId = passengerId;
        this.username = username;
        this.password = password;
        this.novaCredits = novaCredits;
        this.checkedOut = checkedOut;
    }

    /**
     * Constructor to create a Passenger object with specified username, password, and nova credits.
     * The passenger ID is set to -1 and the checkout status is set to false by default.
     *
     * @param username    the username of the passenger
     * @param password    the password of the passenger
     * @param novaCredits the nova credits (in Application currency) of the passenger
     */
    public Passenger(String username, String password, int novaCredits) {
        this(-1, username, password, novaCredits);
    }

    /**
     * getter for passengerId
     * @return the ID of the passenger
     */
    public int getPassengerId() {
        return passengerId;
    }

    /**
     * setter for passengerId
     * @param passengerId the ID of the passenger
     */
    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    /**
     * getter for username
     * @return the username of the passenger
     */
    public String getUsername() {
        return username;
    }

    /**
     * setter for username
     * @param username the username of the passenger
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * getter for password
     * @return the password of the passenger
     */
    public String getPassword() {
        return password;
    }

    /**
     * setter for password
     * @param password the password of the passenger
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * getter for novaCredits
     * @return the nova credits (in Application currency) of the passenger
     */
    public int getNovaCredits() {
        return novaCredits;
    }

    /**
     * setter for novaCredits
     * @param novaCredits the nova credits (in Application currency) of the passenger
     */
    public void setNovaCredits(int novaCredits) {
        this.novaCredits = novaCredits;
    }

    /**
     * getter for checkedOut
     * @return the checkout status of the passenger
     */
    public boolean isCheckedOut() {
        return checkedOut;
    }

    /**
     * setter for checkedOut
     * @param checkedOut the checkout status of the passenger
     */
    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }

    /**
     * toString method to return a string representation of the Passenger object.
     *
     * @return a string representation of the Passenger object
     */
    @Override
    public String toString() {
        return "Passenger{" +
                "id=" + passengerId +
                ", username='" + username + '\'' +
                ", novaCredits=" + novaCredits +
                '}';
    }


}

