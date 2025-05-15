package com.inFlight.shared.state;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CancellationStateTest {

    @Test
    void testRegisterCancellationStoresPassengerNameAndTimestamp() {
        String name = "John Doe";

        long before = System.currentTimeMillis();
        CancellationState.registerCancellation(name);
        long after = System.currentTimeMillis();

        assertEquals(name, CancellationState.getLastCancelledPassenger(), "Passenger name should be stored correctly.");

        long updated = CancellationState.getLastUpdated();
        assertTrue(updated >= before && updated <= after, "Timestamp should be within the expected range.");
    }

    @Test
    void testOverwritingPreviousCancellation() throws InterruptedException {
        CancellationState.registerCancellation("Alice");
        Thread.sleep(5); // to ensure time difference
        CancellationState.registerCancellation("Bob");

        assertEquals("Bob", CancellationState.getLastCancelledPassenger(), "Should return the most recent cancelled passenger.");
    }

    @Test
    void testNullCancellation() {
        CancellationState.registerCancellation(null);
        assertNull(CancellationState.getLastCancelledPassenger(), "Null passenger name should be allowed.");
    }

    @Test
    void testEmptyStringCancellation() {
        CancellationState.registerCancellation("");
        assertEquals("", CancellationState.getLastCancelledPassenger(), "Empty string should be stored as passenger name.");
    }
}