package com.inFlight.client.socket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inFlight.shared.model.*;
import com.inFlight.shared.protocol.MessageEnvelope;
import com.google.gson.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * ClientSocketService is a service class that handles communication with the server using sockets.
 * It provides methods to send requests and receive responses from the server.
 */
public class ClientSocketService {
    private static final Logger logger = LoggerFactory.getLogger(ClientSocketService.class);

    private static final String HOST = "localhost"; // The server's host address
    private static final int PORT = 5555; // The server's port number

    private Socket socket; // Socket for communication with the server
    private BufferedWriter writer; // Output stream for sending data to the server
    private BufferedReader reader; // Input stream for receiving data from the server
    private final Gson gson = new Gson(); // Gson instance for JSON serialization/deserialization

    /**
     * connect method establishes a connection to the server.
     * It creates a socket and initializes input/output streams for communication.
     */
    public boolean connect() {
        try {
            socket = new Socket(HOST, PORT); // Create a socket to connect to the server
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // Output stream for sending data
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Input stream for receiving data
            return true;
        } catch (IOException e) {
            // Handle exceptions related to socket operations
            logger.error("Failed to connect to server.", e);
            return false;
        }
    }

    /**
     * send method sends a request to the server and receives a response.
     * It serializes the request data to JSON format and deserializes the response data from JSON format.
     *
     * @param action The action to be performed
     * @param data   The data associated with the action
     * @return The response from the server as a JsonObject
     */
    public JsonObject send(String action, JsonObject data) {
        try {
            MessageEnvelope request = new MessageEnvelope(action, data);
            String json = gson.toJson(request);

            writer.write(json);
            writer.newLine();
            writer.flush();

            String responseJson = reader.readLine();
            return gson.fromJson(responseJson, JsonObject.class);

        } catch (IOException e) {
            JsonObject error = new JsonObject();
            error.addProperty("status", "ERROR");
            error.addProperty("message", "Failed to send request to server.");
            logger.error("Failed to send request to server.", e);
            return error;
        }
    }

    /**
     * updateNovaCredits method updates the Nova credits for a passenger.
     *
     * @param passengerId The ID of the passenger
     * @param amount      The amount to update
     * @return The response from the server as a JsonObject
     */
    public JsonObject updateNovaCredits(int passengerId, int amount) {
        JsonObject data = new JsonObject();
        data.addProperty("passengerId", passengerId);
        data.addProperty("amount", amount);
        return send("UPDATE_NOVACREDITS", data);
    }

    /**
     * sendChatMessage method sends a chat message to the server.
     *
     * @param message
     * @return The response from the server as a JsonObject
     */
    public JsonObject sendChatMessage(ChatMessage message) {
        JsonObject data = gson.toJsonTree(message).getAsJsonObject();
        return send("SEND_CHAT", data);
    }

    /**
     * getChat method retrieves the chat messages between two users.
     *
     * @param sender   The sender's username
     * @param receiver The receiver's username
     * @return A list of chat messages
     */
    public List<ChatMessage> getChat(String sender, String receiver) {
        JsonObject data = new JsonObject();
        data.addProperty("sender", sender);
        data.addProperty("receiver", receiver);

        JsonObject response = send("GET_CHAT", data);
        List<ChatMessage> messages = new ArrayList<>();

        if ("OK".equals(response.get("status").getAsString())) {
            JsonArray array = response.getAsJsonArray("messages");
            for (var element : array) {
                ChatMessage msg = gson.fromJson(element, ChatMessage.class);
                messages.add(msg);
            }
        }

        return messages;
    }

    /**
     * getAvailableSlots method retrieves the available spacewalk slots from the server.
     *
     * @return A list of available spacewalk slots
     */
    public List<SpacewalkSlot> getAvailableSlots() {
        JsonObject response = send("GET_AVAILABLE_SLOTS", new JsonObject());
        List<SpacewalkSlot> slots = new ArrayList<>();

        if ("OK".equals(response.get("status").getAsString())) {
            JsonArray array = response.getAsJsonArray("slots");
            for (JsonElement element : array) {
                slots.add(gson.fromJson(element, SpacewalkSlot.class));
            }
        }

        return slots;
    }

    /**
     * sendBooking method sends a booking request for a spacewalk slot.
     *
     * @param passengerId The ID of the passenger
     * @param slotId      The ID of the spacewalk slot
     * @param tier        The tier of the booking
     * @return The response from the server as a JsonObject
     */
    public JsonObject sendBooking(int passengerId, int slotId, int tier) {
        JsonObject data = new JsonObject();
        data.addProperty("passengerId", passengerId);
        data.addProperty("slotId", slotId);
        data.addProperty("tier", tier);
        return send("BOOK_SLOT", data);
    }

