package com.inFlight.server.socket;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.inFlight.server.service.BookingService;
import com.inFlight.server.service.ChatService;
import com.inFlight.server.dao.PassengerDAO;
import com.inFlight.server.dao.InventoryItemDAO;
import com.inFlight.server.dao.ChatMessageDAO;
import com.inFlight.server.dao.SpacewalkSlotDAO;
import com.inFlight.server.dao.BookingDAO;
import com.inFlight.server.service.PhotographerService;
import com.inFlight.server.service.InventoryService;
import com.inFlight.shared.model.*;
import com.inFlight.shared.state.BroadcastState;
import com.inFlight.shared.state.SurveyState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

class ProtocolHandlerTest {
    private ProtocolHandler protocolHandler;
    private BookingService bookingService;
    private ChatService chatService;
    private PassengerDAO passengerDAO;
    private SpacewalkSlotDAO slotDAO;
    private BookingDAO bookingDAO;
    private PhotographerService photographerService;
    private InventoryService inventoryService;
    private InventoryItemDAO inventoryDAO;
    private ChatMessageDAO chatMessageDAO;
    private Gson gson;

    @BeforeEach
    void setup() {
        bookingService = mock(BookingService.class);
        chatService = mock(ChatService.class);
        photographerService = mock(PhotographerService.class);
        inventoryService = mock(InventoryService.class);

        passengerDAO = mock(PassengerDAO.class);
        slotDAO = mock(SpacewalkSlotDAO.class);
        bookingDAO = mock(BookingDAO.class);
        inventoryDAO = mock(InventoryItemDAO.class);
        chatMessageDAO = mock(ChatMessageDAO.class);
        gson = new Gson();

        protocolHandler = new ProtocolHandler(bookingService, chatService, photographerService, inventoryService, passengerDAO, bookingDAO, slotDAO, inventoryDAO, chatMessageDAO);
        }

    @Test
    void testHandleLoginSuccess() {
        String message = "{\"action\": \"LOGIN\", \"data\": {\"username\": \"Guenther Geissen\"}}";

        // Corrected: Use the proper constructor that matches the Passenger class with password
        Passenger mockPassenger = new Passenger(1, "Guenther Geissen", "1337", 100, false);

        // Mocking the DAO call to return the mock passenger object
        when(passengerDAO.getPassengerByUsername("Guenther Geissen")).thenReturn(mockPassenger);

        String response = protocolHandler.handle(message);
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

        // Assertions to verify the success
        assertEquals("OK", jsonResponse.get("status").getAsString());
        assertNotNull(jsonResponse.get("passenger"));
    }

    @Test
    void testHandleLoginFailure() {
        String message = "{\"action\": \"LOGIN\", \"data\": {\"username\": \"unknown_user\"}}";
        when(passengerDAO.getPassengerByUsername("unknown_user")).thenReturn(null);

        String response = protocolHandler.handle(message);
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

        assertEquals("ERROR", jsonResponse.get("status").getAsString());
        assertEquals("Passenger not found", jsonResponse.get("message").getAsString());
    }

    @Test
    void testHandleUpdateNovaCreditsSuccess() {
        String message = "{\"action\": \"UPDATE_NOVACREDITS\", \"data\": {\"passengerId\": 1, \"amount\": 50}}";

        Passenger mockPassenger = new Passenger(1, "john_doe", "password123", 100);

        // Mocking the DAO call to return the mock passenger object
        when(passengerDAO.getPassengerById(1)).thenReturn(mockPassenger);

        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        // Assertions to verify the success
        assertEquals("OK", response.get("status").getAsString());
        // New balance should be updated to 150
        assertEquals(150, response.get("newBalance").getAsInt());
    }

    @Test
    void testHandleSendChat() {
        String message = "{\"action\": \"SEND_CHAT\", \"data\": {\"sender\": \"john_doe\", \"receiver\": \"jane_doe\", \"message\": \"Hello!\"}}";
        ChatMessage mockChatMessage = new ChatMessage("john_doe", "jane_doe", "Hello!");
        doNothing().when(chatService).sendMessage(mockChatMessage);

        String response = protocolHandler.handle(message);
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

        assertEquals("OK", jsonResponse.get("status").getAsString());
    }

    @Test
    void testHandleGetChat() {
        String message = "{\"action\": \"GET_CHAT\", \"data\": {\"sender\": \"john_doe\", \"receiver\": \"jane_doe\"}}";
        List<ChatMessage> mockMessages = Arrays.asList(new ChatMessage("john_doe", "jane_doe", "Hello!"));
        when(chatService.getChatHistory("john_doe", "jane_doe")).thenReturn(mockMessages);

        String response = protocolHandler.handle(message);
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

        assertEquals("OK", jsonResponse.get("status").getAsString());
        assertNotNull(jsonResponse.get("messages"));
    }

