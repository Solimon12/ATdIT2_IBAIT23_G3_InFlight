package com.inFlight.server.dao;

import com.inFlight.shared.model.InventoryItem;
import com.inFlight.server.db.SQLiteConnector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryItemDAOTest {

    private Connection mockConnection;
    private PreparedStatement mockStmt;
    private ResultSet mockResultSet;

    private InventoryItemDAO dao;

    @BeforeEach
    void setup() throws Exception {
        dao = new InventoryItemDAO();
        mockConnection = mock(Connection.class);
        mockStmt = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
    }

    @Test
    void testInsertItem() throws Exception {
        InventoryItem item = new InventoryItem("Camera", "Good", true, "photographer");

        try (MockedStatic<SQLiteConnector> mockedStatic = mockStatic(SQLiteConnector.class)) {
            mockedStatic.when(SQLiteConnector::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);

            dao.insertItem(item);

            verify(mockStmt).setString(1, "Camera");
            verify(mockStmt).setString(2, "Good");
            verify(mockStmt).setInt(3, 1);
            verify(mockStmt).setString(4, "photographer");
            verify(mockStmt).setInt(5, 0);
            verify(mockStmt).executeUpdate();
        }
    }

    @Test
    void testGetItemsByRole() throws Exception {
        try (MockedStatic<SQLiteConnector> mockedStatic = mockStatic(SQLiteConnector.class)) {
            mockedStatic.when(SQLiteConnector::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);
            when(mockStmt.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true, false); // only one item
            when(mockResultSet.getInt("id")).thenReturn(1);
            when(mockResultSet.getString("name")).thenReturn("Drill");
            when(mockResultSet.getString("condition")).thenReturn("Worn");
            when(mockResultSet.getInt("available")).thenReturn(0);
            when(mockResultSet.getString("owner_role")).thenReturn("attendant");
            when(mockResultSet.getInt("checked_out")).thenReturn(1);

            List<InventoryItem> items = dao.getItemsByRole("attendant");

            assertEquals(1, items.size());
            InventoryItem item = items.get(0);
            assertEquals("Drill", item.getName());
            assertEquals("Worn", item.getCondition());
            assertFalse(item.isAvailable());
            assertEquals("attendant", item.getOwnerRole());
            assertTrue(item.isCheckedOut());
        }
    }

    @Test
    void testUpdateItem() throws Exception {
        InventoryItem item = new InventoryItem(42, "Helmet", "Excellent", true, "attendant", false);

        try (MockedStatic<SQLiteConnector> mockedStatic = mockStatic(SQLiteConnector.class)) {
            mockedStatic.when(SQLiteConnector::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStmt);

            dao.updateItem(item);

            verify(mockStmt).setString(1, "Excellent");
            verify(mockStmt).setInt(2, 1);
            verify(mockStmt).setInt(3, 0);
            verify(mockStmt).setInt(4, 42);
            verify(mockStmt).executeUpdate();
        }
    }
}