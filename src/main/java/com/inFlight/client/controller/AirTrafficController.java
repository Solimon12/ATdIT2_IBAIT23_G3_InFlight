package com.inFlight.client.controller;

import com.inFlight.client.socket.ClientSocketService;
import com.inFlight.shared.model.ChatMessage;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller class for managing Air Traffic Control (ATC) related UI and functionality.
 * Handles scene transitions, chat functionality with pilots, and error handling.
 */
public class AirTrafficController {
    private static final Logger logger = LoggerFactory.getLogger(AirTrafficController.class);

    // UI elements for various ATC functionalities
    // most fxml entities do not display usage but are used to identify through fx:id in the corresponding FXML file
    public Button welcomeButton; // Button to navigate to the start screen
    public Button trafficOverviewButton1; // Button to view traffic overview - not implemented
    public Button messagePilotButton; // Button to navigate to the message pilot screen
    public Button routeDataButton; // Button to view route data - not implemented
    public Button aircraftDataButton; // Button to view aircraft data - not implemented
    public Button messagePilotBackButton; // Button to navigate back from the message pilot screen
    public Label errorLabel; // Label to display error messages

    // Service for socket communication
    private final ClientSocketService socketService = new ClientSocketService();
    private final String myRole = "ATC"; // Role identifier for Air Traffic Control

    @FXML
    private VBox chatBoxPilotATC; // Container for chat messages with the pilot
    @FXML
    private TextField messageFieldPilotATC; // Input field for sending messages to the pilot

    /**
     * Handles the start button action.
     * Connects to the server and navigates to the ATC start screen.
     *
     * @param event The action event triggered by the UI.
     */
    @FXML
    private void onStart(ActionEvent event) {
        errorLabel.setVisible(false);
        if (!socketService.connect()) {
            errorLabel.setText("Cannot connect to server.");
            errorLabel.setVisible(true);
            return;
        }
        loadScene("fxml/uiAirTraffic/AirTrafficStartScreen.fxml", event);
    }

    /**
     * Switches to the message pilot screen and sets up the chat functionality.
     *
     * @param event The action event triggered by the UI.
     */
    @FXML
    private void switchToAirTrafficMessagePilotScreen(ActionEvent event) {
        loadScene("fxml/uiAirTraffic/AirTrafficMessagePilotScreen.fxml", event);
        setupChat("Pilot", chatBoxPilotATC, messageFieldPilotATC);
    }

    /**
     * Navigates back to the ATC start screen.
     *
     * @param event The action event triggered by the UI.
     */
    @FXML
    private void backToAirTrafficStartScreen(ActionEvent event) {
        loadScene("fxml/uiAirTraffic/AirTrafficStartScreen.fxml", event);
    }

    /**
     * Sends a message to the pilot.
     * The message is prefixed with "ATC" to indicate the sender.
     */
    @FXML
    private void onSendPilotMessage() {
        String text = "ATC: \n" + messageFieldPilotATC.getText().trim();
        if (text.isEmpty()) return;

        ChatMessage msg = new ChatMessage("ATC", "Pilot", text);
        socketService.sendChatMessage(msg);
        messageFieldPilotATC.clear();
        refreshChat("Pilot", chatBoxPilotATC);
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
     * Initializes the chat logic.
     * Starts polling for chat messages if connected to the server.
     */
    public void initialize() {
        if (socketService.isConnected()) {
            startChatPolling("Pilot", "ATC");
        }
    }

    /**
     * Starts polling for new chat messages.
     *
     * @param sender The sender of the messages (e.g., "Pilot").
     * @param receiver The receiver of the messages (e.g., "ATC").
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
     * @param receiver The receiver of the messages (e.g., "ATC").
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
                logger.info("ATC disconnected from server.");
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

