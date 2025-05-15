package com.inFlight.client.controller;

import com.inFlight.client.socket.ClientSocketService;
import com.inFlight.shared.model.*;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PhotographerController {
    private static final Logger logger = LoggerFactory.getLogger(PhotographerController.class);

    // UI elements for various photographer functionalities
    // most fxml entities do not display usage but are used to identify through fx:id in the corresponding FXML file
    public Button welcomeButton; // Button to navigate to the start screen
    public Button getStartedButton; // Button to navigate to the get started screen
    public Button serviceButton; // Button to navigate to the service screen
    public Button viewBookingsButton; // Button to view bookings
    public Button inventoryButton; // Button to navigate to the inventory screen
    public Button editPhotosButton; // Button to edit photos - not implemented
    public Button spaceWalkButton; // Button to navigate to the space walk screen
    public Button photographerCheckOutBackButton; // Button to navigate back from the check out screen
    public Button photographerCheckOutButton; // Button to check in/out the photographer
    public HBox photographerReviewBookingsHBox; // HBox to contain booking review elements
    public Button viewBookingsBackButton; // Button to navigate back from the view bookings screen
    public Label timeLabel1; // Label to display time information
    public Label passengerLabel1; // Label to display passenger information
    public Button inventoryMenuBackButton; // Button to navigate back from the inventory menu screen
    public Button equipmentCheckOutButton; // Button to check out equipment
    public Button equipmentMaintenanceButton; // Button to navigate to the equipment maintenance screen
    public Button inventoryMaintenanceBackButton; // Button to navigate back from the inventory maintenance screen
    public Button submitMaintenanceButton; // Button to submit maintenance information
    public Button inventoryBackButton; // Button to navigate back from the inventory screen
    public Label equipmentLabel1; // Label to display equipment information
    public Label conditionLabel1; // Label to display condition information
    public Label availableLabel1; // Label to display availability information
    public Button checkOutButton1; // Button to check out equipment

    private final ClientSocketService socketService = new ClientSocketService(); // Service for socket communication
    @FXML private VBox photographerBookingReviewContainer; // Container for booking review elements
    @FXML private VBox inventoryContainer; // Container for inventory elements

    private final String role = "Photographer"; // Role identifier for Photographer, used to filter inventory items

    /**
     * Loads a new scene based on the provided FXML path and event.
     * This method is responsible for switching between different screens in the application.
     *
     * @param fxmlPath the path to the FXML file for the new scene
     * @param event the action event that triggered the scene switch
     */
    private void loadScene(String fxmlPath, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
            loader.setController(this);
            Parent root = loader.load();

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest(e -> {
                socketService.disconnect();
                logger.info("Photographer disconnected from server.");
            });
            stage.show();

        } catch (Exception e) {
            logger.error("Scene switch failed", e);
        }
    }

    /**
     * Handles the action when the let's get started button is clicked in the welcome screen.
     *
     * @param event the action event that triggered the scene switch
     */
    @FXML
    public void switchToPhotographerChooseNameScreen(ActionEvent event) {
        loadScene("fxml/uiPhotographer/PhotographerChooseNameScreen.fxml", event);
        socketService.connect();
    }

    /**
     * Handles the action when the Inventory button is clicked in the start screen.
     *
     * @param event the action event that triggered the scene switch
     */
    public void switchToPhotograperInventoryMenuScreen(ActionEvent event) {
        loadScene("fxml/uiPhotographer/PhotographerInventoryMenuScreen.fxml", event);
    }

    /**
     * Handles the action when the SpaceWalk button is clicked in the start screen.
     *
     * @param event the action event that triggered the scene switch
     */
    public void switchToPhotographerSpaceWalkScreen(ActionEvent event) {
        loadScene("fxml/uiPhotographer/PhotographerSpaceWalkScreen.fxml", event);
        initializePhotographerDashboard();
    }

    /**
     * Handles the action when the back button is clicked outside the start screen.
     *
     * @param event the action event that triggered the scene switch
     */
    public void backToPhotographerStartScreen(ActionEvent event) {
        loadScene("fxml/uiPhotographer/PhotographerStartScreen.fxml", event);
    }

    /**
     * Handles the action when the inventory button is clicked in the inventory menu screen.
     *
     * @param event the action event that triggered the scene switch
     */
    public void switchToPhotographerInventoryScreen(ActionEvent event) {
        loadScene("fxml/uiPhotographer/PhotographerInventoryScreen.fxml", event);
        initializeInventoryScreen();
    }

    /**
     * Handles the action when the equipment maintenance button is clicked in the inventory menu screen.
     *
     * @param event the action event that triggered the scene switch
     */
    public void switchToPhotographerInventoryMaintenanceScreen(ActionEvent event) {
        loadScene("fxml/uiPhotographer/PhotographerInventoryMaintenanceScreen.fxml", event);
        initializeMaintenanceScreen();
    }

    /**
     * Handles the action when the back button is clicked in the inventory or maintenance screen.
     *
     * @param event the action event that triggered the scene switch
     */
    public void backToPhotographerInventoryMenuScreen(ActionEvent event) {
        loadScene("fxml/uiPhotographer/PhotographerInventoryMenuScreen.fxml", event);
    }

    @FXML TextField selectedNameTextField; // TextField for entering the photographer's name
    @FXML private Label errorLabel; // Label to display error messages


    private Photographer currentPhotographer; // Current photographer object

    /**
     * Handles the action when the confirm name button is clicked in the choose name screen.
     *
     * @param event the action event that triggered the scene switch
     */
    @FXML
    private void confirmName(ActionEvent event) {

        String name = selectedNameTextField.getText().trim();

        if (name.isEmpty()) {
            errorLabel.setText("Name cannot be empty.");
            errorLabel.setVisible(true);
            return;
        }

        Photographer p = socketService.getPhotographerByName(name);
        if (p == null) {
            errorLabel.setText("No Photographer with that name.");
            errorLabel.setVisible(true);
            return;
            }

        this.currentPhotographer = p;
        loadScene("fxml/uiPhotographer/PhotographerStartScreen.fxml", event);
    }

    /**
     * Handles the action when the check out button is clicked in the space walk screen.
     *
     */
    @FXML
    private void checkInOrOutPhotographer() {
        boolean newStatus = !currentPhotographer.isCheckedOut();
        socketService.setPhotographerCheckedOut(currentPhotographer.getId(), newStatus);
        currentPhotographer.setCheckedOut(newStatus);
        photographerCheckOutButton.setText(newStatus ? "Check In" : "Check Out");
    }

    /**
     * Initializes the photographer dashboard by setting the check out button text based on the photographer's status.
     */
    public void initializePhotographerDashboard() {
        photographerCheckOutButton.setText(currentPhotographer.isCheckedOut() ? "Check In" : "Check Out");
    }

    /**
     * Handles the action when the review bookings button is clicked in the space walk screen.
     *
     * @param event the action event that triggered the scene switch
     */
    public void switchToPhotographerViewBookingsScreen(ActionEvent event) {
        loadScene("fxml/uiPhotographer/PhotographerViewBookingsScreen.fxml", event);
        initializePhotographerReviewScreen();
    }

    /**
     * Initializes the booking review screen by populating the booking review container with booking information.
     */
    private void initializePhotographerReviewScreen() {
            List<Booking> bookings = socketService.getBookings();
            List<Booking> filteredBookings = bookings.stream()
                    .filter(b -> b.getTier() == 3 && b.getStatus().equals("approved"))
                    .toList();

            photographerBookingReviewContainer.getChildren().clear();

            for (Booking b : filteredBookings) {
                SpacewalkSlot slot = socketService.getSlotById(b.getSlotId());
                Passenger p = socketService.getPassengerById(b.getPassengerId());




                Label tierLabel = new Label("Space Prestige");
                tierLabel.setPrefWidth(200);
                tierLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

                Label timeLabel = new Label(slot.getSlotTime());
                timeLabel.setPrefWidth(200);
                timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

                Label  passengerLabel = new Label(p.getUsername());
                passengerLabel.setPrefWidth(200);
                passengerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

                Label  approvalLabel = new Label(b.getStatus());
                approvalLabel.setPrefWidth(200);
                approvalLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");


                HBox row = new HBox(30, tierLabel, timeLabel, passengerLabel, approvalLabel);
                row.setAlignment(Pos.CENTER);
                photographerBookingReviewContainer.getChildren().add(row);
            }

    }

    /**
     * Initializes the inventory screen by populating the inventory container with items based on the photographer's role.
     */
    public void initializeInventoryScreen() {
        List<InventoryItem> items = socketService.getInventoryItemsByRole(role);
        inventoryContainer.getChildren().clear();

        for (InventoryItem item : items) {
            Label nameLabel = new Label(item.getName());
            nameLabel.setPrefWidth(150);
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Label conditionLabel = new Label(item.getCondition());
            conditionLabel.setPrefWidth(200);
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

    @FXML private ComboBox<InventoryItem> equipmentComboBox; // ComboBox for selecting equipment
    @FXML private ComboBox<String> availabilityComboBox; // ComboBox for selecting availability
    @FXML private TextField conditionTextArea; // TextField for entering condition information

    /**
     * Initializes the maintenance screen by populating the equipment and availability ComboBoxes.
     */
    public void initializeMaintenanceScreen() {
        List<InventoryItem> items = socketService.getInventoryItemsByRole(role);
        equipmentComboBox.getItems().clear();
        equipmentComboBox.getItems().addAll(items);

        availabilityComboBox.getItems().clear();
        availabilityComboBox.getItems().addAll("Available", "Unavailable");
    }

    /**
     * Handles the action when the submit maintenance button is clicked in the maintenance screen.
     *
     * @param event the action event that triggered the scene switch
     */
    @FXML
    private void submitMaintenance(ActionEvent event) {
        InventoryItem selectedItem = equipmentComboBox.getValue();
        String selectedAvailability = availabilityComboBox.getValue();
        String condition = conditionTextArea.getText().trim();

        if (selectedItem == null || selectedAvailability == null || condition.isEmpty()) {
            errorLabel.setText("Please fill in all fields!");
            errorLabel.setVisible(true);
            return;
        }

        selectedItem.setAvailable("Available".equals(selectedAvailability));
        selectedItem.setCondition(condition);

        socketService.updateInventoryItem(selectedItem);

        backToPhotographerInventoryMenuScreen(event);
    }
}
