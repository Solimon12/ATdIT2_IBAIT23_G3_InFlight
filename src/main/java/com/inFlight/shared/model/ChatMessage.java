package com.inFlight.shared.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ChatMessage is a class that represents a chat message in the application.
 * It contains information about the sender, receiver, and content of the message.
 * POJO (Plain Old Java Object) class.
 */
public class ChatMessage {
    private static final Logger logger = LoggerFactory.getLogger(ChatMessage.class);
    private String sender; // Sender of the message
    private String receiver; // Receiver of the message
    private String content; // Content of the message


    /**
     * Constructor without parameters
     * This constructor is used to create a ChatMessage object without any initial values.
     * It is typically used for serialization/deserialization purposes.
     */
    public ChatMessage() {

    }

    /**
     * Constructor to create a ChatMessage object with specified sender, receiver, and content.
     *
     * @param sender   the sender of the message
     * @param receiver the receiver of the message
     * @param content  the content of the message
     */
    public ChatMessage(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    /**
     * getter for sender
     * @return the sender of the message
     */
    public String getSender() {
        return sender;
    }

    /**
     * getter for receiver
     * @return the receiver of the message
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * getter for content
     * @return the content of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * setter for sender
     * @param sender the sender of the message
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * setter for receiver
     * @param receiver the receiver of the message
     */
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    /**
     * setter for content
     * @param content the content of the message
     */
    public void setContent(String content) {
        this.content = content;
    }
}

