package com.inFlight.server.dao;

import com.inFlight.server.db.SQLiteConnector;
import com.inFlight.shared.model.ChatMessage;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatMessageDAOTest {
    private ChatMessageDAO dao;
    private Connection mockConn;
    private PreparedStatement mockStmt;
    private ResultSet mockRs;
    private MockedStatic<SQLiteConnector> mockConnector;

    @BeforeEach
    void setUp() throws Exception {
        dao = new ChatMessageDAO();
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
    void testSaveMessageSuccess() throws Exception {
        ChatMessage message = new ChatMessage("Alice", "Bob", "Hello Bob!");
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);

        dao.saveMessage(message);

        verify(mockStmt).setString(1, "Alice");
        verify(mockStmt).setString(2, "Bob");
        verify(mockStmt).setString(3, "Hello Bob!");
        verify(mockStmt).executeUpdate();
    }

    @Test
    void testGetChatBetweenSuccess() throws Exception {
        when(mockConn.prepareStatement(anyString())).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockRs);

        when(mockRs.next()).thenReturn(true, true, false); // zwei Nachrichten
        when(mockRs.getString("sender")).thenReturn("Alice", "Bob");
        when(mockRs.getString("receiver")).thenReturn("Bob", "Alice");
        when(mockRs.getString("content")).thenReturn("Hi Bob", "Hi Alice");

        List<ChatMessage> result = dao.getChatBetween("Alice", "Bob");

        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getSender());
        assertEquals("Hi Bob", result.get(0).getContent());
        assertEquals("Bob", result.get(1).getSender());
        assertEquals("Hi Alice", result.get(1).getContent());
    }
}