package com.inFlight.shared.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SpacewalkSlot is a class that represents a spacewalk slot in the application.
 * It contains information about the slot ID, slot time, and availability status.
 * POJO (Plain Old Java Object) class.
 */
public class SpacewalkSlot {
    private static final Logger logger = LoggerFactory.getLogger(SpacewalkSlot.class);
    private int slotId; // ID of the slot
    private String slotTime; // Time of the slot
    private boolean available; // Availability status of the slot

    /**
     * Constructor to create a SpacewalkSlot object with specified slot ID, slot time, and availability status.
     *
     * @param slotId    the ID of the slot
     * @param slotTime  the time of the slot
     * @param available  the availability status of the slot
     */
    public SpacewalkSlot(int slotId, String slotTime, boolean available) {
        this.slotId = slotId;
        this.slotTime = slotTime;
        this.available = available;
    }

    /**
     * Constructor to create a SpacewalkSlot object with specified slot time and default availability status as true.
     *
     * @param slotTime  the time of the slot
     */
    public SpacewalkSlot(String slotTime) {
        this(-1, slotTime, true);
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
     * getter for slotTime
     * @return the time of the slot
     */
    public String getSlotTime() {
        return slotTime;
    }

    /**
     * setter for slotTime
     * @param slotTime the time of the slot
     */
    public void setSlotTime(String slotTime) {
        this.slotTime = slotTime;
    }

    /**
     * getter for available
     * @return the availability status of the slot
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * setter for available
     * @param available the availability status of the slot
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * toString method to return a string representation of the SpacewalkSlot object.
     *
     * @return a string representation of the SpacewalkSlot object
     */
    @Override
    public String toString() {
        return "SpacewalkSlot{" +
                "slotId=" + slotId +
                ", slotTime='" + slotTime + '\'' +
                ", available=" + available +
                '}';
    }


}

