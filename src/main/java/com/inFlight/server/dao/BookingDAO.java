package com.inFlight.server.dao;

import com.inFlight.server.db.SQLiteConnector;
import com.inFlight.shared.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BookingDAO is a Data Access Object (DAO) class that provides methods to interact with the bookings table in the SQLite database.
 * It includes methods to insert, update, delete, and retrieve booking records.
 */
public class BookingDAO {
    private static final Logger logger = LoggerFactory.getLogger(BookingDAO.class);

    /**
     * Retrieves a booking by its ID from the database.
     *
     * @param bookingId the ID of the booking to retrieve
     * @return the Booking object if found, null otherwise
     */
    public static Booking getBookingById(int bookingId) {
        try (Connection conn = SQLiteConnector.getConnection()) {
            String sql = "SELECT * FROM bookings WHERE booking_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Booking(
                        rs.getInt("booking_id"),
                        rs.getInt("passenger_id"),
                        rs.getInt("slot_id"),
                        rs.getInt("tier"),
                        rs.getString("status")
                );
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch booking", e);
        }
        return null;
    }

    /**
     * Inserts a new booking into the database.
     *
     * @param booking the Booking object to insert
     */
    public void insertBooking(Booking booking) {
        try (Connection conn = SQLiteConnector.getConnection()) {
            String sql = "INSERT INTO bookings (passenger_id, slot_id, tier, status) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, booking.getPassengerId());
            stmt.setInt(2, booking.getSlotId());
            stmt.setInt(3, booking.getTier());
            stmt.setString(4, booking.getStatus());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to save booking", e);
        }
    }

    /**
     * Retrieves all bookings made by a specific passenger from the database.
     *
     * @param passengerId the ID of the passenger
     * @return a list of Booking objects made by the specified passenger
     */
    public List<Booking> getBookingsByPassenger(int passengerId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE passenger_id = ?";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, passengerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                bookings.add(new Booking(
                                rs.getInt("booking_id"),
                                rs.getInt("passenger_id"),
                                rs.getInt("slot_id"),
                                rs.getInt("tier"),
                                rs.getString("status")
                        )
                );
            }

        } catch (SQLException e) {
          logger.error("Failed to fetch bookings by passenger", e);
        }
        return bookings;
    }

    /**
     * Updates the status of a booking in the database.
     *
     * @param bookingId the ID of the booking to update
     * @param status    the new status to set
     */
    public void updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";

        try (Connection conn = SQLiteConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            stmt.executeUpdate();

        } catch (SQLException e) {
          logger.error("Failed to update booking status", e);
        }
    }

    /**
     * Retrieves all bookings from the database.
     *
     * @return a list of all Booking objects
     */
    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings";

        try (Connection conn = SQLiteConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bookings.add(new Booking(
                                rs.getInt("booking_id"),
                                rs.getInt("passenger_id"),
                                rs.getInt("slot_id"),
                                rs.getInt("tier"),
                                rs.getString("status")
                        )
                );
            }

        } catch (SQLException e) {
            logger.error("Failed to fetch all bookings", e);
        }

        return bookings;
    }

    /**
     * Deletes a booking from the database.
     *
     * @param bookingId the ID of the booking to delete
     */
    public void deleteBooking(int bookingId) {
        try (Connection conn = SQLiteConnector.getConnection()) {
            String sql = "DELETE FROM bookings WHERE booking_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to delete booking", e);
        }
    }

    /**
     * Retrieves all bookings with a specific status from the database.
     *
     * @param status the status of the bookings to retrieve
     * @return a list of Booking objects with the specified status
     */
    public List<Booking> getBookingsByStatus(String status) {
        List<Booking> result = new ArrayList<>();
        try (Connection conn = SQLiteConnector.getConnection()) {
            String sql = "SELECT * FROM bookings WHERE status = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(new Booking(
                        rs.getInt("booking_id"),
                        rs.getInt("passenger_id"),
                        rs.getInt("slot_id"),
                        rs.getInt("tier"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            logger.error("Failed to get bookings by status", e);
        }
        return result;
    }

    /**
     * Sets the status of a booking in the database.
     *
     * @param id     the ID of the booking to update
     * @param status the new status to set
     */
    public void setBookingStatus(int id, String status) {
        try (Connection conn = SQLiteConnector.getConnection()) {
            String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to set booking status", e);
        }
    }

}
