package com.inFlight.shared.state;

/**
 * BraodcastState is a singleton class that manages the state of the latest broadcast message.
 * It provides methods to set and get the latest message, as well as the last updated timestamp.
 */
public class BroadcastState {
    private static String latestMessage = null; // Default to null
    private static long lastUpdated = 0; // Default to 0

    /**
     * Sets the latest broadcast message and updates the last updated timestamp.
     *
     * @param msg The latest broadcast message.
     */
    public static void setMessage(String msg) {
        latestMessage = msg;
        lastUpdated = System.currentTimeMillis();
    }

    /**
     * Retrieves the latest broadcast message.
     *
     * @return The latest broadcast message, or null if no message has been set.
     */
    public static String getMessage() {
        return latestMessage;
    }

    /**
     * Retrieves the timestamp of the last broadcast message.
     *
     * @return The timestamp of the last broadcast message in milliseconds since epoch.
     */
    public static long getLastUpdated() {
        return lastUpdated;
    }
}


