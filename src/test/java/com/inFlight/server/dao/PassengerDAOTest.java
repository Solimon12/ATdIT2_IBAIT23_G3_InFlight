package com.inFlight.server.dao;

import com.inFlight.server.db.SQLiteConnector;
import com.inFlight.shared.model.Passenger;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PassengerDAOTest {

    private PassengerDAO dao;
    private Connection mockConn;
    private PreparedStatement mockStmt;
    private ResultSet mockRs;
    private MockedStatic<SQLiteConnector> mockConnector;

    @BeforeEach
    void setUp() throws Exception {
        dao = new PassengerDAO();
        mockConn = mock(Connection.class);
        mockStmt = mock(PreparedStatement.class);
        mockRs = mock(ResultSet.class);

        mockConnector = mockStatic(SQLiteConnector.class);
        mockConnector.when(SQLiteConnector::getConnection).thenReturn(mockConn);
    }

    @AfterEach
    void tearDown() {
        mockConnector.close();
    }

    @Test
    void testInsertPassenger() throws Exception {
        // Create a test Passenger
        Passenger passenger = new Passenger("john_doe", "password123", 100);

        // Simulate PreparedStatement behavior
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

        // Call the method to test
        dao.insertPassenger(passenger);

        // Verify that the correct values are set in the PreparedStatement
        verify(mockStmt).setString(1, "john_doe");
        verify(mockStmt).setString(2, "password123");
        verify(mockStmt).setInt(3, 100);

        // Verify that executeUpdate was called
        verify(mockStmt).executeUpdate();
    }

    @Test
    void testGetPassengerByUsername() throws Exception {
        // Simulate the PreparedStatement and ResultSet
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);

        // Simulate ResultSet behavior
        when(mockRs.next()).thenReturn(true, false);  // Simulate one result returned
        when(mockRs.getInt("passenger_id")).thenReturn(1);
        when(mockRs.getString("username")).thenReturn("john_doe");
        when(mockRs.getString("password")).thenReturn("password123");
        when(mockRs.getInt("novaCredits")).thenReturn(100);

        // Call the method to test
        Passenger result = dao.getPassengerByUsername("john_doe");

        // Verify that the result contains the expected data
        assertNotNull(result);
        assertEquals(1, result.getPassengerId());
        assertEquals("john_doe", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertEquals(100, result.getNovaCredits());
    }

    @Test
    void testGetPassengerById() throws Exception {
        // Simulate the PreparedStatement and ResultSet
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);

        // Simulate ResultSet behavior
        when(mockRs.next()).thenReturn(true, false);  // Simulate one result returned
        when(mockRs.getInt("passenger_id")).thenReturn(1);
        when(mockRs.getString("username")).thenReturn("john_doe");
        when(mockRs.getString("password")).thenReturn("password123");
        when(mockRs.getInt("novaCredits")).thenReturn(100);

        // Call the method to test
        Passenger result = dao.getPassengerById(1);

        // Verify that the result contains the expected data
        assertNotNull(result);
        assertEquals(1, result.getPassengerId());
        assertEquals("john_doe", result.getUsername());
        assertEquals("password123", result.getPassword());
        assertEquals(100, result.getNovaCredits());
    }

    @Test
    void testGetAllPassengers() throws Exception {
        // Simulate the PreparedStatement and ResultSet
        when(mockConn.createStatement()).thenReturn(mockStmt);
        when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);

        // Simulate ResultSet behavior
        when(mockRs.next()).thenReturn(true, false);  // Simulate one result returned
        when(mockRs.getInt("passenger_id")).thenReturn(1);
        when(mockRs.getString("username")).thenReturn("john_doe");
        when(mockRs.getString("password")).thenReturn("password123");
        when(mockRs.getInt("novaCredits")).thenReturn(100);

        // Call the method to test
        List<Passenger> result = dao.getAllPassengers();

        // Verify that the result contains the expected data
        assertEquals(1, result.size());
        Passenger passenger = result.get(0);
        assertEquals(1, passenger.getPassengerId());
        assertEquals("john_doe", passenger.getUsername());
        assertEquals("password123", passenger.getPassword());
        assertEquals(100, passenger.getNovaCredits());
    }

    @Test
    void testUpdateNovaCredits() throws Exception {
        // Create a test passenger ID and new credits value
        int passengerId = 1;
        int newCredits = 200;

        // Simulate PreparedStatement behavior
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

        // Call the method to test
        dao.updateNovaCredits(passengerId, newCredits);

        // Verify that the correct values are set in the PreparedStatement
        verify(mockStmt).setInt(1, newCredits);
        verify(mockStmt).setInt(2, passengerId);

        // Verify that executeUpdate was called
        verify(mockStmt).executeUpdate();
    }

    @Test
    void testSetCheckedOut() throws Exception {
        int passengerId = 42;
        boolean status = true;

        // Simulate PreparedStatement behavior
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

        // Call the method to test
        dao.setCheckedOut(passengerId, status);

        // Verify correct SQL parameter bindings
        verify(mockStmt).setInt(1, 1);  // true becomes 1
        verify(mockStmt).setInt(2, passengerId);

        // Verify that executeUpdate was called
        verify(mockStmt).executeUpdate();
    }
}