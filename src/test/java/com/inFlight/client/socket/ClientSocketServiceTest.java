package com.inFlight.client.socket;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.inFlight.client.socket.ClientSocketService;
import com.inFlight.shared.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ClientSocketServiceTest {

    private ClientSocketService service;
    private Socket mockSocket;
    private BufferedWriter mockWriter;
    private BufferedReader mockReader;

    @BeforeEach
    void setUp() throws Exception {
        service = new ClientSocketService();
        mockSocket = mock(Socket.class);
        mockWriter = mock(BufferedWriter.class);
        mockReader = mock(BufferedReader.class);

        setField("socket", mockSocket);
        setField("writer", mockWriter);
        setField("reader", mockReader);
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field field = ClientSocketService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, value);
    }

    @Test
    void testUpdateNovaCredits() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\"}");
        JsonObject response = service.updateNovaCredits(1, 100);
        assertEquals("OK", response.get("status").getAsString());
    }

    @Test
    void testSendChatMessage() throws IOException {
        ChatMessage msg = new ChatMessage("a", "b", "hi");
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\"}");
        JsonObject response = service.sendChatMessage(msg);
        assertEquals("OK", response.get("status").getAsString());
    }

    @Test
    void testGetChat() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\",\"messages\":[]}");
        List<ChatMessage> messages = service.getChat("a", "b");
        assertTrue(messages.isEmpty());
    }

    @Test
    void testSendBooking() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\"}");
        JsonObject response = service.sendBooking(1, 2, 1);
        assertEquals("OK", response.get("status").getAsString());
    }

    @Test
    void testCancelBooking() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\"}");
        JsonObject response = service.cancelBooking(1);
        assertEquals("OK", response.get("status").getAsString());
    }

    @Test
    void testGetBookings() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\",\"bookings\":[]}");
        List<Booking> bookings = service.getBookings();
        assertTrue(bookings.isEmpty());
    }

    @Test
    void testGetBookingsForPassenger() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\",\"bookings\":[]}");
        List<Booking> bookings = service.getBookingsForPassenger(1);
        assertTrue(bookings.isEmpty());
    }

    @Test
    void testGetSlotById() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\",\"slot\":{}}");
        SpacewalkSlot slot = service.getSlotById(1);
        assertNotNull(slot);
    }

    @Test
    void testSendBroadcast() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\"}");
        service.sendBroadcast("message");
    }

    @Test
    void testGetLatestBroadcast() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\"}");
        JsonObject obj = service.getLatestBroadcast();
        assertEquals("OK", obj.get("status").getAsString());
    }

    @Test
    void testTriggerSurvey() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\"}");
        service.triggerSurvey();
    }

    @Test
    void testIsSurveyTriggered() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"triggered\":true}");
        assertTrue(service.isSurveyTriggered());
    }

    @Test
    void testGetAllPassengers() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\",\"passengers\":[]}");
        List<Passenger> list = service.getAllPassengers();
        assertTrue(list.isEmpty());
    }

    @Test
    void testCheckOutPassenger() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\"}");
        service.checkOutPassenger(1);
    }

    @Test
    void testIsCheckedOut() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"checkedOut\":true}");
        assertTrue(service.isCheckedOut(1));
    }

    @Test
    void testSetCheckedOutStatus() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\"}");
        service.setCheckedOutStatus(1, true);
    }

    @Test
    void testRegisterPhotographer() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\"}");
        service.registerPhotographer("Sam");
    }

    @Test
    void testGetPhotographerByName() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\",\"photographer\":{\"name\":\"Sam\"}}");
        Photographer p = service.getPhotographerByName("Sam");
        assertNotNull(p);
    }

    @Test
    void testGetPhotographerById() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\",\"photographer\":{\"id\":1}}");
        Photographer p = service.getPhotographerById(1);
        assertNotNull(p);
    }

    @Test
    void testSetPhotographerCheckedOut() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\"}");
        service.setPhotographerCheckedOut(1, true);
    }

    @Test
    void testGetInventoryItemsByRole() throws Exception {
        JsonObject item = new JsonObject();
        item.addProperty("id", 1);
        item.addProperty("name", "Helmet");
        item.addProperty("condition", "Perfect. Approved by Ground Crew");
        item.addProperty("available", true);
        item.addProperty("ownerRole", "Attendant");
        item.addProperty("checkedOut", false);

        JsonArray array = new JsonArray();
        array.add(item);

        JsonObject response = new JsonObject();
        response.addProperty("status", "OK");
        response.add("items", array);

        when(mockReader.readLine()).thenReturn(new com.google.gson.Gson().toJson(response));

        List<InventoryItem> result = service.getInventoryItemsByRole("Attendant");

        assertEquals(1, result.size());
        InventoryItem actualItem = result.get(0);
        assertEquals("Helmet", actualItem.getName());
        assertEquals("Attendant", actualItem.getOwnerRole());
        assertTrue(actualItem.isAvailable());
    }

    @Test
    void testUpdateInventoryItem() throws Exception {
        InventoryItem item = new InventoryItem(2, "Camera", "Good", true, "Photographer", false);
        JsonObject response = new JsonObject();
        response.addProperty("status", "OK");

        when(mockReader.readLine()).thenReturn(new com.google.gson.Gson().toJson(response));

        assertDoesNotThrow(() -> service.updateInventoryItem(item));
    }

    @Test
    void testDisconnect() throws IOException {
        service.disconnect();
        verify(mockSocket).close();
    }

    @Test
    void testGetAvailableSlots() throws IOException {
        JsonObject slot = new JsonObject();
        slot.addProperty("slotId", 1);
        slot.addProperty("slotTime", "2025-06-01T10:00:00Z");
        slot.addProperty("available", true);

        JsonArray array = new JsonArray();
        array.add(slot);

        JsonObject response = new JsonObject();
        response.addProperty("status", "OK");
        response.add("slots", array);

        when(mockReader.readLine()).thenReturn(new com.google.gson.Gson().toJson(response));

        List<SpacewalkSlot> slots = service.getAvailableSlots();

        assertEquals(1, slots.size());
        SpacewalkSlot actual = slots.get(0);
        assertEquals(1, actual.getSlotId());
        assertEquals("2025-06-01T10:00:00Z", actual.getSlotTime());
        assertTrue(actual.isAvailable());
    }

    @Test
    void testApproveBooking() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\"}");

        assertDoesNotThrow(() -> service.approveBooking(123));
    }

    @Test
    void testDenyBooking() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\"}");

        assertDoesNotThrow(() -> service.denyBooking(123));
    }

    @Test
    void testGetPassengerById() throws IOException {
        JsonObject passenger = new JsonObject();
        passenger.addProperty("passenger_id", 1);
        passenger.addProperty("username", "astro_user");
        passenger.addProperty("password", "secret");
        passenger.addProperty("novaCredits", 150);

        JsonObject response = new JsonObject();
        response.addProperty("status", "OK");
        response.add("passenger", passenger);

        when(mockReader.readLine()).thenReturn(new com.google.gson.Gson().toJson(response));

        Passenger p = service.getPassengerById(1);

        assertNotNull(p);
        assertEquals("astro_user", p.getUsername());
        assertEquals(150, p.getNovaCredits());
    }

    @Test
    void testGetLastCancellation() throws IOException {
        when(mockReader.readLine()).thenReturn("{\"status\":\"OK\",\"bookingId\":9}");

        JsonObject result = service.getLastCancellation();

        assertEquals("OK", result.get("status").getAsString());
        assertEquals(9, result.get("bookingId").getAsInt());
    }

    @Test
    void testIsConnected_WhenConnected() throws IOException {
        when(mockSocket.isConnected()).thenReturn(true);
        assertTrue(service.isConnected());
    }

    @Test
    void testIsConnected_WhenDisconnected() {
        // Disconnect by setting socket and writer to null
        assertDoesNotThrow(() -> {
            setField("writer", null);
            setField("socket", null);
        });

        assertFalse(service.isConnected());
    }
}