package com.inFlight.shared.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhotographerTest {

    @Test
    void testFullConstructor() {
        Photographer photographer = new Photographer(5, "Jane Doe", true);

        assertEquals(5, photographer.getId());
        assertEquals("Jane Doe", photographer.getName());
        assertTrue(photographer.isCheckedOut());
    }

    @Test
    void testPartialConstructorDefaults() {
        Photographer photographer = new Photographer("John Smith");

        assertEquals(-1, photographer.getId());
        assertEquals("John Smith", photographer.getName());
        assertFalse(photographer.isCheckedOut()); // default should be false
    }

    @Test
    void testSetCheckedOut() {
        Photographer photographer = new Photographer("Alex Johnson");

        photographer.setCheckedOut(true);
        assertTrue(photographer.isCheckedOut());

        photographer.setCheckedOut(false);
        assertFalse(photographer.isCheckedOut());
    }

    @Test
    void testToStringReturnsName() {
        Photographer photographer = new Photographer("Emily Zhang");
        assertEquals("Emily Zhang", photographer.toString());
    }
}