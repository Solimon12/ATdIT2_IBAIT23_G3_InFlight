package com.inFlight.server.service;

import com.inFlight.server.dao.ChatMessageDAO;
import com.inFlight.shared.model.ChatMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    private ChatMessageDAO mockDAO;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        mockDAO = mock(ChatMessageDAO.class);
        chatService = new ChatService(mockDAO);
    }

    @Test
    void testSendMessageDelegatesToDAO() {
        ChatMessage message = new ChatMessage("alice", "bob", "Hello Bob!");

        chatService.sendMessage(message);

        verify(mockDAO, times(1)).saveMessage(message);
    }

    @Test
    void testGetChatHistoryReturnsMessages() {
        ChatMessage msg1 = new ChatMessage("alice", "bob", "Hi");
        ChatMessage msg2 = new ChatMessage("bob", "alice", "Hey");

        when(mockDAO.getChatBetween("alice", "bob")).thenReturn(List.of(msg1, msg2));

        List<ChatMessage> result = chatService.getChatHistory("alice", "bob");

        assertEquals(2, result.size());
        assertEquals("Hi", result.get(0).getContent());
        assertEquals("Hey", result.get(1).getContent());
    }

    @Test
    void testGetChatHistoryReturnsEmptyList() {
        when(mockDAO.getChatBetween("alice", "charlie")).thenReturn(List.of());

        List<ChatMessage> result = chatService.getChatHistory("alice", "charlie");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}