    @Test
    void testHandleApproveBooking() {
        String message = "{\"action\": \"APPROVE_BOOKING\", \"data\": {\"bookingId\": 1}}";
        doNothing().when(bookingService).setBookingStatus(1, "approved");

        String response = protocolHandler.handle(message);
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

        assertEquals("OK", jsonResponse.get("status").getAsString());
    }

    @Test
    void testHandleException() {
        String message = "{\"action\": \"LOGIN\", \"data\": {\"username\": \"invalid\"}}";
        when(passengerDAO.getPassengerByUsername(anyString())).thenThrow(new RuntimeException("Database error"));

        String response = protocolHandler.handle(message);
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

        assertEquals("ERROR", jsonResponse.get("status").getAsString());
        assertEquals("Failed to parse or handle request.", jsonResponse.get("message").getAsString());
    }

    @Test
    void testHandleGetPassengerByIdSuccess() {
        String message = "{\"action\": \"GET_PASSENGER_BY_ID\", \"data\": {\"passengerId\": 1}}";

        Passenger mockPassenger = new Passenger(1, "john_doe", "password123", 150);
        when(passengerDAO.getPassengerById(1)).thenReturn(mockPassenger);

        String response = protocolHandler.handle(message);
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

        assertEquals("OK", jsonResponse.get("status").getAsString());
        assertNotNull(jsonResponse.get("passenger"));
        JsonObject passengerJson = jsonResponse.getAsJsonObject("passenger");
        assertEquals("john_doe", passengerJson.get("username").getAsString());
        assertEquals(150, passengerJson.get("novaCredits").getAsInt());
    }

    @Test
    void testHandleGetPassengerByIdNotFound() {
        String message = "{\"action\": \"GET_PASSENGER_BY_ID\", \"data\": {\"passengerId\": 99}}";

        when(passengerDAO.getPassengerById(99)).thenReturn(null);

        String response = protocolHandler.handle(message);
        JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

        assertEquals("ERROR", jsonResponse.get("status").getAsString());
        assertFalse(jsonResponse.has("passenger"));
    }

