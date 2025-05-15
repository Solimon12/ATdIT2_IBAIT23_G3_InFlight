package com.inFlight.shared.protocol;

import com.google.gson.JsonObject; // Importing JsonObject from Gson library
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MessageEnvelope is a class that represents a message envelope used in the application.
 * It contains an action and data in JSON format.
 */
public class MessageEnvelope {
    private static final Logger logger = LoggerFactory.getLogger(MessageEnvelope.class);
    private String action; // The action to be performed
    private JsonObject data; // The data associated with the action

    /**
     * Default constructor for MessageEnvelope.
     */
    public MessageEnvelope() {}

    /**
     * Constructor for MessageEnvelope with action and data.
     *
     * @param action The action to be performed
     * @param data   The data associated with the action
     */
    public MessageEnvelope(String action, JsonObject data) {
        this.action = action;
        this.data = data;
    }

    /**
     * getter for action.
     * @return The action to be performed
     */
    public String getAction() {
        return action;
    }

    /**
     * getter for data.
     * @return The data associated with the action
     */
    public JsonObject getData() {
        return data;
    }

    /**
     * setter for action.
     * @param action The action to be performed
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * setter for data.
     * @param data The data associated with the action
     */
    public void setData(JsonObject data) {
        this.data = data;
    }
}
