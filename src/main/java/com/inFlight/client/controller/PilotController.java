package com.inFlight.client.controller;

import com.google.gson.JsonObject;
import com.inFlight.client.socket.ClientSocketService;
import com.inFlight.shared.model.Booking;
import com.inFlight.shared.model.ChatMessage;
import com.inFlight.shared.model.Passenger;
import com.inFlight.shared.model.SpacewalkSlot;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller class for managing the Pilot role in the application.
 * This class handles the UI interactions and socket communication for the Pilot role.
 */
public class PilotController {
    private static final Logger logger = LoggerFactory.getLogger(PilotController.class);

    // UI elements for various Pilot functionalities
    // most fxml entities do not display usage but are used to identify through fx:id in the corresponding FXML file
    public Button welcomeButton; // Button to navigate to the start screen
    public Button startScreenBackButton; // Button to navigate back from the start screen
    public Button aircraftDataButton; // Button to view aircraft data - not implemented
    public Button routeDataButton; // Button to view route data - not implemented
    public Button reviewBookingsButton; // Button to navigate to the review bookings screen
    public Button messageAirTrafficControllerButton; // Button to navigate to the message ATC screen
    public Button messageFlightAttendantsButton; // Button to navigate to the message flight attendants screen
    public Button passengerMenuButton; // Button to navigate to the passenger menu screen
    public Button messageAttendantBackButton; // Button to navigate back from the message attendant screen
    public Button messageATCBackButton; // Button to navigate back from the message ATC screen
    public Button passengerMenuBackButon; // Button to navigate back from the passenger menu screen
    public Button ShipAndSpaceCheckButton; // Button to navigate to the ship and space check screen
    public Button pilotMessagePassengersButton; // Button to navigate to the message passengers screen
    public Button initiateSurveyButton; // Button to initiate a survey
    public Button pilotMessagePassengersBackButton; // Button to navigate back from the message passengers screen
    public TextArea pilotMessagePassengersTextArea; // Text area for entering messages to passengers
    public Button sendPassengersButton; // Button to send messages to passengers
    public Button viewPassengersBackButton; // Button to navigate back from the view passengers screen
    public Button passenger2Button; // Button to view passenger 2 details
    public Button passenger1Button; // Button to view passenger 1 details
    public Button passenger5Button; // Button to view passenger 5 details
    public Button passenger4Button; // Button to view passenger 4 details
    public Button passenger3Button; // Button to view passenger 3 details
    public Button photographerButton; // Button to view photographer details
    public Button bookingReviewBackButton; // Button to navigate back from the booking review screen
    public HBox pilotReviewBookingsHBox; // HBox for displaying booking reviews
    public Label pilotPackageLabel1; // Label for displaying package information
    public Label pilotTimeLabel1; // Label for displaying time information
    public Label pilotPassengerLabel1; // Label for displaying passenger information
    public Button pilotApprovalButton1; // Button for approving or denying booking
    public VBox chatBoxATCContainer; // Container for chat messages with ATC
    public Label errorLabel; // Label for displaying error messages

    private final ClientSocketService socketService = new ClientSocketService(); // Service for socket communication
    private final String myRole = "Pilot"; // Role identifier for Pilot used in chat
    private int lastPendingCount = 0; // Counter for pending bookings

    @FXML
    private VBox chatBoxATC; // Container for chat messages with ATC
    @FXML
    private TextField messageFieldATC; // Input field for sending messages to ATC

    @FXML
    private VBox chatBoxAttendant; // Container for chat messages with Flight Attendant
    @FXML
    private TextField messageFieldAttendant; // Input field for sending messages to Flight Attendant

    private Timeline atcChatRefresh; // Timeline for refreshing chat with ATC
    private Timeline attendantChatRefresh; // Timeline for refreshing chat with Flight Attendant
    private Timeline bookingPoller; // Timeline for polling new bookings

    /**
     * Handles the start button action.
     * Connects to the server and navigates to the Pilot start screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToPilotStartScreen(ActionEvent event) {
        errorLabel.setVisible(false);
        if (!socketService.connect()) {
            errorLabel.setText("Cannot connect to server.");
            errorLabel.setVisible(true);
            return;
        }
        loadScene("fxml/uiPilot/PilotStartScreen.fxml", event);
    }

    /**
     * Navigates to the Pilot review bookings screen and initializes it.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToPilotReviewBookingsScreen(ActionEvent event) {
        loadScene("fxml/uiPilot/PilotReviewBookingsScreen.fxml", event);
        initializePilotReviewScreen();
    }

    /**
     * Navigates to the Pilot message passengers screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToPilotPassengerMenuScreen(ActionEvent event) {
        loadScene("fxml/uiPilot/PilotPassengerMenuScreen.fxml", event);
    }

    /**
     * Handles functionality of back buttons on second level UIs.
     * @param event
     */
    public void backToPilotStartScreen(ActionEvent event) {
        loadScene("fxml/uiPilot/PilotStartScreen.fxml", event);
    }


