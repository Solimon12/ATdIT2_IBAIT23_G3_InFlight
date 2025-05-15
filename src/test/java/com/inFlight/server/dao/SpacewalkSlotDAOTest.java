package com.inFlight.server.dao;

import com.inFlight.server.db.SQLiteConnector;
import com.inFlight.shared.model.SpacewalkSlot;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpacewalkSlotDAOTest {

    private SpacewalkSlotDAO dao;
    private Connection mockConn;
    private PreparedStatement mockStmt;
    private ResultSet mockRs;
    private MockedStatic<SQLiteConnector> mockConnector;

    @BeforeEach
    void setUp() throws Exception {
        dao = new SpacewalkSlotDAO();
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
    void testGetAvailableSlots() throws Exception {
        // Simulate the PreparedStatement and ResultSet behavior
        when(mockConn.createStatement()).thenReturn(mockStmt);
        when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);

        // Simulate ResultSet behavior
        when(mockRs.next()).thenReturn(true, false);  // Simulate one result returned
        when(mockRs.getInt("slot_id")).thenReturn(1);
        when(mockRs.getString("slot_time")).thenReturn("2025-05-11T12:00:00Z");
        when(mockRs.getInt("available")).thenReturn(1);

        // Call the method to test
        List<SpacewalkSlot> slots = dao.getAvailableSlots();

        // Verify that the result contains the expected data
        assertNotNull(slots);
        assertEquals(1, slots.size());
        SpacewalkSlot slot = slots.get(0);
        assertEquals(1, slot.getSlotId());
        assertEquals("2025-05-11T12:00:00Z", slot.getSlotTime());
        assertTrue(slot.isAvailable());
    }

    @Test
    void testInsertSlot() throws Exception {
        // Create a test SpacewalkSlot
        SpacewalkSlot slot = new SpacewalkSlot("2025-05-11T12:00:00Z");

        // Simulate PreparedStatement behavior
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

        // Call the method to test
        dao.insertSlot(slot);

        // Verify that the correct values are set in the PreparedStatement
        verify(mockStmt).setString(1, "2025-05-11T12:00:00Z");
        verify(mockStmt).setInt(2, 1);  // available is true, so it's set to 1

        // Verify that executeUpdate was called
        verify(mockStmt).executeUpdate();
    }

    @Test
    void testGetAllSlots() throws Exception {
        // Simulate the PreparedStatement and ResultSet behavior
        when(mockConn.createStatement()).thenReturn(mockStmt);
        when(mockStmt.executeQuery(anyString())).thenReturn(mockRs);

        // Simulate ResultSet behavior
        when(mockRs.next()).thenReturn(true, false);  // Simulate one result returned
        when(mockRs.getInt("slot_id")).thenReturn(1);
        when(mockRs.getString("slot_time")).thenReturn("2025-05-11T12:00:00Z");
        when(mockRs.getInt("available")).thenReturn(1);

        // Call the method to test
        List<SpacewalkSlot> slots = dao.getAllSlots();

        // Verify that the result contains the expected data
        assertNotNull(slots);
        assertEquals(1, slots.size());
        SpacewalkSlot slot = slots.get(0);
        assertEquals(1, slot.getSlotId());
        assertEquals("2025-05-11T12:00:00Z", slot.getSlotTime());
        assertTrue(slot.isAvailable());
    }

    @Test
    void testGetSlotById() throws Exception {
        // Simulate the PreparedStatement and ResultSet behavior
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);

        // Simulate ResultSet behavior
        when(mockRs.next()).thenReturn(true, false);  // Simulate one result returned
        when(mockRs.getInt("slot_id")).thenReturn(1);
        when(mockRs.getString("slot_time")).thenReturn("2025-05-11T12:00:00Z");
        when(mockRs.getInt("available")).thenReturn(1);

        // Call the method to test
        SpacewalkSlot slot = dao.getSlotById(1);

        // Verify that the result contains the expected data
        assertNotNull(slot);
        assertEquals(1, slot.getSlotId());
        assertEquals("2025-05-11T12:00:00Z", slot.getSlotTime());
        assertTrue(slot.isAvailable());
    }

    @Test
    void testSetAvailability() throws Exception {
        // Create a test slot ID and availability value
        int slotId = 1;
        boolean available = false;

        // Simulate PreparedStatement behavior
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

        // Call the method to test
        dao.setAvailability(slotId, available);

        // Verify that the correct values are set in the PreparedStatement
        verify(mockStmt).setInt(1, 0);  // available is false, so it's set to 0
        verify(mockStmt).setInt(2, slotId);

        // Verify that executeUpdate was called
        verify(mockStmt).executeUpdate();
    }

    @Test
    void testIsSlotAvailable() throws Exception {
        // Simulate the PreparedStatement and ResultSet behavior
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);

        // Simulate ResultSet behavior
        when(mockRs.next()).thenReturn(true, false);  // Simulate one result returned
        when(mockRs.getInt("available")).thenReturn(1);

        // Call the method to test
        boolean isAvailable = dao.isSlotAvailable(1);

        // Verify that the slot is available
        assertTrue(isAvailable);
    }

    @Test
    void testDeleteSlot() throws Exception {
        // Create a test slot ID
        int slotId = 1;

        // Simulate PreparedStatement behavior
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

        // Call the method to test
        dao.deleteSlot(slotId);

        // Verify that the correct values are set in the PreparedStatement
        verify(mockStmt).setInt(1, slotId);

        // Verify that executeUpdate was called
        verify(mockStmt).executeUpdate();
    }
}