package com.inFlight.server.service;

import com.inFlight.server.dao.BookingDAO;
import com.inFlight.shared.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * PilotService is a service class that handles pilot-related operations.
 * It interacts with the BookingDAO to manage booking data.
 */
public class PilotService {
    private static final Logger logger = LoggerFactory.getLogger(PilotService.class);

    private final BookingDAO bookingDAO; // DAO for booking operations

    /**
     * Default constructor initializes the DAO.
     */
    public PilotService() {
        this.bookingDAO = new BookingDAO();
    }

    /**
     * Constructor for dependency injection
     *
     * @param bookingDAO the BookingDAO instance to use
     */
    public PilotService (BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    /**
     * Retrieves a list of all pending bookings.
     *
     * @return a list of Booking objects with status "pending"
     */
    public List<Booking> getAllPendingBookings() {
        return bookingDAO.getAllBookings().stream()
                .filter(b -> b.getStatus().equalsIgnoreCase("pending"))
                .collect(Collectors.toList());
    }

    /**
     * approves a booking by updating its status to "approved".
     * @param bookingId the ID of the booking to approve
     */
    public void approveBooking(int bookingId) {
        bookingDAO.updateBookingStatus(bookingId, "approved");
        logger.info("Booking " + bookingId + " approved.");
    }

    /**
     * denies a booking by updating its status to "denied".
     * @param bookingId the ID of the booking to deny
     */
    public void denyBooking(int bookingId) {
        bookingDAO.updateBookingStatus(bookingId, "denied");
        logger.info("Booking " + bookingId + " denied.");
    }
}