    /**
     * getBookingsForPassenger method retrieves the bookings for a specific passenger.
     *
     * @param passengerId The ID of the passenger
     * @return A list of bookings for the passenger
     */
    public List<Booking> getBookingsForPassenger(int passengerId) {
        JsonObject data = new JsonObject();
        data.addProperty("passengerId", passengerId);

        JsonObject response = send("GET_BOOKINGS_FOR_PASSENGER", data);
        List<Booking> result = new ArrayList<>();

        if ("OK".equals(response.get("status").getAsString())) {
            JsonArray array = response.getAsJsonArray("bookings");
            for (JsonElement e : array) {
                result.add(gson.fromJson(e, Booking.class));
            }
        }

        return result;
    }

    /**
     * getSlotById method retrieves a spacewalk slot by its ID.
     *
     * @param slotId The ID of the spacewalk slot
     * @return The SpacewalkSlot object if found, null otherwise
     */
    public SpacewalkSlot getSlotById(int slotId) {
        JsonObject data = new JsonObject();
        data.addProperty("slotId", slotId);

        JsonObject response = send("GET_SLOT_BY_ID", data);
        if ("OK".equals(response.get("status").getAsString())) {
            return gson.fromJson(response.get("slot"), SpacewalkSlot.class);
        }
        return null;
    }

    /**
     * cancelBooking method cancels a booking by its ID.
     *
     * @param bookingId The ID of the booking to cancel
     * @return The response from the server as a JsonObject
     */
    public JsonObject cancelBooking(int bookingId) {
        JsonObject data = new JsonObject();
        data.addProperty("bookingId", bookingId);
        return send("CANCEL_BOOKING", data);
    }

    /**
     * getBookings method retrieves all bookings from the server.
     *
     * @return A list of all bookings
     */
    public List<Booking> getBookings() {
        JsonObject response = send("GET_BOOKINGS", new JsonObject());
        List<Booking> bookings = new ArrayList<>();

        if ("OK".equals(response.get("status").getAsString())) {
            JsonArray array = response.getAsJsonArray("bookings");
            for (JsonElement element : array) {
                bookings.add(gson.fromJson(element, Booking.class));
            }
        }

        return bookings;
    }

    /**
     * approveBooking method approves a booking by its ID.
     *
     * @param bookingId The ID of the booking to approve
     */
    public void approveBooking(int bookingId) {
        JsonObject data = new JsonObject();
        data.addProperty("bookingId", bookingId);
        send("APPROVE_BOOKING", data);
    }

    /**
     * denyBooking method denies a booking by its ID.
     *
     * @param bookingId The ID of the booking to deny
     */
    public void denyBooking(int bookingId) {
        JsonObject data = new JsonObject();
        data.addProperty("bookingId", bookingId);
        send("DENY_BOOKING", data);
    }

    /**
     * getPassengerById method retrieves a passenger by their ID.
     *
     * @param passengerId The ID of the passenger
     * @return The Passenger object if found, null otherwise
     */
    public Passenger getPassengerById(int passengerId) {
        JsonObject data = new JsonObject();
        data.addProperty("passengerId", passengerId);
        JsonObject response = send("GET_PASSENGER_BY_ID", data);

        if ("OK".equals(response.get("status").getAsString())) {
            return gson.fromJson(response.get("passenger"), Passenger.class);
        }
        return null;
    }

    /**
     * sendBroadcast method sends a broadcast message to all passengers.
     *
     * @param message The message to broadcast
     */
    public void sendBroadcast(String message) {
        JsonObject data = new JsonObject();
        data.addProperty("message", message);
        send("TRIGGER_BROADCAST", data);
    }

    /**
     * getLatestBroadcast method retrieves the latest broadcast message.
     *
     * @return The latest broadcast message as a JsonObject
     */
    public JsonObject getLatestBroadcast() {
        return send("GET_BROADCAST", new JsonObject());
    }

    /**
     * getLastCancellation method retrieves the last cancellation message.
     *
     * @return The last cancellation message as a JsonObject
     */
    public JsonObject getLastCancellation() {
        return send("GET_LAST_CANCELLATION", new JsonObject());
    }

    /**
     * triggerSurvey method triggers a survey for passengers.
     */
    public void triggerSurvey() {
        send("TRIGGER_SURVEY", new JsonObject());
    }

    /**
     * isSurveyTriggered method checks if a survey has been triggered.
     *
     * @return true if a survey has been triggered, false otherwise
     */
    public boolean isSurveyTriggered() {
        JsonObject response = send("CHECK_SURVEY_TRIGGER", new JsonObject());
        return response.has("triggered") && response.get("triggered").getAsBoolean();
    }

