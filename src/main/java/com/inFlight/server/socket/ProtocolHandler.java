package com.inFlight.server.socket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.inFlight.server.service.*;
import com.inFlight.shared.model.*;
import com.inFlight.shared.protocol.MessageEnvelope;
import com.inFlight.shared.protocol.ActionType;
import com.inFlight.shared.state.BroadcastState;
import com.inFlight.shared.state.CancellationState;
import com.inFlight.shared.state.SurveyState;

import com.inFlight.server.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ProtocolHandler is a class that handles incoming messages and performs actions based on the action type.
 * It uses the corresponding services to process requests and return responses.
 */
public class ProtocolHandler {
    private static final Logger logger = LoggerFactory.getLogger(ProtocolHandler.class); // Logger

    private final Gson gson = new Gson(); // Gson instance for JSON serialization/deserialization
    private final BookingService bookingService; // Service for booking-related operations
    private final ChatService chatService; // Service for chat-related operations
    private final PhotographerService photographerService; // Service for photographer-related operations
    private final InventoryService inventoryService; // Service for inventory-related operations
    private final PassengerService passengerService = new PassengerService(); // Service for passenger-related operations
    private final PilotService pilotService = new PilotService(); // Service for pilot-related operations

    private final PassengerDAO passengerDAO;
    private final BookingDAO BookingDAO;
    private final SpacewalkSlotDAO SpacewalkSlotDAO;
    private final InventoryItemDAO inventoryItemDAO;
    private final ChatMessageDAO chatMessageDAO;
    private final BookingDAO bookingDAO;


    public ProtocolHandler () {
        this.bookingService = new BookingService();
        this.chatService = new ChatService();
        this.photographerService = new PhotographerService();
        this.inventoryService = new InventoryService();

        this.passengerDAO = new PassengerDAO();
        this.BookingDAO = new BookingDAO();
        this.SpacewalkSlotDAO = new SpacewalkSlotDAO();
        this.inventoryItemDAO = new InventoryItemDAO();
        this.chatMessageDAO = new ChatMessageDAO();
        this.bookingDAO = new BookingDAO();
    }

    /**
     * Constructor for dependency injection.
     */
    public ProtocolHandler (BookingService bookingService, ChatService chatService, PhotographerService photographerService, InventoryService inventoryService, PassengerDAO passengerDAO, BookingDAO bookingDAO, SpacewalkSlotDAO spacewalkSlotDAO, InventoryItemDAO inventoryItemDAO, ChatMessageDAO chatMessageDAO) {
        this.bookingService = bookingService;
        this.chatService = chatService;
        this.photographerService = photographerService;
        this.inventoryService = inventoryService;

        this.passengerDAO = passengerDAO;
        this.BookingDAO = bookingDAO;
        this.SpacewalkSlotDAO = spacewalkSlotDAO;
        this.inventoryItemDAO = inventoryItemDAO;
        this.chatMessageDAO = chatMessageDAO;
        this.bookingDAO = bookingDAO;
    }


