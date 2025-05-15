package com.inFlight.server.service;
import com.google.gson.JsonObject;
import com.inFlight.server.dao.BookingDAO;
import com.inFlight.server.dao.PassengerDAO;
import com.inFlight.server.dao.SpacewalkSlotDAO;
import com.inFlight.server.service.BookingService;
import com.inFlight.server.service.PassengerService;
import com.inFlight.shared.model.Booking;
import com.inFlight.shared.model.Passenger;
import com.inFlight.shared.model.SpacewalkSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    private BookingDAO bookingDAO;
    private SpacewalkSlotDAO slotDAO;
    private PassengerDAO passengerDAO;
    private PassengerService passengerService;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingDAO = mock(BookingDAO.class);
        slotDAO = mock(SpacewalkSlotDAO.class);
        passengerDAO = mock(PassengerDAO.class);
        passengerService = mock(PassengerService.class);
        bookingService = new BookingService(bookingDAO, slotDAO, passengerDAO, passengerService);
    }

    @Test
    void testProcessBookingSuccess() {
        Passenger passenger = new Passenger(1, "user1", "pass", 250000);
        SpacewalkSlot slot = new SpacewalkSlot(10, "2025-06-01T12:00:00Z", true);

        when(passengerDAO.getPassengerById(1)).thenReturn(passenger);
        when(slotDAO.getSlotById(10)).thenReturn(slot);
        when(slotDAO.isSlotAvailable(10)).thenReturn(true);

        JsonObject result = bookingService.processBooking(1, 10, 2); // Tier 2: 250000

        assertEquals("OK", result.get("status").getAsString());
        assertEquals(250000, result.get("newBalance").getAsInt());

        verify(passengerDAO).updateNovaCredits(1, 0);
        verify(bookingDAO).insertBooking(any(Booking.class));
        verify(slotDAO).setAvailability(10, false);
    }

    @Test
    void testProcessBookingFailsIfPassengerNull() {
        when(passengerDAO.getPassengerById(1)).thenReturn(null);
        when(slotDAO.getSlotById(5)).thenReturn(new SpacewalkSlot(5, "2025-06-01T12:00:00Z", true));
        when(slotDAO.isSlotAvailable(5)).thenReturn(true);

        JsonObject result = bookingService.processBooking(1, 5, 1);

        assertEquals("ERROR", result.get("status").getAsString());
        verify(passengerDAO, never()).updateNovaCredits(anyInt(), anyInt());
    }

    @Test
    void testProcessBookingFailsIfSlotIsNull() {
        Passenger passenger = new Passenger(1, "user", "pass", 100000);

        when(passengerDAO.getPassengerById(1)).thenReturn(passenger);
        when(slotDAO.getSlotById(10)).thenReturn(null);

        JsonObject result = bookingService.processBooking(1, 10, 1);

        assertEquals("ERROR", result.get("status").getAsString());
        verify(passengerDAO, never()).updateNovaCredits(anyInt(), anyInt());
    }

    @Test
    void testProcessBookingFailsIfSlotUnavailable() {
        Passenger passenger = new Passenger(1, "user", "pass", 500000);
        SpacewalkSlot slot = new SpacewalkSlot(5, "2025-06-01T12:00:00Z", false);

        when(passengerDAO.getPassengerById(1)).thenReturn(passenger);
        when(slotDAO.getSlotById(5)).thenReturn(slot);
        when(slotDAO.isSlotAvailable(5)).thenReturn(false);

        JsonObject result = bookingService.processBooking(1, 5, 3);

        assertEquals("ERROR", result.get("status").getAsString());
        verify(passengerDAO, never()).updateNovaCredits(anyInt(), anyInt());
    }

    @Test
    void testProcessBookingFailsDueToLowCredits() {
        Passenger passenger = new Passenger(1, "lowcredit", "pass", 50000);
        SpacewalkSlot slot = new SpacewalkSlot(5, "2025-06-01T12:00:00Z", true);

        when(passengerDAO.getPassengerById(1)).thenReturn(passenger);
        when(slotDAO.getSlotById(5)).thenReturn(slot);
        when(slotDAO.isSlotAvailable(5)).thenReturn(true);

        JsonObject result = bookingService.processBooking(1, 5, 2);

        assertEquals("ERROR", result.get("status").getAsString());
        assertEquals("Booking failed.", result.get("message").getAsString());

        verify(passengerDAO, never()).updateNovaCredits(anyInt(), anyInt());
        verify(bookingDAO, never()).insertBooking(any());
    }

    @Test
    void testGetBookingsForPassenger() {
        List<Booking> bookings = List.of(new Booking(1, 42, 99, 1, "pending"));
        when(bookingDAO.getBookingsByPassenger(42)).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsForPassenger(42);
        assertEquals(1, result.size());
        assertEquals(42, result.get(0).getPassengerId());
    }

    @Test
    void testGetBookingsByStatus() {
        List<Booking> bookings = List.of(new Booking(2, 11, 33, 2, "approved"));
        when(bookingDAO.getBookingsByStatus("approved")).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByStatus("approved");
        assertEquals(1, result.size());
        assertEquals("approved", result.get(0).getStatus());
    }

    @Test
    void testApproveBooking() {
        bookingService.approveBooking(101);
        verify(bookingDAO).updateBookingStatus(101, "approved");
    }

    @Test
    void testDenyBooking() {
        bookingService.denyBooking(102);
        verify(bookingDAO).updateBookingStatus(102, "denied");
    }

    @Test
    void testGetAllBookings() {
        List<Booking> bookings = List.of(
                new Booking(1, 10, 20, 1, "pending"),
                new Booking(2, 11, 21, 2, "approved")
        );
        when(bookingDAO.getAllBookings()).thenReturn(bookings);

        List<Booking> result = bookingService.getAllBookings();
        assertEquals(2, result.size());
    }

    @Test
    void testSetBookingStatus() {
        bookingService.setBookingStatus(200, "cancelled");
        verify(bookingDAO).updateBookingStatus(200, "cancelled");
    }
}