    /**
     * getAllPassengers method retrieves all passengers from the server.
     *
     * @return A list of all passengers
     */
    public List<Passenger> getAllPassengers() {
        JsonObject response = send("GET_ALL_PASSENGERS", new JsonObject());
        List<Passenger> passengers = new ArrayList<>();

        if ("OK".equals(response.get("status").getAsString())) {
            JsonArray array = response.getAsJsonArray("passengers");
            for (JsonElement element : array) {
                passengers.add(gson.fromJson(element, Passenger.class));
            }
        }

        return passengers;
    }

    /**
     * checkOutPassenger method checks out a passenger by their ID.
     *
     * @param passengerId The ID of the passenger to check out
     */
    public void checkOutPassenger(int passengerId) {
        JsonObject data = new JsonObject();
        data.addProperty("passengerId", passengerId);
        send("CHECK_OUT_PASSENGER", data);
    }

    /**
     * isCheckedOut method checks if a passenger has checked out.
     *
     * @param passengerId The ID of the passenger
     * @return true if the passenger has checked out, false otherwise
     */
    public boolean isCheckedOut(int passengerId) {
        JsonObject data = new JsonObject();
        data.addProperty("passengerId", passengerId);
        JsonObject response = send("CHECK_CHECKOUT_STATUS", data);
        return response.has("checkedOut") && response.get("checkedOut").getAsBoolean();
    }

    /**
     * setCheckedOutStatus method sets the checked out status for a passenger.
     *
     * @param passengerId The ID of the passenger
     * @param status      The checked out status to set
     */
    public void setCheckedOutStatus(int passengerId, boolean status) {
        JsonObject data = new JsonObject();
        data.addProperty("passengerId", passengerId);
        data.addProperty("checkedOut", status);
        send("SET_CHECKED_OUT_STATUS", data);
    }

    /**
     * registerPhotographer method registers a new photographer.
     *
     * @param name The name of the photographer
     */
    public void registerPhotographer(String name) {
        JsonObject data = new JsonObject();
        data.addProperty("name", name);
        send("REGISTER_PHOTOGRAPHER", data);
    }

    /**
     * getPhotographerByName method retrieves a photographer by their name.
     *
     * @param name The name of the photographer
     * @return The Photographer object if found, null otherwise
     */
    public Photographer getPhotographerByName(String name) {
        JsonObject data = new JsonObject();
        data.addProperty("name", name);

        JsonObject response = send("GET_PHOTOGRAPHER_BY_NAME", data);
        if ("OK".equals(response.get("status").getAsString())) {
            return gson.fromJson(response.get("photographer"), Photographer.class);
        }
        return null;
    }

    /**
     * getPhotographerById method retrieves a photographer by their ID.
     *
     * @param id The ID of the photographer
     * @return The Photographer object if found, null otherwise
     */
    public Photographer getPhotographerById(int id) {
        JsonObject data = new JsonObject();
        data.addProperty("id", id);

        JsonObject response = send("GET_PHOTOGRAPHER_BY_ID", data);
        if ("OK".equals(response.get("status").getAsString())) {
            return gson.fromJson(response.get("photographer"), Photographer.class);
        }
        return null;
    }

    /**
     * setPhotographerCheckedOut method sets the checked out status for a photographer.
     *
     * @param id        The ID of the photographer
     * @param checkedOut The checked out status to set
     */
    public void setPhotographerCheckedOut(int id, boolean checkedOut) {
        JsonObject data = new JsonObject();
        data.addProperty("id", id);
        data.addProperty("checkedOut", checkedOut);
        send("SET_PHOTOGRAPHER_CHECKED_OUT", data);
    }

    /**
     * getInventoryItemsByRole method retrieves inventory items by their role.
     *
     * @param role The role of the inventory items
     * @return A list of inventory items for the specified role
     */
    public List<InventoryItem> getInventoryItemsByRole(String role) {
        JsonObject data = new JsonObject();
        data.addProperty("role", role);

        JsonObject response = send("GET_INVENTORY_BY_ROLE", data);
        List<InventoryItem> items = new ArrayList<>();

        if ("OK".equals(response.get("status").getAsString())) {
            JsonArray array = response.getAsJsonArray("items");
            for (JsonElement element : array) {
                items.add(gson.fromJson(element, InventoryItem.class));
            }
        }

        return items;
    }

    /**
     * updateInventoryItem method updates an inventory item.
     * @param item The InventoryItem object to update
     */
    public void updateInventoryItem(InventoryItem item) {
        JsonObject data = new JsonObject();
        data.add("item", gson.toJsonTree(item));
        send("UPDATE_INVENTORY_ITEM", data);
    }

    /**
     * isConnected method checks if the client is connected to the server.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return writer != null && socket != null && socket.isConnected();
    }

    /**
     * disconnect method closes the socket connection to the server.
     * It is important to call this method when the client is done using the service.
     */
    public void disconnect() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // Handle exceptions related to socket operations
            logger.error("Failed to disconnect from server.", e);
        }
    }
}