    /**
     * Handles incoming messages and performs actions based on the action type.
     *
     * @param message the incoming message in JSON format
     * @return a JSON response based on the action performed
     */
    public String handle(String message) {
       // Parse the incoming message
        try {
            MessageEnvelope envelope = gson.fromJson(message, MessageEnvelope.class); // Deserialize the message into a MessageEnvelope object
            ActionType action = ActionType.valueOf(envelope.getAction()); // Get the action type from the envelope
            JsonObject data = envelope.getData(); // Get the data associated with the action

            // Perform actions based on the action type
            switch (action) {
                //
                case LOGIN:
                    //
                    String username = data.get("username").getAsString();
                    Passenger p = passengerDAO.getPassengerByUsername(username);
                    JsonObject response = new JsonObject();
                    if (p != null) {
                        response.addProperty("status", "OK");
                        response.add("passenger", gson.toJsonTree(p));
                    } else {
                        response.addProperty("status", "ERROR");
                        response.addProperty("message", "Passenger not found");
                    }
                    return gson.toJson(response);

                case UPDATE_NOVACREDITS:
                    //
                    int passengerId = data.get("passengerId").getAsInt();
                    int amount = data.get("amount").getAsInt();

                    Passenger passenger = passengerDAO.getPassengerById(passengerId);
                    JsonObject updateResponse = new JsonObject();

                    if (passenger != null) {
                        int newBalance = passenger.getNovaCredits() + amount;
                        passengerDAO.updateNovaCredits(passengerId, newBalance);

                        updateResponse.addProperty("status", "OK");
                        updateResponse.addProperty("newBalance", newBalance);
                    } else {
                        updateResponse.addProperty("status", "ERROR");
                        updateResponse.addProperty("message", "Passenger not found.");
                    }
                    return gson.toJson(updateResponse);

                case SEND_CHAT:
                    //
                    ChatMessage ChatMessage = gson.fromJson(data, ChatMessage.class);
                    chatService.sendMessage(ChatMessage);

                    JsonObject sendResponse = new JsonObject();
                    sendResponse.addProperty("status", "OK");
                    return gson.toJson(sendResponse);

                case GET_CHAT:
                    //
                    String sender = data.get("sender").getAsString();
                    String receiver = data.get("receiver").getAsString();

                    List<ChatMessage> history = chatService.getChatHistory(sender, receiver);

                    JsonObject chatResponse = new JsonObject();
                    chatResponse.addProperty("status", "OK");
                    chatResponse.add("messages", gson.toJsonTree(history));
                    return gson.toJson(chatResponse);

                case GET_AVAILABLE_SLOTS:
                    //
                    List<SpacewalkSlot> slots = SpacewalkSlotDAO.getAvailableSlots();

                    JsonObject slotResponse = new JsonObject();
                    slotResponse.addProperty("status", "OK");
                    slotResponse.add("slots", gson.toJsonTree(slots));
                    return gson.toJson(slotResponse);

                case BOOK_SLOT:
                    //
                    int passengerIdBook = data.get("passengerId").getAsInt();
                    int slotId = data.get("slotId").getAsInt();
                    int tier = data.get("tier").getAsInt();

                    JsonObject bookingResult = bookingService.processBooking(passengerIdBook, slotId, tier);
                    return gson.toJson(bookingResult);

                case GET_BOOKINGS_FOR_PASSENGER:
                    //
                    int pid = data.get("passengerId").getAsInt();
                    List<Booking> bookings = bookingService.getBookingsForPassenger(pid);

                    JsonObject responseB = new JsonObject();
                    responseB.addProperty("status", "OK");
                    responseB.add("bookings", gson.toJsonTree(bookings));
                    return gson.toJson(responseB);

                case GET_SLOT_BY_ID:
                    //
                    int slotIdb = data.get("slotId").getAsInt();
                    SpacewalkSlot slot = SpacewalkSlotDAO.getSlotById(slotIdb);

                    JsonObject responsec = new JsonObject();
                    if (slot != null) {
                        responsec.addProperty("status", "OK");
                        responsec.add("slot", gson.toJsonTree(slot));
                    } else {
                        responsec.addProperty("status", "ERROR");
                        responsec.addProperty("message", "Slot not found");
                    }
                    return gson.toJson(responsec);

                case CANCEL_BOOKING:
                    //
                    int bookingId = data.get("bookingId").getAsInt();

                    Booking canceled = BookingDAO.getBookingById(bookingId);
                    Passenger passengerC = passengerDAO.getPassengerById(canceled.getPassengerId());
                    SpacewalkSlot slotC = SpacewalkSlotDAO.getSlotById(canceled.getSlotId());

                    JsonObject cancelResult = new JsonObject();

                    if (passengerC != null && canceled != null && slotC != null) {
                        int refund = switch (canceled.getTier()) {
                            case 1 -> (int) (100000 * 0.75);
                            case 2 -> (int) (250000 * 0.75);
                            case 3 -> (int) (50000 * 0.75);
                            default -> 0;
                        };



                        passengerDAO.updateNovaCredits(passengerC.getPassengerId(), passengerC.getNovaCredits() + refund);


                        bookingDAO.deleteBooking(bookingId);


                        SpacewalkSlotDAO.setAvailability(slotC.getSlotId(), true);
                        CancellationState.registerCancellation(passengerC.getUsername());

                        cancelResult.addProperty("status", "OK");
                        cancelResult.addProperty("newBalance", passengerC.getNovaCredits());
                    } else {
                        cancelResult.addProperty("status", "ERROR");
                        cancelResult.addProperty("message", "Invalid cancel request");
                    }
                    return gson.toJson(cancelResult);

                case GET_BOOKINGS:
                    //
                    List<Booking> pending = bookingService.getAllBookings();

                    JsonObject responseGPB = new JsonObject();
                    responseGPB.addProperty("status", "OK");
                    responseGPB.add("bookings", gson.toJsonTree(pending));
                    return gson.toJson(responseGPB);

                case APPROVE_BOOKING:
                    //
                    int approveId = data.get("bookingId").getAsInt();
                    bookingService.setBookingStatus(approveId, "approved");

                    JsonObject approveResp = new JsonObject();
                    approveResp.addProperty("status", "OK");


                    return gson.toJson(approveResp);

                case DENY_BOOKING:
                    //
                    int denyId = data.get("bookingId").getAsInt();

                    Booking denied = bookingDAO.getBookingById(denyId);
                    if (denied != null) {

                        int refund = switch (denied.getTier()) {
                            case 1 -> 100000;
                            case 2 -> 250000;
                            case 3 -> 500000;
                            default -> 0;
                        };

                        Passenger refundee = passengerDAO.getPassengerById(denied.getPassengerId());
                        passengerDAO.updateNovaCredits(refundee.getPassengerId(), refundee.getNovaCredits() + refund);

                        SpacewalkSlotDAO.setAvailability(denied.getSlotId(), false);
                        bookingDAO.setBookingStatus(denyId, "denied");

                        JsonObject denyResp = new JsonObject();
                        denyResp.addProperty("status", "OK");
                        denyResp.addProperty("newBalance", refundee.getNovaCredits());
                        return gson.toJson(denyResp);
                    }

                    JsonObject error = new JsonObject();
                    error.addProperty("status", "ERROR");
                    return gson.toJson(error);

                case GET_PASSENGER_BY_ID:
                    //
                    int pID = data.get("passengerId").getAsInt();
                    Passenger passengerById = passengerDAO.getPassengerById(pID);

                    JsonObject resp = new JsonObject();
                    if (passengerById != null) {
                        resp.addProperty("status", "OK");
                        resp.add("passenger", gson.toJsonTree(passengerById));
                    } else {
                        resp.addProperty("status", "ERROR");
                    }
                    return gson.toJson(resp);

                case TRIGGER_BROADCAST:
                    //
                    String broadcast = data.get("message").getAsString();
                    BroadcastState.setMessage(broadcast);

                    JsonObject ack = new JsonObject();
                    ack.addProperty("status", "OK");
                    return gson.toJson(ack);

                case GET_BROADCAST:
                    //
                    JsonObject result = new JsonObject();
                    result.addProperty("status", "OK");
                    result.addProperty("message", BroadcastState.getMessage());
                    result.addProperty("timestamp", BroadcastState.getLastUpdated());
                    return gson.toJson(result);

                case GET_LAST_CANCELLATION:
                    //
                    JsonObject cancelResponse = new JsonObject();
                    cancelResponse.addProperty("status", "OK");
                    cancelResponse.addProperty("passenger", CancellationState.getLastCancelledPassenger());
                    cancelResponse.addProperty("timestamp", CancellationState.getLastUpdated());
                    return gson.toJson(cancelResponse);

                case TRIGGER_SURVEY:
                    //
                    SurveyState.triggerSurvey();
                    JsonObject resultSurvey = new JsonObject();
                    resultSurvey.addProperty("status", "OK");
                    return gson.toJson(resultSurvey);

                case CHECK_SURVEY_TRIGGER:
                    //
                    JsonObject survey = new JsonObject();
                    survey.addProperty("triggered", SurveyState.isSurveyTriggered());
                    return gson.toJson(survey);

                case GET_ALL_PASSENGERS:
                    //
                    List<Passenger> allPassengers = passengerDAO.getAllPassengers();
                    JsonObject allPassengersResponse = new JsonObject();
                    allPassengersResponse.addProperty("status", "OK");
                    allPassengersResponse.add("passengers", gson.toJsonTree(allPassengers));
                    return gson.toJson(allPassengersResponse);

                case CHECK_OUT_PASSENGER:
                    //
                    int pIdCheckout = data.get("passengerId").getAsInt();
                    passengerDAO.setCheckedOut(pIdCheckout, true);

                    JsonObject success = new JsonObject();
                    success.addProperty("status", "OK");
                    return gson.toJson(success);

                case CHECK_CHECKOUT_STATUS:
                    //
                    int passengerIdcheck = data.get("passengerId").getAsInt();
                    Passenger pCheck = passengerDAO.getPassengerById(passengerIdcheck);

                    JsonObject resultCheck = new JsonObject();
                    resultCheck.addProperty("checkedOut", pCheck != null && pCheck.isCheckedOut());
                    return gson.toJson(resultCheck);

                case SET_CHECKED_OUT_STATUS:
                    //
                    int pidsc = data.get("passengerId").getAsInt();
                    boolean status = data.get("checkedOut").getAsBoolean();
                    passengerDAO.setCheckedOut(pidsc, status);

                    JsonObject ok = new JsonObject();
                    ok.addProperty("status", "OK");
                    return gson.toJson(ok);

                case REGISTER_PHOTOGRAPHER:
                    //
                    String name = data.get("name").getAsString();
                    photographerService.registerPhotographer(name);

                    JsonObject regResp = new JsonObject();
                    regResp.addProperty("status", "OK");
                    return gson.toJson(regResp);

                case GET_PHOTOGRAPHER_BY_NAME:
                    //
                    String searchName = data.get("name").getAsString();
                    Photographer found = photographerService.getByName(searchName);

                    JsonObject nameResp = new JsonObject();
                    if (found != null) {
                        nameResp.addProperty("status", "OK");
                        nameResp.add("photographer", gson.toJsonTree(found));
                    } else {
                        nameResp.addProperty("status", "NOT_FOUND");
                    }
                    return gson.toJson(nameResp);

                case GET_PHOTOGRAPHER_BY_ID:
                    //
                    int phototid = data.get("id").getAsInt();
                    Photographer photo = photographerService.getById(phototid);

                    JsonObject idResp = new JsonObject();
                    if (photo != null) {
                        idResp.addProperty("status", "OK");
                        idResp.add("photographer", gson.toJsonTree(photo));
                    } else {
                        idResp.addProperty("status", "NOT_FOUND");
                    }
                    return gson.toJson(idResp);

                case SET_PHOTOGRAPHER_CHECKED_OUT:
                    //
                    int photographerId = data.get("id").getAsInt();
                    boolean statusphoto = data.get("checkedOut").getAsBoolean();
                    photographerService.setCheckedOut(photographerId, statusphoto);

                    JsonObject statusResp = new JsonObject();
                    statusResp.addProperty("status", "OK");
                    return gson.toJson(statusResp);

                case GET_INVENTORY_BY_ROLE:
                    //
                    String role = data.get("role").getAsString();
                    List<InventoryItem> items = inventoryService.getItemsByRole(role);

                    JsonObject listResponse = new JsonObject();
                    listResponse.addProperty("status", "OK");
                    listResponse.add("items", gson.toJsonTree(items));
                    return gson.toJson(listResponse);

                case UPDATE_INVENTORY_ITEM:
                    //
                    InventoryItem updated = gson.fromJson(data.get("item"), InventoryItem.class);
                    inventoryService.updateItem(updated);

                    JsonObject updateResp = new JsonObject();
                    updateResp.addProperty("status", "OK");
                    return gson.toJson(updateResp);
                    //handle other actions here after adding to enumeration

                default:
                    // Handle unknown action
                    JsonObject error2 = new JsonObject();
                    error2.addProperty("status", "ERROR");
                    error2.addProperty("message", "Unknown action: " + action);
                    return gson.toJson(error2);
            }

        } catch (Exception e) {
            //catch exceptions and return ERROR
            JsonObject error = new JsonObject();
            error.addProperty("status", "ERROR");
            error.addProperty("message", "Failed to parse or handle request.");
            logger.error("Failed to parse or handle request.", e);
            return gson.toJson(error);
        }
    }
}

