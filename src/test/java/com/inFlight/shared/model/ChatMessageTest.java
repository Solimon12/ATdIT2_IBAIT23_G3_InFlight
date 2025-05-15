package com.inFlight.shared.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChatMessageTest {

    @Test
    void testConstructorAndGetters() {
        ChatMessage message = new ChatMessage("John", "Jane", "Hello!");

        assertEquals("John", message.getSender());
        assertEquals("Jane", message.getReceiver());
        assertEquals("Hello!", message.getContent());
    }

    @Test
    void testSetters() {
        ChatMessage message = new ChatMessage();

        message.setSender("John");
        message.setReceiver("Jane");
        message.setContent("How are you?");

        assertEquals("John", message.getSender());
        assertEquals("Jane", message.getReceiver());
        assertEquals("How are you?", message.getContent());
    }

    @Test
    void testEmptyConstructor() {
        ChatMessage message = new ChatMessage();

        assertNull(message.getSender());
        assertNull(message.getReceiver());
        assertNull(message.getContent());
    }
}