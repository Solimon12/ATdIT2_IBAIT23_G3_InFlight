package com.inFlight.client.controller;

import com.inFlight.client.socket.ClientSocketService;
import com.inFlight.shared.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
 * Controller class for managing flight attendant-related UI and functionality.
 * Handles scene transitions, passenger management, inventory management, and communication with the pilot.
 */
public class AttendantController {
    private static final Logger logger = LoggerFactory.getLogger(AttendantController.class);

    // UI elements for various flight attendant functionalities
    // most fxml entities do not display usage but are used to identify through fx:id in the corresponding FXML file
    public Button welcomeButton; // Button to navigate to the welcome screen
    public Button StartBackButton; // Button to navigate back from the start screen
    public Button viewPassengersButton; // Button to view passenger information
    public Button viewPhotographerButton; // Button to view photographer information
    public Button inventoryButton; // Button to manage inventory
    public Button pilotMessengerButton; // Button to communicate with the pilot
    public Label photographerNameLabel; // Label to display the photographer's name
    public Button viewPassengersBackButton; // Button to navigate back from the passenger view screen
    public Button passenger2Button; // Button for passenger 2
    public Button passenger1Button; // Button for passenger 1
    public Button passenger5Button; // Button for passenger 5
    public Button passenger4Button; // Button for passenger 4
    public Button passenger3Button; // Button for passenger 3
    public Button selectPassengerInfoBackButton; // Button to navigate back from the passenger info screen
    public Label selectedPassengerNameLabel; // Label to display the selected passenger's name
    public Button selectedPassengerServiceCallsButton; // Button to view service calls for the selected passenger
    public Button selectedPassengerBookingsButton; // Button to view bookings for the selected passenger
    public Button selectedPassengerCheckOutButton; // Button to check in or check out the selected passenger
    public Button bookingReviewBackButton; // Button to navigate back from the booking review screen
    public Label flightAttendantPassengerLabel; // Label to display passenger information
    public Label flightAttendantPackageLabel1; // Label to display package information for passenger 1
    public Label flightAttendantTimeLabel1; // Label to display time information for passenger 1
    public Label flightAttendantPassengerLabel1; // Label to display passenger information for passenger 1
    public Button messagePilotBackButton; // Button to navigate back from the pilot message screen
    public ListView chatList; // List view to display chat messages
    public TextField messageField; // Text field to input chat messages
    public Button inventoryMenuBackButton; // Button to navigate back from the inventory menu
    public Button equipmentCheckOutButton; // Button to check out equipment
    public Button equipmentMaintenanceButton; // Button to perform equipment maintenance
    public Button inventoryBackButton; // Button to navigate back from the inventory screen
    public Button checkOutButton1; // Button to check out equipment item 1
    public Label availableLabel1; // Label to display availability of equipment item 1
    public Label conditionLabel1; // Label to display condition of equipment item 1
    public Label equipmentLabel1; // Label to display name of equipment item 1
    public Button inventoryMaintenanceBackButton; // Button to navigate back from the inventory maintenance screen
    public ComboBox equipmentComboBox; // ComboBox to select equipment for maintenance
    public ComboBox availabilityComboBox; // ComboBox to select availability status for maintenance
    public TextArea conditionTextArea; // Text area to input condition details for maintenance
    public Button submitMaintenanceButton; // Button to submit maintenance updates
    public Label errorLabel; // Label to display error messages

    // Service for socket communication
    private final ClientSocketService socketService = new ClientSocketService();
    private final String myRole = "Attendant"; // Role identifier for the flight attendant
    private List<Passenger> loadedPassengers = new ArrayList<>(); // List of loaded passengers
    private List<Button> passengerButtons; // List of passenger buttons
    @FXML private VBox inventoryContainer; // Container for displaying inventory items


    @FXML
    private VBox chatBoxPilotAttendant; // Container for chat messages with the pilot
    @FXML
    private TextField messageFieldPilotAttendant; // Input field for sending messages to the pilot
    @FXML
    private VBox attendantBookingReviewContainer; // Container for displaying passenger bookings


