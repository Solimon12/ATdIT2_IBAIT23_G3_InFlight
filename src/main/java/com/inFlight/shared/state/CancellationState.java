package com.inFlight.shared.state;

/**
 * CancellationState is a singleton class that manages the state of the last cancelled passenger.
 * It provides methods to register a cancellation and retrieve the last cancelled passenger's name.
 */
public class CancellationState {
    private static String lastCancelledPassenger = null; // Default to null
    private static long lastUpdated = 0; // Default to 0

    /**
     * Registers a cancellation by setting the lastCancelledPassenger to the given name
     * and updating the lastUpdated timestamp.
     *
     * @param passengerName The name of the cancelled passenger.
     */
    public static void registerCancellation(String passengerName) {
        lastCancelledPassenger = passengerName;
        lastUpdated = System.currentTimeMillis();
    }

    /**
     * Retrieves the name of the last cancelled passenger.
     *
     * @return The name of the last cancelled passenger, or null if no cancellation has occurred.
     */
    public static String getLastCancelledPassenger() {
        return lastCancelledPassenger;
    }

    /**
     * Retrieves the timestamp of the last cancellation.
     *
     * @return The timestamp of the last cancellation in milliseconds since epoch.
     */
    public static long getLastUpdated() {
        return lastUpdated;
    }
}

