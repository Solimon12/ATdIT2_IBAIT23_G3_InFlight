package com.inFlight.shared.model;

import com.inFlight.shared.model.SpacewalkSlot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpacewalkSlotTest {

    @Test
    void testConstructorWithAllParameters() {
        SpacewalkSlot slot = new SpacewalkSlot(1, "2025-05-11T12:00:00Z", true);

        assertEquals(1, slot.getSlotId());
        assertEquals("2025-05-11T12:00:00Z", slot.getSlotTime());
        assertTrue(slot.isAvailable());
    }

    @Test
    void testConstructorWithSlotTimeOnly() {
        SpacewalkSlot slot = new SpacewalkSlot("2025-05-11T12:00:00Z");

        assertEquals(-1, slot.getSlotId());  // Default value when no slotId provided
        assertEquals("2025-05-11T12:00:00Z", slot.getSlotTime());
        assertTrue(slot.isAvailable());  // Default value for available is true
    }

    @Test
    void testToString() {
        SpacewalkSlot slot = new SpacewalkSlot(1, "2025-05-11T12:00:00Z", true);
        String expected = "SpacewalkSlot{slotId=1, slotTime='2025-05-11T12:00:00Z', available=true}";

        assertEquals(expected, slot.toString());
    }

    @Test
    void testGettersAndSetters() {
        SpacewalkSlot slot = new SpacewalkSlot("2025-05-11T12:00:00Z");

        slot.setSlotId(2);
        slot.setSlotTime("2025-05-11T14:00:00Z");
        slot.setAvailable(false);

        assertEquals(2, slot.getSlotId());
        assertEquals("2025-05-11T14:00:00Z", slot.getSlotTime());
        assertFalse(slot.isAvailable());
    }
}