    /**
     * Switches to the flight attendant start screen.
     * Displays an error message if the server connection fails.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToFlightAttendantStartScreen(ActionEvent event) {
        errorLabel.setVisible(false);
        if (!socketService.connect()) {
            errorLabel.setText("Cannot connect to server.");
            errorLabel.setVisible(true);
            return;
        }
        loadScene("fxml/uiFlightAttendant/FlightAttendantStartScreen.fxml", event);
    }

    /**
     * Initializes the passenger overview screen by populating it with passenger data.
     */
    public void initializePassengerOverviewScreen() {
        passengerButtons = List.of(passenger1Button, passenger2Button, passenger3Button, passenger4Button, passenger5Button);
        loadedPassengers = socketService.getAllPassengers();

        for (int i = 0; i < 5; i++) {
            if (i < loadedPassengers.size()) {
                passengerButtons.get(i).setText(loadedPassengers.get(i).getUsername());
                passengerButtons.get(i).setDisable(false);
            } else {
                passengerButtons.get(i).setText("Not Booked");
                passengerButtons.get(i).setDisable(true);
            }
        }
    }

    private Passenger currentPassenger; // The currently selected passenger
    private String lastButtonId; // The ID of the last button clicked

    /**
     * Initializes the passenger management screen based on the selected passenger.
     *
     * @param event The action event triggered by the UI.
     */
    private void initializePassengerManagementScreen(ActionEvent event) {
        Node source = (Node) event.getSource();
        String id = source.getId();
        switch (id) {
            case "passenger1Button" -> {
                selectedPassengerNameLabel.setText(loadedPassengers.get(0).getUsername());
                selectedPassengerServiceCallsButton.setText(loadedPassengers.get(0).getUsername() + " Service Calls");
                selectedPassengerBookingsButton.setText(loadedPassengers.get(0).getUsername() + " Bookings");
                currentPassenger = loadedPassengers.get(0);
                if (currentPassenger.isCheckedOut()) {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(0).getUsername() + " Check In");
                } else {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(0).getUsername() + " Check Out");
                }
                lastButtonId = id;
            }

