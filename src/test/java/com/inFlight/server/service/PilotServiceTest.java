package com.inFlight.server.service;

import com.inFlight.server.dao.BookingDAO;
import com.inFlight.shared.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PilotServiceTest {
    private BookingDAO mockBookingDAO;
    private PilotService pilotService;

    @BeforeEach
    void setUp() {
        mockBookingDAO = mock(BookingDAO.class);
        pilotService = new PilotService(mockBookingDAO);
    }

    @Test
    void testGetAllPendingBookings() {
        Booking b1 = new Booking(1, 10, 100, 1, "pending");
        Booking b2 = new Booking(2, 11, 101, 2, "approved");
        Booking b3 = new Booking(3, 12, 102, 3, "pending");

        when(mockBookingDAO.getAllBookings()).thenReturn(List.of(b1, b2, b3));

        List<Booking> result = pilotService.getAllPendingBookings();

        assertEquals(2, result.size());
        assertTrue(result.contains(b1));
        assertTrue(result.contains(b3));
        assertFalse(result.contains(b2));
    }

    @Test
    void testApproveBooking() {
        int bookingId = 1;

        pilotService.approveBooking(bookingId);

        verify(mockBookingDAO, times(1)).updateBookingStatus(bookingId, "approved");
    }

    @Test
    void testDenyBooking() {
        int bookingId = 2;

        pilotService.denyBooking(bookingId);

        verify(mockBookingDAO, times(1)).updateBookingStatus(bookingId, "denied");
    }
}