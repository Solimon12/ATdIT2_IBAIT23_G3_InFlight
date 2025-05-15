package com.inFlight.server.dao;

import com.inFlight.server.db.SQLiteConnector;
import com.inFlight.shared.model.Photographer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PhotographerDAOTest {

    private PhotographerDAO dao;

    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() throws Exception {
        dao = new PhotographerDAO();
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
    }

    @Test
    public void testInsertPhotographer() throws Exception {
        try (MockedStatic<SQLiteConnector> mockedStatic = mockStatic(SQLiteConnector.class)) {
            Photographer photographer = new Photographer(0, "Alice", false);

            mockedStatic.when(SQLiteConnector::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

            dao.insertPhotographer(photographer);

            verify(mockStatement).setString(1, "Alice");
            verify(mockStatement).setInt(2, 0);
            verify(mockStatement).executeUpdate();
        }
    }

    @Test
    public void testGetPhotographerByName_Found() throws Exception {
        try (MockedStatic<SQLiteConnector> mockedStatic = mockStatic(SQLiteConnector.class)) {
            mockedStatic.when(SQLiteConnector::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt("id")).thenReturn(1);
            when(mockResultSet.getString("name")).thenReturn("Bob");
            when(mockResultSet.getInt("checked_out")).thenReturn(1);

            Photographer result = dao.getPhotographerByName("Bob");

            assertNotNull(result);
            assertEquals(1, result.getId());
            assertEquals("Bob", result.getName());
            assertTrue(result.isCheckedOut());
        }
    }

    @Test
    public void testGetPhotographerByName_NotFound() throws Exception {
        try (MockedStatic<SQLiteConnector> mockedStatic = mockStatic(SQLiteConnector.class)) {
            mockedStatic.when(SQLiteConnector::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            Photographer result = dao.getPhotographerByName("Ghost");
            assertNull(result);
        }
    }

    @Test
    public void testGetPhotographerById_Found() throws Exception {
        try (MockedStatic<SQLiteConnector> mockedStatic = mockStatic(SQLiteConnector.class)) {
            mockedStatic.when(SQLiteConnector::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);

            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt("id")).thenReturn(2);
            when(mockResultSet.getString("name")).thenReturn("Charlie");
            when(mockResultSet.getInt("checked_out")).thenReturn(0);

            Photographer result = dao.getPhotographerById(2);

            assertNotNull(result);
            assertEquals(2, result.getId());
            assertEquals("Charlie", result.getName());
            assertFalse(result.isCheckedOut());
        }
    }

    @Test
    public void testGetPhotographerById_NotFound() throws Exception {
        try (MockedStatic<SQLiteConnector> mockedStatic = mockStatic(SQLiteConnector.class)) {
            mockedStatic.when(SQLiteConnector::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
            when(mockStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false);

            Photographer result = dao.getPhotographerById(99);
            assertNull(result);
        }
    }

    @Test
    public void testSetCheckedOutStatus() throws Exception {
        try (MockedStatic<SQLiteConnector> mockedStatic = mockStatic(SQLiteConnector.class)) {
            mockedStatic.when(SQLiteConnector::getConnection).thenReturn(mockConnection);
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);

            dao.setCheckedOutStatus(5, true);

            verify(mockStatement).setInt(1, 1);
            verify(mockStatement).setInt(2, 5);
            verify(mockStatement).executeUpdate();
        }
    }
}