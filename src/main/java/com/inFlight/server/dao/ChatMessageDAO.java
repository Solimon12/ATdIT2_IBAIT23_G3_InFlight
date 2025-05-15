package com.inFlight.server.dao;

import com.inFlight.shared.model.ChatMessage;
import com.inFlight.server.db.SQLiteConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ChatMessageDAO is a Data Access Object (DAO) class that provides methods to interact with the chat_message table in the SQLite database.
 * It includes methods to save and retrieve chat messages.
 */
public class ChatMessageDAO {
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageDAO.class);

    /**
     * Saves a chat message to the database.
     *
     * @param msg the ChatMessage object to save
     */
    public void saveMessage(ChatMessage msg) {
        try (Connection conn = SQLiteConnector.getConnection()) {
            String sql = "INSERT INTO chat_message (sender, receiver, content) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, msg.getSender());
            stmt.setString(2, msg.getReceiver());
            stmt.setString(3, msg.getContent());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Failed to save chat message", e);
        }
    }

    /**
     * Retrieves all chat messages between two users from the database.
     *
     * @param sender   the sender's username
     * @param receiver the receiver's username
     * @return a list of ChatMessage objects representing the chat messages between the two users
     */
    public List<ChatMessage> getChatBetween(String sender, String receiver) {
        List<ChatMessage> messages = new ArrayList<>();

        try (Connection conn = SQLiteConnector.getConnection()) {
            String sql = """
                SELECT sender, receiver, content 
                FROM chat_message
                WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)
                ORDER BY id ASC
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, sender);
            stmt.setString(2, receiver);
            stmt.setString(3, receiver);
            stmt.setString(4, sender);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(new ChatMessage(
                        rs.getString("sender"),
                        rs.getString("receiver"),
                        rs.getString("content")
                ));
            }
        } catch (SQLException e) {
            logger.error("Failed to load chat messages", e);
        }

        return messages;
    }
}

