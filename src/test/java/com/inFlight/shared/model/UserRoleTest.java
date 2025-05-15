package com.inFlight.shared.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void testEnumValuesExistence() {
        assertNotNull(UserRole.PASSENGER);
        assertNotNull(UserRole.PILOT);
        assertNotNull(UserRole.ATTENDANT);
        assertNotNull(UserRole.PHOTOGRAPHER);
        assertNotNull(UserRole.ATC);
    }

    @Test
    void testValueOf() {
        assertEquals(UserRole.PASSENGER, UserRole.valueOf("PASSENGER"));
        assertEquals(UserRole.PILOT, UserRole.valueOf("PILOT"));
        assertEquals(UserRole.ATTENDANT, UserRole.valueOf("ATTENDANT"));
        assertEquals(UserRole.PHOTOGRAPHER, UserRole.valueOf("PHOTOGRAPHER"));
        assertEquals(UserRole.ATC, UserRole.valueOf("ATC"));
    }

    @Test
    void testValuesLength() {
        UserRole[] values = UserRole.values();
        assertEquals(5, values.length);
    }

    @Test
    void testOrdinalPositions() {
        assertEquals(0, UserRole.PASSENGER.ordinal());
        assertEquals(1, UserRole.PILOT.ordinal());
        assertEquals(2, UserRole.ATTENDANT.ordinal());
        assertEquals(3, UserRole.PHOTOGRAPHER.ordinal());
        assertEquals(4, UserRole.ATC.ordinal());
    }
}