            case "passenger2Button" -> {
                selectedPassengerNameLabel.setText(loadedPassengers.get(1).getUsername());
                selectedPassengerServiceCallsButton.setText(loadedPassengers.get(1).getUsername() + " Service Calls");
                selectedPassengerBookingsButton.setText(loadedPassengers.get(1).getUsername() + " Bookings");
                currentPassenger = loadedPassengers.get(1);
                if (currentPassenger.isCheckedOut()) {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(1).getUsername() + " Check In");
                } else {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(1).getUsername() + " Check Out");
                }
                lastButtonId = id;
            }
            case "passenger3Button" -> {
                selectedPassengerNameLabel.setText(loadedPassengers.get(2).getUsername());
                selectedPassengerServiceCallsButton.setText(loadedPassengers.get(2).getUsername() + " Service Calls");
                selectedPassengerBookingsButton.setText(loadedPassengers.get(2).getUsername() + " Bookings");
                currentPassenger = loadedPassengers.get(2);
                if (currentPassenger.isCheckedOut()) {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(2).getUsername() + " Check In");
                } else {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(2).getUsername() + " Check Out");
                }
                lastButtonId = id;
            }
            case "passenger4Button" -> {
                selectedPassengerNameLabel.setText(loadedPassengers.get(3).getUsername());
                selectedPassengerServiceCallsButton.setText(loadedPassengers.get(3).getUsername() + " Service Calls");
                selectedPassengerBookingsButton.setText(loadedPassengers.get(3).getUsername() + " Bookings");
                currentPassenger = loadedPassengers.get(3);
                if (currentPassenger.isCheckedOut()) {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(3).getUsername() + " Check In");
                } else {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(3).getUsername() + " Check Out");
                }
                lastButtonId = id;
            }
            case "passenger5Button" -> {
                selectedPassengerNameLabel.setText(loadedPassengers.get(4).getUsername());
                selectedPassengerServiceCallsButton.setText(loadedPassengers.get(4).getUsername() + " Service Calls");
                selectedPassengerBookingsButton.setText(loadedPassengers.get(4).getUsername() + " Bookings");
                currentPassenger = loadedPassengers.get(4);
                if (currentPassenger.isCheckedOut()) {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(4).getUsername() + " Check In");
                } else {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(4).getUsername() + " Check Out");
                }
                lastButtonId = id;
            }
            default -> logger.info("Unrecognized button: " + id);
        }
    }

    /**
     * Initializes the passenger management screen when navigating back.
     *
     * @param id The ID of the last button clicked.
     */
    private void initializeBackPassengerManagementScreen(String id) {
        switch (id) {
            case "passenger1Button" -> {
                selectedPassengerNameLabel.setText(loadedPassengers.get(0).getUsername());
                selectedPassengerServiceCallsButton.setText(loadedPassengers.get(0).getUsername() + "\n Service Calls");
                selectedPassengerBookingsButton.setText(loadedPassengers.get(0).getUsername() + "\n Bookings");
                currentPassenger = loadedPassengers.get(0);
                if (currentPassenger.isCheckedOut()) {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(0).getUsername() + "\n Check In");
                } else {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(0).getUsername() + "\n Check Out");
                }
                lastButtonId = id;
            }

            case "passenger2Button" -> {
                selectedPassengerNameLabel.setText(loadedPassengers.get(1).getUsername());
                selectedPassengerServiceCallsButton.setText(loadedPassengers.get(1).getUsername() + "\n Service Calls");
                selectedPassengerBookingsButton.setText(loadedPassengers.get(1).getUsername() + "\n Bookings");
                currentPassenger = loadedPassengers.get(1);
                if (currentPassenger.isCheckedOut()) {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(1).getUsername() + "\n Check In");
                } else {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(1).getUsername() + "\n Check Out");
                }                currentPassenger = loadedPassengers.get(1);
                lastButtonId = id;
            }
            case "passenger3Button" -> {
                selectedPassengerNameLabel.setText(loadedPassengers.get(2).getUsername());
                selectedPassengerServiceCallsButton.setText(loadedPassengers.get(2).getUsername() + "\n Service Calls");
                selectedPassengerBookingsButton.setText(loadedPassengers.get(2).getUsername() + "\n Bookings");
                currentPassenger = loadedPassengers.get(2);
                if (currentPassenger.isCheckedOut()) {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(2).getUsername() + "\n Check In");
                } else {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(2).getUsername() + "\n Check Out");
                }                currentPassenger = loadedPassengers.get(2);
                lastButtonId = id;
            }
            case "passenger4Button" -> {
                selectedPassengerNameLabel.setText(loadedPassengers.get(3).getUsername());
                selectedPassengerServiceCallsButton.setText(loadedPassengers.get(3).getUsername() + "\n Service Calls");
                selectedPassengerBookingsButton.setText(loadedPassengers.get(3).getUsername() + "\n Bookings");
                currentPassenger = loadedPassengers.get(3);
                if (currentPassenger.isCheckedOut()) {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(3).getUsername() + "\n Check In");
                } else {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(3).getUsername() + "\n Check Out");
                }
                lastButtonId = id;
            }
            case "passenger5Button" -> {
                selectedPassengerNameLabel.setText(loadedPassengers.get(4).getUsername());
                selectedPassengerServiceCallsButton.setText(loadedPassengers.get(4).getUsername() + "\n Service Calls");
                selectedPassengerBookingsButton.setText(loadedPassengers.get(4).getUsername() + "\n Bookings");
                currentPassenger = loadedPassengers.get(4);
                if (currentPassenger.isCheckedOut()) {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(4).getUsername() + "\n Check In");
                } else {
                    selectedPassengerCheckOutButton.setText(loadedPassengers.get(4).getUsername() + "\n Check Out");
                }
                lastButtonId = id;
            }
            default -> logger.info("Unrecognized button: " + id);
        }
    }

    /**
     * Switches to the passenger view screen and initializes the passenger overview.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToFlightAttendantViewPassengersScreen(ActionEvent event) {
        loadScene("fxml/uiFlightAttendant/FlightAttendantViewPassengersScreen.fxml", event);
        initializePassengerOverviewScreen();
    }

    /**
     * Switches to the pilot message screen and sets up the chat functionality.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToFlightAttendantMessagePilotScreen(ActionEvent event) {
        loadScene("fxml/uiFlightAttendant/FlightAttendantMessagePilotScreen.fxml", event);
        setupChat("Pilot", chatBoxPilotAttendant, messageFieldPilotAttendant);
    }

    /**
     * Switches to the passenger info screen and initializes the passenger management screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToFlightAttendantSelectPassengerInfoScreen(ActionEvent event) {
        loadScene("fxml/uiFlightAttendant/FlightAttendantSelectPassengerInfoScreen.fxml", event);
        initializePassengerManagementScreen(event);
    }

    /**
     * Toggles the check-in or check-out status of the selected passenger.
     *
     * @param event The action event triggered by the UI.
     */
    @FXML
    private void checkInOrOutPassenger(ActionEvent event) {
        boolean currentlyCheckedOut = currentPassenger.isCheckedOut();
        boolean newStatus = !currentlyCheckedOut;

        // Update the passenger's status on the server
        socketService.setCheckedOutStatus(currentPassenger.getPassengerId(), newStatus);

        // Update the local passenger object
        currentPassenger.setCheckedOut(newStatus);

        // Update the button text
        selectedPassengerCheckOutButton.setText(newStatus ? "Check In" : "Check Out");
        logger.info(currentPassenger.getUsername() + " is now " + (newStatus ? "checked out" : "checked in"));
    }


    /**
     * Navigates back to the passenger info screen and reinitializes the passenger management screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void backToFlightAttendantSelectPassengerInfoScreen(ActionEvent event) {
        loadScene("fxml/uiFlightAttendant/FlightAttendantSelectPassengerInfoScreen.fxml", event);
        initializeBackPassengerManagementScreen(lastButtonId);
    }

    /**
     * Switches to the passenger bookings screen and initializes the bookings view.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToFlightAttendantViewBookingsScreen(ActionEvent event) {
        loadScene("fxml/uiFlightAttendant/FlightAttendantViewBookingsScreen.fxml", event);
        initializePassengerBookingsScreen();
    }

    @FXML private VBox attendantPassengerBookingContainer; // Container for displaying passenger bookings
    @FXML private Label bookingScreenPassengerLabel; // Label to display the selected passenger's name

    /**
     * Initializes the passenger bookings screen by populating it with booking data.
     */
    private void initializePassengerBookingsScreen() {
        bookingScreenPassengerLabel.setText(currentPassenger.getUsername());
        loadBookingsForPassenger(currentPassenger);
    }

    /**
     * Loads bookings for the selected passenger to display them in the UI.
     *
     * @param passenger The selected passenger.
     */
    private void loadBookingsForPassenger(Passenger passenger) {
        List<Booking> bookings = socketService.getBookingsForPassenger(passenger.getPassengerId()).stream().sorted(Comparator.comparing(Booking::getSlotId)).toList();
        attendantPassengerBookingContainer.getChildren().clear();

        for (Booking b : bookings) {
            SpacewalkSlot slot = socketService.getSlotById(b.getSlotId());

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

            Label statusLabel = new Label(b.getStatus());
            statusLabel.setPrefWidth(200);
            statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            HBox row = new HBox(30, tierLabel, timeLabel, statusLabel);
            row.setAlignment(Pos.CENTER);
            attendantPassengerBookingContainer.getChildren().add(row);
        }
    }

    /**
     * Switches to the photographer view screen and displays the photographer's name.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToFlightAttendantViewPhotographerScreen(ActionEvent event) {
        loadScene("fxml/uiFlightAttendant/FlightAttendantViewPhotographerScreen.fxml", event);
        photographerNameLabel.setText(socketService.getPhotographerById(1).getName() + "\n" + "Service Calls");

    }

    /**
     * Switches to the inventory menu screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToFlightAttendantInventoryMenuScreen(ActionEvent event) {
        loadScene("fxml/uiFlightAttendant/FlightAttendantInventoryMenuScreen.fxml", event);
    }

    /**
     * Switches back to Start Screen. Used by all back buttons.
     *
     * @param event The action event triggered by the UI.
     */
    public void backToFlightAttendantStartScreen(ActionEvent event) {
        loadScene("fxml/uiFlightAttendant/FlightAttendantStartScreen.fxml", event);
    }

    /**
     * Switches to the inventory screen and initializes the inventory view.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToFlightAttendantInventoryScreen(ActionEvent event) {
        loadScene("fxml/uiFlightAttendant/FlightAttendantInventoryScreen.fxml", event);
        initializeInventoryScreen();
    }

    /**
     * Switches to the inventory maintenance screen and initializes the maintenance view.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToFlightAttendantInventoryMaintenanceScreen(ActionEvent event) {
        loadScene("fxml/uiFlightAttendant/FlightAttendantInventoryMaintenanceScreen.fxml", event);
        initializeMaintenanceScreen();
    }

    /**
     * Navigates back to the inventory menu screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void backToFlightAttendantInventoryMenuScreen(ActionEvent event) {
        loadScene("fxml/uiFlightAttendant/FlightAttendantInventoryMenuScreen.fxml", event);
    }

    /**
     * Sends a message to the pilot.
     * The message is prefixed with "Attendant" to indicate the sender.
     */
    @FXML
    private void onSendPilotMessage() {
        String text = "Attendant: \n" + messageFieldPilotAttendant.getText().trim();
        if (text.isEmpty()) return;

        ChatMessage msg = new ChatMessage("Attendant", "Pilot", text);
        socketService.sendChatMessage(msg);
        messageFieldPilotAttendant.clear();
        refreshChat("Pilot", chatBoxPilotAttendant);
    }

    /**
     * Sets up chat functionality for a specific chat partner.
     * Refreshes the chat messages periodically.
     *
     * @param chatPartner The chat partner (e.g., "Pilot").
     * @param chatBox The VBox container for chat messages.
     * @param messageField The TextField for entering chat messages.
     */
    private void setupChat(String chatPartner, VBox chatBox, TextField messageField) {
        Timeline refresher = new Timeline(new KeyFrame(Duration.seconds(1), e -> refreshChat(chatPartner, chatBox)));
        refresher.setCycleCount(Timeline.INDEFINITE);
        refresher.play();
    }

    /**
     * Refreshes the chat messages for a specific chat partner.
     * Updates the chat box with the latest messages.
     *
     * @param partner The chat partner (e.g., "Pilot").
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

    private Timeline chatPoller; // Timeline for polling chat messages
    private int lastMessageCount = 0; // Tracks the last message count for chat

    /**
     * Starts polling for chat messages if connected to the server.
     */
    public void initialize() {
        if (socketService.isConnected()) {
            startChatPolling("Pilot", "Attendant");
        }
    }

    /**
     * Starts polling for new chat messages.
     *
     * @param sender The sender of the messages (e.g., "Pilot").
     * @param receiver The receiver of the messages (e.g., "Attendant").
     */
    private void startChatPolling(String sender, String receiver) {
        chatPoller = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> checkNewMessages(sender, receiver))
        );
        chatPoller.setCycleCount(Timeline.INDEFINITE);
        chatPoller.play();
    }

    /**
     * Checks for new chat messages and displays an alert if a new message is detected.
     *
     * @param sender The sender of the messages (e.g., "Pilot").
     * @param receiver The receiver of the messages (e.g., "Attendant").
     */
    private void checkNewMessages(String sender, String receiver) {
        List<ChatMessage> allMessages = socketService.getChat(sender, receiver);
        List<ChatMessage> incoming = allMessages.stream()
                .filter(msg -> msg.getSender().equals(sender) && msg.getReceiver().equals(receiver))
                .toList();

        if (incoming.size() > lastMessageCount) {
            lastMessageCount = incoming.size();

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("New Message");
                alert.setHeaderText(null);
                alert.setContentText("New message from " + sender + ".");
                alert.show();
            });
        }
    }

    /**
     * Initializes the inventory screen by populating it with inventory items.
     */
    public void initializeInventoryScreen() {
        List<InventoryItem> items = socketService.getInventoryItemsByRole(myRole);
        inventoryContainer.getChildren().clear();

        for (InventoryItem item : items) {
            Label nameLabel = new Label(item.getName());
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Label conditionLabel = new Label(item.getCondition());
            conditionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Button toggleButton = new Button(item.isCheckedOut() ? "Check In" : "Check Out");
            toggleButton.setPrefWidth(100);


            toggleButton.setDisable(!item.isAvailable());

            toggleButton.setOnAction(e -> {

                item.setCheckedOut(!item.isCheckedOut());
                socketService.updateInventoryItem(item);


                toggleButton.setText(item.isCheckedOut() ? "Check In" : "Check Out");
            });

            HBox row = new HBox(110, nameLabel, conditionLabel, toggleButton);
            row.setAlignment(Pos.CENTER);

            inventoryContainer.getChildren().add(row);
        }
    }

    @FXML private ComboBox<InventoryItem> equipmentComboBoxAttendant; // ComboBox to select equipment for maintenance
    @FXML private ComboBox<String> availabilityComboBoxAttendant; // ComboBox to select availability status for maintenance
    @FXML private TextField conditionTextAreaAttendant; // TextField to input condition details for maintenance

    /**
     * Initializes the maintenance screen by populating it with inventory items and availability options.
     */
    public void initializeMaintenanceScreen() {
        List<InventoryItem> items = socketService.getInventoryItemsByRole(myRole);
        equipmentComboBoxAttendant.getItems().clear();
        equipmentComboBoxAttendant.getItems().addAll(items);

        availabilityComboBoxAttendant.getItems().clear();
        availabilityComboBoxAttendant.getItems().addAll("Available", "Unavailable");
    }

    /**
     * Handles the submission of maintenance updates for an inventory item.
     * Validates the input fields, updates the selected item's availability and condition,
     * and sends the updated information to the server.
     *
     * @param event The action event triggered by the UI.
     */
    @FXML
    private void submitMaintenanceAttendant(ActionEvent event) {
        InventoryItem selectedItem = equipmentComboBoxAttendant.getValue();
        String selectedAvailability = availabilityComboBoxAttendant.getValue();
        String condition = conditionTextAreaAttendant.getText().trim();

        if (selectedItem == null || selectedAvailability == null || condition.isEmpty()) {
            errorLabel.setText("Please fill in all fields!");
            errorLabel.setVisible(true);
            return;
        }

        selectedItem.setAvailable("Available".equals(selectedAvailability));
        selectedItem.setCondition(condition);

        socketService.updateInventoryItem(selectedItem);

        backToFlightAttendantInventoryMenuScreen(event);
    }

    /**
     * Loads a new scene based on the provided FXML path.
     *
     * @param fxmlPath The path to the FXML file.
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
                logger.info("Flight Attendant disconnected from server.");
            });
            stage.show();

        } catch (Exception e) {
            logger.error("Scene switch failed", e);
        }
    }

    private void stopAllPollers() {
        if (chatPoller != null) chatPoller.stop();
    }
}
