package com.inFlight.server.dao;

import com.inFlight.server.db.SQLiteConnector;
import com.inFlight.shared.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingDAOTest {
    private Connection mockConn;
    private PreparedStatement mockStmt;
    private ResultSet mockRs;

    private BookingDAO dao;

    @BeforeEach
    void setUp() throws Exception {
        mockConn = mock(Connection.class);
        mockStmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);

        dao = new BookingDAO();
    }

    @Test
    void testInsertBooking() throws Exception {
        Booking booking = new Booking(1, 2, 3, 1, "CONFIRMED");

        try (MockedStatic<SQLiteConnector> mockConnector = mockStatic(SQLiteConnector.class)) {
            mockConnector.when(SQLiteConnector::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

            dao.insertBooking(booking);

            verify(mockStmt).setInt(1, booking.getPassengerId());
            verify(mockStmt).setInt(2, booking.getSlotId());
            verify(mockStmt).setInt(3, booking.getTier());
            verify(mockStmt).setString(4, booking.getStatus());
            verify(mockStmt).executeUpdate();
        }
    }

    @Test
    void testGetBookingById() throws Exception {
        try (MockedStatic<SQLiteConnector> mockConnector = mockStatic(SQLiteConnector.class)) {
            mockConnector.when(SQLiteConnector::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);
            when(mockRs.next()).thenReturn(true);
            when(mockRs.getInt("booking_id")).thenReturn(1);
            when(mockRs.getInt("passenger_id")).thenReturn(2);
            when(mockRs.getInt("slot_id")).thenReturn(3);
            when(mockRs.getInt("tier")).thenReturn(1);
            when(mockRs.getString("status")).thenReturn("CONFIRMED");

            Booking result = BookingDAO.getBookingById(1);

            assertNotNull(result);
            assertEquals(1, result.getBookingId());
            assertEquals("CONFIRMED", result.getStatus());
        }
    }

    @Test
    void testGetBookingsByPassenger() throws Exception {
        try (MockedStatic<SQLiteConnector> mockConnector = mockStatic(SQLiteConnector.class)) {
            mockConnector.when(SQLiteConnector::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);

            when(mockRs.next()).thenReturn(true, false);
            when(mockRs.getInt("booking_id")).thenReturn(1);
            when(mockRs.getInt("passenger_id")).thenReturn(2);
            when(mockRs.getInt("slot_id")).thenReturn(3);
            when(mockRs.getInt("tier")).thenReturn(1);
            when(mockRs.getString("status")).thenReturn("CONFIRMED");

            List<Booking> result = dao.getBookingsByPassenger(2);

            assertEquals(1, result.size());
            assertEquals(2, result.get(0).getPassengerId());
        }
    }

    @Test
    void testUpdateBookingStatus() throws Exception {
        try (MockedStatic<SQLiteConnector> mockConnector = mockStatic(SQLiteConnector.class)) {
            mockConnector.when(SQLiteConnector::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

            dao.updateBookingStatus(1, "CANCELLED");

            verify(mockStmt).setString(1, "CANCELLED");
            verify(mockStmt).setInt(2, 1);
            verify(mockStmt).executeUpdate();
        }
    }

    @Test
    void testGetAllBookings() throws Exception {
        Statement stmt = mock(Statement.class);

        try (MockedStatic<SQLiteConnector> mockConnector = mockStatic(SQLiteConnector.class)) {
            mockConnector.when(SQLiteConnector::getConnection).thenReturn(mockConn);
            when(mockConn.createStatement()).thenReturn(stmt);
            when(stmt.executeQuery(anyString())).thenReturn(mockRs);

            when(mockRs.next()).thenReturn(true, false);
            when(mockRs.getInt("booking_id")).thenReturn(1);
            when(mockRs.getInt("passenger_id")).thenReturn(2);
            when(mockRs.getInt("slot_id")).thenReturn(3);
            when(mockRs.getInt("tier")).thenReturn(1);
            when(mockRs.getString("status")).thenReturn("CONFIRMED");

            List<Booking> result = dao.getAllBookings();

            assertEquals(1, result.size());
        }
    }

    @Test
    void testDeleteBooking() throws Exception {
        try (MockedStatic<SQLiteConnector> mockConnector = mockStatic(SQLiteConnector.class)) {
            mockConnector.when(SQLiteConnector::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

            dao.deleteBooking(42);

            verify(mockStmt).setInt(1, 42);
            verify(mockStmt).executeUpdate();
        }
    }

    @Test
    void testGetBookingsByStatus() throws Exception {
        try (MockedStatic<SQLiteConnector> mockConnector = mockStatic(SQLiteConnector.class)) {
            mockConnector.when(SQLiteConnector::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockRs);

            when(mockRs.next()).thenReturn(true, false);
            when(mockRs.getInt("booking_id")).thenReturn(1);
            when(mockRs.getInt("passenger_id")).thenReturn(2);
            when(mockRs.getInt("slot_id")).thenReturn(3);
            when(mockRs.getInt("tier")).thenReturn(1);
            when(mockRs.getString("status")).thenReturn("WAITLISTED");

            List<Booking> result = dao.getBookingsByStatus("WAITLISTED");

            assertEquals(1, result.size());
            assertEquals("WAITLISTED", result.get(0).getStatus());
        }
    }

    @Test
    void testSetBookingStatus() throws Exception {
        try (MockedStatic<SQLiteConnector> mockConnector = mockStatic(SQLiteConnector.class)) {
            mockConnector.when(SQLiteConnector::getConnection).thenReturn(mockConn);
            when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

            dao.setBookingStatus(1, "CONFIRMED");

            verify(mockStmt).setString(1, "CONFIRMED");
            verify(mockStmt).setInt(2, 1);
            verify(mockStmt).executeUpdate();
        }
    }
}