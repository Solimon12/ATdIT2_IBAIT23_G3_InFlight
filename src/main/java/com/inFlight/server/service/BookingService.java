package com.inFlight.server.service;

import com.google.gson.JsonObject;
import com.inFlight.server.dao.BookingDAO;
import com.inFlight.server.dao.PassengerDAO;
import com.inFlight.server.dao.SpacewalkSlotDAO;
import com.inFlight.shared.model.Booking;
import com.inFlight.shared.model.Passenger;
import com.inFlight.shared.model.SpacewalkSlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * BookingService is a service class that handles booking-related operations.
 * It interacts with the BookingDAO, SpacewalkSlotDAO, and PassengerDAO to manage bookings.
 */
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

    private final BookingDAO bookingDAO; // DAO for booking operations
    private final SpacewalkSlotDAO slotDAO; // DAO for spacewalk slot operations
    private final PassengerDAO passengerDAO; // DAO for passenger operations
    private final PassengerService passengerService; // Service for passenger-related operations
    private static final int TIER1_COST = 100000; // Cost for Tier 1 booking
    private static final int TIER2_COST = 250000; // Cost for Tier 2 booking
    private static final int TIER3_COST = 500000; // Cost for Tier 3 booking

    /**
     * Default constructor initializes the DAOs and services.
     */
    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.slotDAO = new SpacewalkSlotDAO();
        this.passengerDAO = new PassengerDAO();
        this.passengerService = new PassengerService();
    }

    /**
     * Constructor for dependency injection
     */
    public BookingService(BookingDAO bookingDAO, SpacewalkSlotDAO slotDAO, PassengerDAO passengerDAO, PassengerService passengerService) {
        this.bookingDAO = bookingDAO;
        this.slotDAO = slotDAO;
        this.passengerDAO = passengerDAO;
        this.passengerService = passengerService;
    }

    /**
     * Processes a booking for a passenger.
     *
     * @param passengerId the ID of the passenger
     * @param slotId      the ID of the slot
     * @param tier        the tier of the booking
     * @return a JsonObject containing the status and new balance
     */
    public JsonObject processBooking(int passengerId, int slotId, int tier) {
       // Create a JSON object to store the result
        JsonObject result = new JsonObject();

        //retrieve passenger and slot information
        Passenger passenger = passengerDAO.getPassengerById(passengerId);
        SpacewalkSlot slot = slotDAO.getSlotById(slotId);

        // Set cost based on tier
        int cost = switch (tier) {
            case 1 -> TIER1_COST;
            case 2 -> TIER2_COST;
            case 3 -> TIER3_COST;
            default -> 9999;
        };

        // Check if the passenger and slot exist and if the slot is still available
        boolean isStillAvailable = slotDAO.isSlotAvailable(slotId);

        // Check if the passenger has enough nova credits and if the slot is still available
        if (passenger == null || slot == null || !isStillAvailable || passenger.getNovaCredits() < cost) {
            result.addProperty("status", "ERROR");
            result.addProperty("message", "Booking failed.");
            return result;
        }

        // Update passenger's nova credits and insert booking
        passengerDAO.updateNovaCredits(passenger.getPassengerId(), passenger.getNovaCredits() - cost);
        // Create a new booking
        bookingDAO.insertBooking(new Booking(1, passengerId, slotId, tier, "pending"));
        // Update the slot availability
        slotDAO.setAvailability(slotId, false);

        //result for the client through JSON
        result.addProperty("status", "OK");
        result.addProperty("newBalance", passenger.getNovaCredits());
        return result;
    }

    /**
     * Retrieves all bookings made by a specific passenger.
     *
     * @param passengerId the ID of the passenger
     * @return a list of Booking objects made by the specified passenger
     */
    public List<Booking> getBookingsForPassenger(int passengerId) {
        return bookingDAO.getBookingsByPassenger(passengerId);
    }

    /**
     * Retrieves all bookings with a specific status.
     *
     * @param status the status of the bookings to retrieve
     * @return a list of Booking objects with the specified status
     */
    public List<Booking> getBookingsByStatus(String status) {
        return bookingDAO.getBookingsByStatus(status);
    }

    /**
     * Approves a booking by updating its status to "approved".
     *
     * @param bookingId the ID of the booking to approve
     */
    public void approveBooking(int bookingId) {
        bookingDAO.updateBookingStatus(bookingId, "approved");
    }

    /**
     * Denies a booking by updating its status to "denied".
     *
     * @param bookingId the ID of the booking to deny
     */
    public void denyBooking(int bookingId) {
        bookingDAO.updateBookingStatus(bookingId, "denied");
    }

    /**
     * Retrieves all bookings from the database.
     *
     * @return a list of all Booking objects
     */
    public List<Booking> getAllBookings() {
        return bookingDAO.getAllBookings();
    }

    /**
     * Updates the status of a booking.
     *
     * @param approveId the ID of the booking to update
     * @param status    the new status to set
     */
    public void setBookingStatus(int approveId, String status) {
        bookingDAO.updateBookingStatus(approveId, status);
    }
}