    @Test
    void testHandleTriggerBroadcast() {
        String message = "{\"action\": \"TRIGGER_BROADCAST\", \"data\": {\"message\": \"This is a test\"}}";

        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);
        assertEquals("OK", response.get("status").getAsString());
        assertEquals("This is a test", BroadcastState.getMessage());
    }

    @Test
    void testHandleGetBroadcast() {
        BroadcastState.setMessage("Test broadcast");

        String message = "{\"action\": \"GET_BROADCAST\", \"data\": {}}";
        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        assertEquals("OK", response.get("status").getAsString());
        assertEquals("Test broadcast", response.get("message").getAsString());
        assertNotNull(response.get("timestamp"));
    }

    @Test
    void testHandleTriggerSurvey() {
        String message = "{\"action\": \"TRIGGER_SURVEY\", \"data\": {}}";
        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        assertEquals("OK", response.get("status").getAsString());
        assertTrue(SurveyState.isSurveyTriggered());
    }

    @Test
    void testHandleCheckSurveyTrigger() {
        SurveyState.triggerSurvey();

        String message = "{\"action\": \"CHECK_SURVEY_TRIGGER\", \"data\": {}}";
        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        assertTrue(response.get("triggered").getAsBoolean());
    }

    @Test
    void testHandleGetAllPassengers() {
        List<Passenger> passengers = Arrays.asList(
                new Passenger(1, "Alice", "pw", 10, false),
                new Passenger(2, "Bob", "pw", 20, true)
        );
        when(passengerDAO.getAllPassengers()).thenReturn(passengers);

        String message = "{\"action\": \"GET_ALL_PASSENGERS\", \"data\": {}}";
        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        assertEquals("OK", response.get("status").getAsString());
        assertTrue(response.has("passengers"));
    }

    @Test
    void testHandleCheckOutPassenger() {
        String message = "{\"action\": \"CHECK_OUT_PASSENGER\", \"data\": {\"passengerId\": 1}}";

        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        verify(passengerDAO).setCheckedOut(1, true);
        assertEquals("OK", response.get("status").getAsString());
    }

    @Test
    void testHandleCheckCheckoutStatus() {
        Passenger mockPassenger = new Passenger(1, "Alice", "pw", 10, true);
        when(passengerDAO.getPassengerById(1)).thenReturn(mockPassenger);

        String message = "{\"action\": \"CHECK_CHECKOUT_STATUS\", \"data\": {\"passengerId\": 1}}";
        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        assertTrue(response.get("checkedOut").getAsBoolean());
    }

    @Test
    void testHandleSetCheckedOutStatus() {
        String message = "{\"action\": \"SET_CHECKED_OUT_STATUS\", \"data\": {\"passengerId\": 1, \"checkedOut\": false}}";

        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        verify(passengerDAO).setCheckedOut(1, false);
        assertEquals("OK", response.get("status").getAsString());
    }

    @Test
    void testHandleRegisterPhotographer() {
        String message = "{\"action\": \"REGISTER_PHOTOGRAPHER\", \"data\": {\"name\": \"John\"}}";

        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        verify(photographerService).registerPhotographer("John");
        assertEquals("OK", response.get("status").getAsString());
    }

    @Test
    void testHandleGetPhotographerByNameFound() {
        Photographer photo = new Photographer(1, "John", false);
        when(photographerService.getByName("John")).thenReturn(photo);

        String message = "{\"action\": \"GET_PHOTOGRAPHER_BY_NAME\", \"data\": {\"name\": \"John\"}}";
        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        assertEquals("OK", response.get("status").getAsString());
        assertNotNull(response.get("photographer"));
    }

    @Test
    void testHandleGetPhotographerByNameNotFound() {
        when(photographerService.getByName("Unknown")).thenReturn(null);

        String message = "{\"action\": \"GET_PHOTOGRAPHER_BY_NAME\", \"data\": {\"name\": \"Unknown\"}}";
        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        assertEquals("NOT_FOUND", response.get("status").getAsString());
    }

    @Test
    void testHandleGetInventoryByRole() {
        List<InventoryItem> items = Arrays.asList(new InventoryItem("Helmet", "Good", true, "crew"));
        when(inventoryService.getItemsByRole("crew")).thenReturn(items);

        String message = "{\"action\": \"GET_INVENTORY_BY_ROLE\", \"data\": {\"role\": \"crew\"}}";
        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        assertEquals("OK", response.get("status").getAsString());
        assertTrue(response.has("items"));
    }

    @Test
    void testHandleUpdateInventoryItem() {
        InventoryItem item = new InventoryItem("Helmet", "Good", true, "crew");
        String message = "{\"action\": \"UPDATE_INVENTORY_ITEM\", \"data\": {\"item\": " + gson.toJson(item) + "}}";

        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        verify(inventoryService).updateItem(any(InventoryItem.class));
        assertEquals("OK", response.get("status").getAsString());
    }

    @Test
    void testHandleGetPhotographerByIdFound() {
        Photographer mockPhotographer = new Photographer(1, "Yu", false);
        when(photographerService.getById(1)).thenReturn(mockPhotographer);

        String message = "{\"action\": \"GET_PHOTOGRAPHER_BY_ID\", \"data\": {\"id\": 1}}";
        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        assertEquals("OK", response.get("status").getAsString());
        assertTrue(response.has("photographer"));
        JsonObject photographer = response.getAsJsonObject("photographer");
        assertEquals("Yu", photographer.get("name").getAsString());
    }

    @Test
    void testHandleGetPhotographerByIdNotFound() {
        when(photographerService.getById(999)).thenReturn(null);

        String message = "{\"action\": \"GET_PHOTOGRAPHER_BY_ID\", \"data\": {\"id\": 999}}";
        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        assertEquals("NOT_FOUND", response.get("status").getAsString());
        assertFalse(response.has("photographer"));
    }

    @Test
    void testHandleSetPhotographerCheckedOut() {
        String message = "{\"action\": \"SET_PHOTOGRAPHER_CHECKED_OUT\", \"data\": {\"id\": 1, \"checkedOut\": true}}";

        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        verify(photographerService).setCheckedOut(1, true);
        assertEquals("OK", response.get("status").getAsString());
    }

    @Test
    void testHandleGetBookingsForPassenger() {
        int passengerId = 1;
        List<Booking> mockBookings = Arrays.asList(
                new Booking(101, passengerId, 1, 1, "approved"),
                new Booking(102, passengerId, 2, 1, "approved")
        );
        when(bookingService.getBookingsForPassenger(passengerId)).thenReturn(mockBookings);

        String message = "{\"action\": \"GET_BOOKINGS_FOR_PASSENGER\", \"data\": {\"passengerId\": 1}}";
        JsonObject response = gson.fromJson(protocolHandler.handle(message), JsonObject.class);

        assertEquals("OK", response.get("status").getAsString());
        assertTrue(response.has("bookings"));

        JsonArray bookings = response.getAsJsonArray("bookings");
        assertEquals(2, bookings.size());
        assertEquals(101, bookings.get(0).getAsJsonObject().get("bookingId").getAsInt());
        assertEquals(102, bookings.get(1).getAsJsonObject().get("bookingId").getAsInt());
    }
}