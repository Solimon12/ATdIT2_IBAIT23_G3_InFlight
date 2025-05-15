package com.inFlight.shared.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BroadcastStateTest {

    @Test
    void testSetMessageUpdatesMessageAndTimestamp() {
        String testMessage = "System reboot scheduled";

        // Capture time before setting
        long before = System.currentTimeMillis();
        BroadcastState.setMessage(testMessage);
        long after = System.currentTimeMillis();

        assertEquals(testMessage, BroadcastState.getMessage(), "The message should match what was set.");

        long lastUpdated = BroadcastState.getLastUpdated();
        assertTrue(lastUpdated >= before && lastUpdated <= after,
                "lastUpdated timestamp should be within the range of before and after");
    }

    @Test
    void testSetMessageOverridesPreviousMessage() throws InterruptedException {
        BroadcastState.setMessage("First message");
        Thread.sleep(5); // Ensure time difference
        BroadcastState.setMessage("Second message");

        assertEquals("Second message", BroadcastState.getMessage(), "Should return the most recently set message.");
    }

    @Test
    void testEmptyMessageCanBeSet() {
        BroadcastState.setMessage("");
        assertEquals("", BroadcastState.getMessage(), "Empty string should be set and returned.");
    }

    @Test
    void testNullMessageCanBeSet() {
        BroadcastState.setMessage(null);
        assertNull(BroadcastState.getMessage(), "Message should be null after setting null.");
    }
}