    /**
     * Navigates to the Pilot message Passengers screen.
     * @param event The action event triggered by the UI.
     */
    public void switchToPilotMessagePassengersScreen(ActionEvent event) {
        loadScene("fxml/uiPilot/PilotMessagePassengersScreen.fxml", event);
    }

    /**
     * Initiates the survey for passengers.
     * @param event The action event triggered by the UI.
     */
    public void initiateSurvey(ActionEvent event) {
        socketService.triggerSurvey();
    }

    /**
     * Initializes the PilotController.
     * Sets up the booking and chat polling if the socket is connected.
     */
    @FXML
    public void initialize() {
        if (socketService.isConnected()) {
            startBookingPolling();
            startCancellationPolling();
            startChatPollingATC();
            startChatPollingAttendant();
        }
    }


    private long lastCancelSeen = 0; // Timestamp of the last cancellation seen
    private Timeline cancelPoller;
    /**
     * Starts the cancellation polling to check for cancelled bookings.
     */
    private void startCancellationPolling() {
        cancelPoller = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> checkForCancelledBooking())
        );
        cancelPoller.setCycleCount(Animation.INDEFINITE);
        cancelPoller.play();
    }

    /**
     * Checks for cancelled bookings and displays an alert if a new cancellation is detected.
     */
    private void checkForCancelledBooking() {
        JsonObject response = socketService.getLastCancellation();
        if (!"OK".equals(response.get("status").getAsString())) return;

        if (!response.has("passenger") || response.get("passenger").isJsonNull()) return;

        String passenger = response.get("passenger").getAsString();
        long timestamp = response.get("timestamp").getAsLong();

        if (timestamp > lastCancelSeen) {
            lastCancelSeen = timestamp;

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Booking Cancelled");
                alert.setHeaderText(null);
                alert.setContentText("Passenger " + passenger + " canceled their spacewalk.");
                alert.show();
            });
        }
    }

    /**
     * Starts the booking polling to check for new bookings.
     */
    private void startBookingPolling() {
        bookingPoller = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> checkForNewBookings())
        );
        bookingPoller.setCycleCount(Timeline.INDEFINITE);
        bookingPoller.play();
    }

    /**
     * Checks for new bookings and displays an alert if a new booking is detected.
     */
    private void checkForNewBookings() {
        List<Booking> pending = socketService.getBookings();

        if (pending.size() > lastPendingCount) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("New Booking");
                alert.setHeaderText(null);
                alert.setContentText("A new spacewalk booking has been submitted.");
                alert.show();
            });
        }

        lastPendingCount = pending.size();
    }

    @FXML private VBox pilotBookingReviewContainer; // Container for displaying booking reviews

    /**
     * Initializes the Pilot review bookings screen.
     * Loads the bookings and displays them in the UI.
     */
    public void initializePilotReviewScreen() {
        List<Booking> bookings = socketService.getBookings().stream().sorted(Comparator.comparing(Booking::getSlotId)).toList();

        pilotBookingReviewContainer.getChildren().clear();

        for (Booking b : bookings) {
            SpacewalkSlot slot = socketService.getSlotById(b.getSlotId());
            Passenger p = socketService.getPassengerById(b.getPassengerId());

            String tier = switch (b.getTier()) {
                case 1 -> "Space Essential";
                case 2 -> "Space Comfort";
                case 3 -> "Space Prestige";
                default -> "Unknown";
            };

            Label tierLabel = new Label(tier);
            tierLabel.setPrefWidth(200);
            tierLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Label timeLabel = new Label(slot.getSlotTime());
            timeLabel.setPrefWidth(200);
            timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Label passengerLabel = new Label(p.getUsername());
            passengerLabel.setPrefWidth(200);
            passengerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Button actionButton = new Button("Acknowledge");
            actionButton.setPrefWidth(120);
            if ("approved".equals(b.getStatus())) {
                actionButton.setText("Deny");
            }
            if ("denied".equals(b.getStatus())) {
                actionButton.setDisable(true);
                actionButton.setText("Denied");
            }

            int bookingId = b.getBookingId();

            if ("pending".equals(b.getStatus())) {
                actionButton.setOnAction(e -> {
                    socketService.approveBooking(bookingId);
                    actionButton.setText("Deny");
                    initializePilotReviewScreen();
                });
            } else {
                actionButton.setOnAction(ev -> {
                    socketService.denyBooking(bookingId);
                    initializePilotReviewScreen();
                });
            }
            HBox row = new HBox(30, tierLabel, timeLabel, passengerLabel, actionButton);
            row.setAlignment(Pos.CENTER_LEFT);
            pilotBookingReviewContainer.getChildren().add(row);
        }
    }


    /**
     * Navigates to the Pilot message Air Traffic Controller screen.
     * Sets up the chat functionality for the Air Traffic Controller.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToPilotMessageAirTrafficScreen(ActionEvent event) {
        loadScene("fxml/uiPilot/PilotMessageAirTrafficScreen.fxml", event);
        setupChat("ATC", chatBoxATC, messageFieldATC);
    }

    /**
     * Navigates to the Pilot message Flight Attendant screen.
     * Sets up the chat functionality for the Flight Attendant.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToPilotMessageFlightAttendantScreen(ActionEvent event) {
        loadScene("fxml/uiPilot/PilotMessageFlightAttendantScreen.fxml", event);
        setupChat("Attendant", chatBoxAttendant, messageFieldAttendant);
    }

    /**
     * Sends a message to the Flight Attendant.
     * The message is prefixed with "Pilot" to indicate the sender.
     */
    @FXML
    private void onSendAttendant() {
        String text = "Pilot: \n" + messageFieldAttendant.getText().trim();
        if (text.isEmpty()) return;

        ChatMessage msg = new ChatMessage("Pilot", "Attendant", text);
        socketService.sendChatMessage(msg);
        messageFieldAttendant.clear();
        refreshChat("Attendant", chatBoxAttendant);
    }

    /**
     * Sends a message to the Air Traffic Controller.
     * The message is prefixed with "Pilot" to indicate the sender.
     */
    @FXML
    private void onSendATCMessage() {
        String text = "Pilot: \n" + messageFieldATC.getText().trim();
        if (text.isEmpty()) return;

        ChatMessage msg = new ChatMessage("Pilot", "ATC", text);
        socketService.sendChatMessage(msg);
        messageFieldATC.clear();
        refreshChat("ATC", chatBoxATC);
    }

    /**
     * Sets up the chat functionality for a specific chat partner.
     * Refreshes the chat messages periodically.
     *
     * @param chatPartner The chat partner (e.g., "ATC").
     * @param chatBox The VBox container for chat messages.
     * @param messageField The TextField for entering chat messages.
     */
    private void setupChat(String chatPartner, VBox chatBox, TextField messageField) {
        Timeline refresher = new Timeline(new KeyFrame(Duration.seconds(1), e -> refreshChat(chatPartner, chatBox)));
        refresher.setCycleCount(Timeline.INDEFINITE);
        refresher.play();


        if (chatPartner.equals("ATC")) atcChatRefresh = refresher;
        else attendantChatRefresh = refresher;
    }

    /**
     * Refreshes the chat messages for a specific chat partner.
     * Updates the chat box with the latest messages.
     *
     * @param partner The chat partner (e.g., "ATC").
     * @param chatBox The VBox container for chat messages.
     */
    private void refreshChat(String partner, VBox chatBox) {
        List<ChatMessage> messages = socketService.getChat(myRole, partner);
        chatBox.getChildren().clear();

        for (ChatMessage msg : messages) {
            Label label = new Label(msg.getContent());
            label.setWrapText(true);
            label.setStyle("-fx-background-color: lightgray; -fx-padding: 8px; -fx-background-radius: 10px;");

            HBox wrapper = new HBox(label);
            wrapper.setAlignment(msg.getSender().equals(myRole) ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

            chatBox.getChildren().add(wrapper);
        }
    }

    /**
     * Loads a new scene based on the provided FXML path and event.
     * This method is used to switch between different screens in the application.
     *
     * @param fxmlPath The path to the FXML file for the new scene.
     * @param event The action event triggered by the UI.
     */
    private void loadScene(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));


            loader.setController(this);

            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest(e -> {
                stopAllPollers();
                socketService.disconnect();
                logger.info("Pilot disconnected from server.");
            });
            stage.show();

        } catch (Exception e) {
            logger.error("Scene switch failed", e);
        }
    }

    private void stopAllPollers() {

        if (chatPollerATC != null) chatPollerATC.stop();
        if (chatPollerAttendant != null) chatPollerAttendant.stop();
        if (bookingPoller != null) bookingPoller.stop();
        if (cancelPoller != null) cancelPoller.stop();
    }

    @FXML private TextArea broadcastField; // Text area for entering broadcast messages

    /**
     * Sends a broadcast message to all passengers.
     * This method is called when the user clicks the "Send Broadcast" button.
     */
    @FXML
    private void onSendBroadcast() {
        String msg = broadcastField.getText().trim();
        if (!msg.isEmpty()) {
            socketService.sendBroadcast(msg);
            broadcastField.clear();
        }
    }

    private Timeline chatPollerATC; // Timeline for polling chat with ATC
    private int lastMessageCountATC = 0; // Tracks the last message count for chat with ATC
    private Timeline chatPollerAttendant; // Timeline for polling chat with Flight Attendant
    private int lastMessageCountAttendant = 0; // Tracks the last message count for chat with Flight Attendant

    /**
     * Starts the chat polling for the Air Traffic Controller.
     * This method is called when the Pilot sends a message to the Air Traffic Controller.
     */
    private void startChatPollingATC() {
        chatPollerATC = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    checkNewMessagesATC("ATC", "Pilot");
                })
        );
        chatPollerATC.setCycleCount(Timeline.INDEFINITE);
        chatPollerATC.play();
    }

    /**
     * Checks for new messages from the Air Traffic Controller and displays an alert if a new message is detected.
     *
     * @param sender The sender of the messages (e.g., "ATC").
     * @param receiver The receiver of the messages (e.g., "Pilot").
     */
    private void checkNewMessagesATC(String sender, String receiver) {
        List<ChatMessage> incoming = socketService.getChat(sender, receiver);


        if (incoming.size() > lastMessageCountATC) {
            lastMessageCountATC = incoming.size();

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("New Message");
                alert.setHeaderText(null);
                alert.setContentText("New message from ATC.");
                alert.show();
            });
        }
    }

    /**
     * Starts the chat polling for the Flight Attendant.
     * This method is called when the Pilot sends a message to the Flight Attendant.
     */
    private void startChatPollingAttendant() {
        chatPollerAttendant = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    checkNewMessagesAttendant("Attendant", "Pilot");
                })
        );
        chatPollerAttendant.setCycleCount(Timeline.INDEFINITE);
        chatPollerAttendant.play();
    }

    /**
     * Checks for new messages from the Flight Attendant and displays an alert if a new message is detected.
     *
     * @param sender The sender of the messages (e.g., "Attendant").
     * @param receiver The receiver of the messages (e.g., "Pilot").
     */
    private void checkNewMessagesAttendant(String sender, String receiver) {
        List<ChatMessage> incoming = socketService.getChat(sender, receiver);


        if (incoming.size() > lastMessageCountAttendant) {
            lastMessageCountAttendant = incoming.size();

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("New Message");
                alert.setHeaderText(null);
                alert.setContentText("New message from Attendant.");
                alert.show();
            });
        }
    }

    private List<Passenger> loadedPassengers = new ArrayList<>(); // List to store loaded passengers
    private List<Button> passengerButtons; // List to store passenger buttons

    /**
     * Initializes the passenger overview screen.
     * Loads the passengers and sets their status on the buttons.
     */
    public void initializePassengerOverviewScreen() {
        passengerButtons = List.of(passenger1Button, passenger2Button, passenger3Button, passenger4Button, passenger5Button);
        loadedPassengers = socketService.getAllPassengers();
        String checkInOut;

        for (int i = 0; i < Math.min(loadedPassengers.size(), passengerButtons.size()); i++) {
            if(loadedPassengers.get(i).isCheckedOut()) {
                checkInOut = "Checked Out";
            } else {
                checkInOut = "Checked In";
            }
            passengerButtons.get(i).setText(loadedPassengers.get(i).getUsername() + "\n" + checkInOut);
        }
        String photographerStatus = socketService.getPhotographerById(1).isCheckedOut() ? "Checked Out" : "Checked In";
        photographerButton.setText(socketService.getPhotographerById(1).getName() + "\n" + photographerStatus);
    }

    /**
     * Navigates to Space and Ship Check screen.
     * This method is called when the user clicks the "Ship and Space Check" button.
     * It loads the corresponding FXML file and initializes the Ship and Space overview screen.
     *
     * @param event
     */
    public void switchToShipAndSpaceCheckScreen(ActionEvent event) {
        loadScene("fxml/uiPilot/ShipAndSpaceCheckScreen.fxml", event);
        initializePassengerOverviewScreen();
    }
}


