package com.inFlight.shared.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Booking is a class that represents a booking made by a passenger.
 * It contains information about the booking ID, passenger ID, slot ID, tier, and status.
 * POJO (Plain Old Java Object) class.
 */
public class Booking {
    private static final Logger logger = LoggerFactory.getLogger(Booking.class);
    private int bookingId; // ID of the booking
    private int passengerId; // ID of the passenger
    private int slotId; // ID of the slot
    private int tier; // Tier of the booking
    private String status; // Status of the booking

    /**
     * Constructor to create a Booking object with specified booking ID, passenger ID, slot ID, tier, and status.
     *
     * @param bookingId   the ID of the booking
     * @param passengerId the ID of the passenger
     * @param slotId      the ID of the slot
     * @param tier        the tier of the booking
     * @param status      the status of the booking
     */
    public Booking(int bookingId, int passengerId, int slotId, int tier, String status) {
        this.bookingId = bookingId;
        this.passengerId = passengerId;
        this.slotId = slotId;
        this.tier = tier;
        this.status = status;
    }

    /**
     * getter for bookingId
     * @return the ID of the booking
     */
    public int getBookingId() {
        return bookingId;
    }

    /**
     * setter for bookingId
     * @param bookingId the ID of the booking
     */
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
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
     * getter for slotId
     * @return the ID of the slot
     */
    public int getSlotId() {
        return slotId;
    }

    /**
     * setter for slotId
     * @param slotId the ID of the slot
     */
    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    /**
     * getter for tier
     * @return the tier of the booking
     */
    public int getTier() {
        return tier;
    }

    /**
     * setter for tier
     * @param tier the tier of the booking
     */
    public void setTier(int tier) {
        this.tier = tier;
    }

    /**
     * getter for status
     * @return the status of the booking
     */
    public String getStatus() {
        return status;
    }

    /**
     * setter for status
     * @param status the status of the booking
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * toString method to return a string representation of the Booking object.
     *
     * @return a string representation of the Booking object
     */
    @Override
    public String toString() {
        return "Booking{" +
                "id=" + bookingId +
                ", passengerId=" + passengerId +
                ", slotId=" + slotId +
                ", tier=" + tier +
                ", status='" + status + '\'' +
                '}';
    }
}
