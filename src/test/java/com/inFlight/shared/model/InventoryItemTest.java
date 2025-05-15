package com.inFlight.shared.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryItemTest {

    @Test
    void testFullConstructorAndGetters() {
        InventoryItem item = new InventoryItem(1, "Helmet", "New", true, "attendant", true);

        assertEquals(1, item.getId());
        assertEquals("Helmet", item.getName());
        assertEquals("New", item.getCondition());
        assertTrue(item.isAvailable());
        assertEquals("attendant", item.getOwnerRole());
        assertTrue(item.isCheckedOut());
    }

    @Test
    void testPartialConstructorDefaults() {
        InventoryItem item = new InventoryItem("Camera", "Good", true, "photographer");

        assertEquals(-1, item.getId());
        assertEquals("Camera", item.getName());
        assertEquals("Good", item.getCondition());
        assertTrue(item.isAvailable());
        assertEquals("photographer", item.getOwnerRole());
        assertFalse(item.isCheckedOut()); // default value
    }

    @Test
    void testSetters() {
        InventoryItem item = new InventoryItem("Temp", "Fair", false, "temp");

        item.setId(10);
        item.setName("Tablet");
        item.setCondition("Used");
        item.setAvailable(true);
        item.setOwnerRole("attendant");
        item.setCheckedOut(true);

        assertEquals(10, item.getId());
        assertEquals("Tablet", item.getName());
        assertEquals("Used", item.getCondition());
        assertTrue(item.isAvailable());
        assertEquals("attendant", item.getOwnerRole());
        assertTrue(item.isCheckedOut());
    }

    @Test
    void testToStringReturnsName() {
        InventoryItem item = new InventoryItem("Wrench", "Good", true, "attendant");
        assertEquals("Wrench", item.toString());
    }
}