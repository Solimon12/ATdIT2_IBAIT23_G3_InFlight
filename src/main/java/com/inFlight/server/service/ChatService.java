package com.inFlight.server.service;

import com.inFlight.server.dao.ChatMessageDAO;
import com.inFlight.shared.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ChatService is a service class that handles chat-related operations.
 * It interacts with the ChatMessageDAO to manage chat messages.
 */
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ChatMessageDAO dao; // DAO for chat message operations

    /**
     * Default constructor initializes the DAO.
     */
    public ChatService () {
        dao = new ChatMessageDAO();
    }

    /**
     * Constructor for dependency injection
     */
    public ChatService (ChatMessageDAO dao) {
        this.dao = dao;
    }

    /**
     * Sends a chat message by saving it to the database.
     *
     * @param msg the ChatMessage object to send
     */
    public void sendMessage(ChatMessage msg) {
        dao.saveMessage(msg);
    }

    /**
     * Retrieves the chat history between two users.
     *
     * @param sender   the sender's username
     * @param receiver the receiver's username
     * @return a list of ChatMessage objects representing the chat history between the two users
     */
    public List<ChatMessage> getChatHistory(String sender, String receiver) {
        return dao.getChatBetween(sender, receiver);
    }
}
