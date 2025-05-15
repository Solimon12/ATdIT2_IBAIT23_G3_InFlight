package com.inFlight.shared.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void testConstructorAndGetters() {
        Booking booking = new Booking(101, 10, 5, 2, "pending");

        assertEquals(101, booking.getBookingId());
        assertEquals(10, booking.getPassengerId());
        assertEquals(5, booking.getSlotId());
        assertEquals(2, booking.getTier());
        assertEquals("pending", booking.getStatus());
    }

    @Test
    void testSetters() {
        Booking booking = new Booking(0, 0, 0, 0, "");

        booking.setBookingId(200);
        booking.setPassengerId(20);
        booking.setSlotId(8);
        booking.setTier(3);
        booking.setStatus("approved");

        assertEquals(200, booking.getBookingId());
        assertEquals(20, booking.getPassengerId());
        assertEquals(8, booking.getSlotId());
        assertEquals(3, booking.getTier());
        assertEquals("approved", booking.getStatus());
    }

    @Test
    void testToStringFormat() {
        Booking booking = new Booking(1, 2, 3, 4, "confirmed");
        String expected = "Booking{id=1, passengerId=2, slotId=3, tier=4, status='confirmed'}";

        assertEquals(expected, booking.toString());
    }
}