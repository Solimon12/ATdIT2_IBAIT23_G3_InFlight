package com.inFlight.shared.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PassengerTest {

    @Test
    void testConstructorWithoutCheckedOut() {
        Passenger passenger = new Passenger(1, "john_doe", "secret", 100);
        assertEquals(1, passenger.getPassengerId());
        assertEquals("john_doe", passenger.getUsername());
        assertEquals("secret", passenger.getPassword());
        assertEquals(100, passenger.getNovaCredits());
        assertFalse(passenger.isCheckedOut());
    }

    @Test
    void testConstructorWithCheckedOut() {
        Passenger passenger = new Passenger(2, "jane_doe", "pass123", 150, true);
        assertEquals(2, passenger.getPassengerId());
        assertEquals("jane_doe", passenger.getUsername());
        assertEquals("pass123", passenger.getPassword());
        assertEquals(150, passenger.getNovaCredits());
        assertTrue(passenger.isCheckedOut());
    }

    @Test
    void testConstructorWithDefaultId() {
        Passenger passenger = new Passenger("guest", "pwd", 75);
        assertEquals(-1, passenger.getPassengerId());
        assertEquals("guest", passenger.getUsername());
        assertEquals("pwd", passenger.getPassword());
        assertEquals(75, passenger.getNovaCredits());
        assertFalse(passenger.isCheckedOut());
    }

    @Test
    void testSettersAndGetters() {
        Passenger passenger = new Passenger("user", "pass", 50);
        passenger.setPassengerId(10);
        passenger.setUsername("updatedUser");
        passenger.setPassword("newPass");
        passenger.setNovaCredits(200);
        passenger.setCheckedOut(true);

        assertEquals(10, passenger.getPassengerId());
        assertEquals("updatedUser", passenger.getUsername());
        assertEquals("newPass", passenger.getPassword());
        assertEquals(200, passenger.getNovaCredits());
        assertTrue(passenger.isCheckedOut());
    }

    @Test
    void testToStringContainsExpectedFields() {
        Passenger passenger = new Passenger(3, "alex", "xyz", 120);
        String output = passenger.toString();
        assertTrue(output.contains("alex"));
        assertTrue(output.contains("120"));
        assertTrue(output.contains("3"));
    }
}