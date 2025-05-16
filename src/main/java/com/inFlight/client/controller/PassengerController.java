package com.inFlight.client.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.inFlight.client.socket.ClientSocketService;
import com.inFlight.shared.model.Booking;
import com.inFlight.shared.model.SpacewalkSlot;
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
import com.inFlight.shared.model.Passenger;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Controller class for managing passenger-related UI and functionality.
 * Handles scene transitions, booking management, and socket communication.
 */
public class PassengerController {
    private static final Logger logger = LoggerFactory.getLogger(PassengerController.class);

    // UI elements for various passenger functionalities
    // most fxml entities do not display usage but are used to identify through fx:id in the corresponding FXML file
    public Button letsGoButton; // Button to start the application
    public Button spaceWalkButton; // Button to book a spacewalk
    public Button novaCreditsButton; // Button to view NovaCredits
    public Button mediaButton; // Button to access media
    public Button cameraButton; // Button to access camera
    public Button flightDataButton; // Button to view flight data
    public Button vitalsButton; // Button to view vitals
    public Button serviceButton; // Button to access service
    public TextArea specialRequestTextArea; // Text area for special requests
    public TextField selectedNameTextField; // Text field for entering passenger name
    public Button getStartedButton; // Button to confirm name
    public Label errorLabel; // Label to display error messages
    public Button novaCreditsMenuBackButton; // Button to go back to the NovaCredits menu
    public Label novaCreditsMenuLabel; // Label to display NovaCredits balance
    public Button rechargeButton; // Button to recharge NovaCredits
    public Button spaceWalkSelectionBackButton; // Button to go back to spacewalk selection
    public Button cryptoWalletBackButton; // Button to go back to the crypto wallet
    public Button logInButton; // Button to log in to the crypto wallet
    public TextField cryptoPINTextField; // Text field for entering crypto wallet PIN
    public TextField cryptoUserTextField; // Text field for entering crypto wallet username
    public Label comfortSelectedTimeslotLabel; // Label to display selected timeslot for comfort package
    public Label comfortSelectedTimeslotLabel1; // Label to display selected timeslot for comfort package
    public Button selectCryptoBackButton; // Button to go back to the crypto selection screen
    public Button bitcoinButton; // Button to select Bitcoin as payment method
    public Button ethereumButton; // Button to select Ethereum as payment method
    public Button dogecoinButton; // Button to select Dogecoin as payment method
    public Button teslaStockButton; // Button to select Tesla Stock as payment method
    public Button confirmRechargeButton; // Button to confirm recharge of NovaCredits
    public TextField rechargeTextField; // Text field for entering recharge amount
    public Label selectedCurrencyLabel; // Label to display selected currency
    public Button rechargeNovaCreditsBackButton; // Button to go back to the recharge NovaCredits screen
    public Button loginDeniedButton; // Button to go back to the login denied screen
    public ComboBox surveyComboBox1; // ComboBox for survey question 1
    public ComboBox surveyComboBox2; // ComboBox for survey question 2
    public ComboBox surveyComboBox3; // ComboBox for survey question 3
    public Button surveyNextButton; // Button to go to the next survey question
    public TextArea surveyTextArea; // Text area for survey comments
    public ComboBox surveyComboBox; // ComboBox for survey question
    public Button surveySubmitButton; // Button to submit the survey
    public ProgressBar surveyProgressBar2; // Progress bar for survey completion
    public ProgressBar surveyProgressBar1; // Progress bar for survey completion
    public Label thanksLabel; // Label to display thank you message
    public Button passengerBookSpaceWalkButton; // Button to book a spacewalk
    public Button passengerReviewBookingsButton; // Button to review bookings
    public Button passengerBookingReviewBackButton; // Button to go back to the booking review screen
    public Button passengerCancelButton1; // Button to cancel a booking
    public Label passengerApprovalLabel1; // Label to display booking approval status
    public Label passengerTimeLabel1; // Label to display booking time
    public Label passengerPackageLabel1; // Label to display booking package
    public Button packageSelectionBackButton; // Button to go back to the package selection screen
    public Button spaceEssentialButton; // Button to select the essential package
    public Button spaceComfortButton; // Button to select the comfort package
    public Button spacePrestigeButton; // Button to select the prestige package
    public Button essentialTimeSlotBackButton; // Button to go back to the timeslot selection screen
    public Button essentialTimeslot1Button; // Button to select the first timeslot for essential package
    public Button essentialTimeslot2Button; // Button to select the second timeslot for essential package
    public Button essentialTimeslot3Button; // Button to select the third timeslot for essential package
    public Button comfortTimeslot1Button; // Button to select the first timeslot for comfort package
    public Button comfortTimeslot2Button; // Button to select the second timeslot for comfort package
    public Button comfortTimeslot3Button; // Button to select the third timeslot for comfort package
    public Button comfortCheckBackButton; // Button to go back to the timeslot selection screen
    public Button comfortConfirmButton; // Button to confirm booking for comfort package
    public Button essentialCheckBackButton; // Button to go back to the timeslot selection screen
    public Label essentialSelectedTimeslotLabel; // Label to display selected timeslot for essential package
    public Button essentialConfirmButton; // Button to confirm booking for essential package
    public Button comfortMoneyBackButton; // Button to go back to the booking screen
    public Label comfortNoMoneyLabel; // Label to display insufficient funds message
    public Button prestigeTimeslot1Button; // Button to select the first timeslot for prestige package
    public Button prestigeTimeslot2Button; // Button to select the second timeslot for prestige package
    public Button prestigeTimeslot3Button; // Button to select the third timeslot for prestige package
    public Button prestigeCheckBackButton; // Button to go back to the timeslot selection screen
    public Label prestigeSelectedTimeslotLabel; // Label to display selected timeslot for prestige package
    public Button prestigeConfirmButton; // Button to confirm booking for prestige package
    public Button bookingDoneButton; // Button to go back to the booking screen
    public Button packageSelectionButton; // Button to go back to the package selection screen
    public Label timeSlotLabel; // Label to display selected timeslot
    public VBox contentPane1; // VBox for layout
    @FXML Label tierLabel; // Label to display selected package


    private final ClientSocketService socketService = new ClientSocketService(); // Service for socket communication
    private Passenger currentPassenger; // Current passenger object, local persistance for controller instance

    private int selectedTier; // Selected package tier (1: Essential, 2: Comfort, 3: Prestige)
    private long lastBroadcastSeen = 0; // Timestamp of the last broadcast seen

    private SpacewalkSlot selectedSlot; // Selected spacewalk slot
    private List<Booking> previousBookings = new ArrayList<>(); // List of previous bookings for the passenger
    private Timeline bookingStatusPoller; // Timeline for polling booking status updates
    @FXML Button checkInButton; // Button to check in for the flight

    /**
     * Handles the back button action.
     * Navigates back to the passenger start screen from most second level UIs.
     *
     * @param event The action event triggered by the UI.
     */
    public void backToPassengerStartScreen(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerStartScreen.fxml", event);
    }

    /**
     * Handles the Lets Go button action.
     * Navigates to the passenger choose name screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToPassengerChooseNameScreen(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerChooseNameScreen.fxml", event);
    }

    /**
     * Handles the confirm name button action.
     * Validates the entered username and attempts to connect to the server.
     * If successful, navigates to the passenger start screen.
     *
     * @param event The action event triggered by the UI.
     */
    @FXML
    private void confirmName(ActionEvent event) {
        errorLabel.setVisible(false);
        String username = selectedNameTextField.getText().trim();

        if (username.isEmpty()) {
            errorLabel.setText("Please enter a username.");
            errorLabel.setVisible(true);
            return;
        }

        if (!socketService.connect()) {
            errorLabel.setText("Cannot connect to server.");
            errorLabel.setVisible(true);
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("username", username);

        JsonObject response = socketService.send("LOGIN", data);

        if ("OK".equals(response.get("status").getAsString())) {
            JsonObject passengerJson = response.getAsJsonObject("passenger");
            currentPassenger = new Gson().fromJson(passengerJson, Passenger.class);


            loadScene("fxml/uiPassenger1/PassengerStartScreen.fxml", event);

        } else {
            errorLabel.setText("Login failed: " + response.get("message").getAsString());
            errorLabel.setVisible(true);
        }

    }

    /**
     * Handles the NovaCredits button action.
     * Navigates to the passenger NovaCredits menu screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToPassengerNovaCreditsMenuScreen(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerNovaCreditsMenuScreen.fxml", event);
        refreshPassenger();
        novaCreditsMenuLabel.setText("NovaCredits: " + currentPassenger.getNovaCredits());
    }

    /**
     * Handles the SpaceWalk button action.
     * Navigates to the passenger spacewalk selection screen.
     *
     * @param actionEvent The action event triggered by the UI.
     */
    public void switchToPassengerSpaceWalkSelectionScreen(ActionEvent actionEvent) {
        loadScene("fxml/uiPassenger1/PassengerSpaceWalkSelectionScreen.fxml", actionEvent);
    }

    /**
     * Switches the scene to the PassengerCryptoWalletLoginScreen.
     * This method is called when the user clicks the recharge button for the crypto wallet.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToPassengerCryptoWalletLoginScreen(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerCryptoWalletLoginScreen.fxml", event);
    }

    /**
     * Handles the action when the user clicks the login button for the crypto wallet.
     * This method validates the entered credentials and navigates to the passenger select crypto screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToPassengerSelectCryptoScreen(ActionEvent event) {
        if (cryptoPINTextField.getText().trim().equals(currentPassenger.getPassword()) &&
                cryptoUserTextField.getText().trim().equals(currentPassenger.getUsername())) {
        loadScene("fxml/uiPassenger1/PassengerSelectCryptoScreen.fxml", event);}
        else {
        loadScene("fxml/uiPassenger1/PassengerCryptoLoginDeniedScreen.fxml", event);}
    }

    /**
     * Handles the action when the user clicks the back button from the NovaCredits menu.
     * This method navigates back to the passenger start screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void switchToPassengerRechargeNovaCreditsScreen(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerRechargeNovaCreditsScreen.fxml", event);
        switch (((Button) event.getSource()).getId()) {
            case "bitcoinButton" -> selectedCurrencyLabel.setText("Chosen Currency: Bitcoin");
            case "ethereumButton" -> selectedCurrencyLabel.setText("Chosen Currency: Ethereum");
            case "dogecoinButton" -> selectedCurrencyLabel.setText("Chosen Currency: Dogecoin");
            case "teslaStockButton" -> selectedCurrencyLabel.setText("Chosen Currency: Tesla Stock");
        }
    }

    /**
     * Handles the action when the user clicks the back button from the NovaCredits menu.
     * This method navigates back to the passenger start screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void backToPassengerSelectCryptoScreen(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerSelectCryptoScreen.fxml", event);
    }

    /**
     * Handles the action when the user clicks the confirm recharge button.
     * This method validates the input amount and sends a request to update NovaCredits.
     * If successful, it navigates back to the NovaCredits menu screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void onConfirmRecharge(ActionEvent event) {
        String input = rechargeTextField.getText().trim();


        if (input.isEmpty()) {
            errorLabel.setText("Enter an amount.");
            errorLabel.setVisible(true);
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            errorLabel.setText("Amount is too high or not a valid number.");
            errorLabel.setVisible(true);
            return;
        }


        if (amount < 0) {
            errorLabel.setText("Amount must be positive.");
            errorLabel.setVisible(true);
            return;
        }

        if ((long) amount + currentPassenger.getNovaCredits() > Integer.MAX_VALUE) {
            errorLabel.setText("Maximum amount of NovaCredits exceeded!\nAmount must be less.");
            errorLabel.setVisible(true);
            return;
        }


        JsonObject response = socketService.updateNovaCredits(currentPassenger.getPassengerId(), amount);

        if ("OK".equals(response.get("status").getAsString())) {
            int newBalance = response.get("newBalance").getAsInt();
            currentPassenger.setNovaCredits(newBalance);
            loadScene("fxml/uiPassenger1/PassengerNovaCreditsMenuScreen.fxml", event);
            novaCreditsMenuLabel.setText("NovaCredits: " + currentPassenger.getNovaCredits());
        }
    }

    /**
     * Handles the action when the user starts the application.
     * This method is called when the user clicks the "Let's Go" button.
     * @param event
     */
    public void goToPassengerStartScreen(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerStartScreen.fxml", event);

    }

    /**
     * Handles the action when the user clicks next on the first survey screen.
     * @param event The action event triggered by the UI.
     */
    public void goToNextSurvey(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerSurvey2Screen.fxml", event);
    }

    /**
     * Handles the action when the user submits the survey.
     * This method sends the survey data to the server and navigates to the thank you screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void submitSurvey(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerSurveyThanksScreen.fxml", event);
    }

    private Stage currentStage; // Current stage for scene management

    /**
     * Loads a new scene based on the provided FXML path and event.
     * This method is used for switching between different screens in the application.
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
            this.currentStage = stage;
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest(e -> {
                stopAllPollers();
                socketService.disconnect();
                logger.info(currentPassenger.getUsername() + " disconnected from server.");
            });
            stage.show();



        } catch (Exception e) {
            logger.error("Scene switch failed", e);
        }
    }

    private void stopAllPollers() {
        if (bookingStatusPoller != null) bookingStatusPoller.stop();
        if (surveyPoller != null) surveyPoller.stop();
        if (broadcastPoller != null) broadcastPoller.stop();
        if (checkoutPoller != null) checkoutPoller.stop();
        if (checkInPoller != null) checkInPoller.stop();
    }

    @FXML private VBox contentPane; // VBox for layout

    /**
     * Loads a new scene without an event.
     * This method is used for loading scenes that do not require an action event.
     *
     * @param fxmlPath The path to the FXML file.
     */
    private void loadSceneWithoutEvent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));
            loader.setController(this);
            Parent root = loader.load();


            Stage stage = currentStage;
            stage.setScene(new Scene(root));
            stage.setOnCloseRequest(e -> {
                socketService.disconnect();
                logger.info("ATC disconnected from server.");
            });
            stage.show();

        } catch (Exception e) {
            logger.error("Scene switch failed", e);
        }
    }

    /**
     * Refreshes the current passenger information from the server.
     * This method retrieves the latest passenger data and updates the currentPassenger object locally.
     */
    private void refreshPassenger() {
        currentPassenger = socketService.getPassengerById(currentPassenger.getPassengerId());
    }

    /**
     * Handles the Button action to switch to the PassengerBookingPackageSelectionScreen.
     * @param event The action event triggered by the UI.
     */
    public void switchToPassengerBookingPackageSelectionScreen(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerBookingPackageSelectionScreen.fxml", event);
    }

    /**
     * Handles the Button action to switch to the PassengerBookingReviewBookingsScreen.
     * @param event The action event triggered by the UI.
     */
    public void switchToPassengerReviewBookingsScreen(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerReviewBookingsScreen.fxml", event);
        initializeBookingReviewScreen();
    }

    /**
     * Handles the action when clicking the back button from the booking review screen.
     * @param event The action event triggered by the UI.
     */
    public void backToSpaceWalkSelectionScreen(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerSpaceWalkSelectionScreen.fxml", event);
    }

    /**
     * Handles the action when the user selects a timeslot for the essential package.
     * Navigates to the booking confirmation screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void chooseEssentialTimeslot(ActionEvent event) {
        selectedTier = 1;
        loadScene("fxml/uiPassenger1/PassengerBookingTimeslotSelectionScreen.fxml", event);
        initializeTimeslotScreen();
    }

    /**
     * Handles the action when the user selects a timeslot for the comfort package.
     * Navigates to the booking confirmation screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void chooseComfortTimeslot(ActionEvent event) {
        selectedTier = 2;
        loadScene("fxml/uiPassenger1/PassengerBookingTimeslotSelectionScreen.fxml", event);
        initializeTimeslotScreen();
    }

    /**
     * Handles the action when the user selects a timeslot for the prestige package.
     * Navigates to the booking confirmation screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void choosePrestigeTimeslot(ActionEvent event) {
        selectedTier = 3;
        loadScene("fxml/uiPassenger1/PassengerBookingTimeslotSelectionScreen.fxml", event);
        initializeTimeslotScreen();
    }

    @FXML private VBox timeslotButtonContainer; // VBox to hold timeslot buttons

    /**
     * Initializes the timeslot selection screen.
     * This method retrieves available slots from the server and creates buttons for each slot.
     * When a button is clicked, it navigates to the booking check screen.
     */
    public void initializeTimeslotScreen() {
        List<SpacewalkSlot> slots = socketService.getAvailableSlots();

        for (SpacewalkSlot slot : slots) {
            Button btn = new Button(slot.getSlotTime());
            btn.setOnAction(e -> {
                loadScene("fxml/uiPassenger1/PassengerBookingCheckScreen.fxml", e);
                selectedSlot = slot;
                initializeCheckScreen();
            });

            timeslotButtonContainer.getChildren().add(btn);
        }
    }

    /**
     * Initializes the booking check screen.
     * This method sets the selected package and timeslot labels based on the user's selection.
     */
    public void initializeCheckScreen() {
        tierLabel.setText("Package: " + switch (selectedTier) {
            case 1 -> "Space Essential";
            case 2 -> "Space Comfort";
            case 3 -> "Space Prestige";
            default -> "Unknown Tier";
        });
        timeSlotLabel.setText("Slot: " + selectedSlot.getSlotTime());

    }

    /**
     * Handles the action when the user confirms the booking.
     * This method checks if the passenger has enough NovaCredits and sends the booking request to the server.
     * If successful, it navigates to the booking confirmation screen.
     *
     * @param e The action event triggered by the UI.
     */
    @FXML
    private void onConfirmBooking(ActionEvent e) {
        int cost = switch (selectedTier) {
            case 1 -> 100000;
            case 2 -> 250000;
            case 3 -> 500000;
            default -> 9999;
        };

        if (currentPassenger.getNovaCredits() < cost) {
            loadScene("fxml/uiPassenger1/PassengerBookingNoMoneyScreen.fxml", e);
            comfortNoMoneyLabel.setText(currentPassenger.getNovaCredits() + " NovaCredits");

        } else {

            JsonObject result = socketService.sendBooking(currentPassenger.getPassengerId(), selectedSlot.getSlotId(), selectedTier);
            if ("OK".equals(result.get("status").getAsString())) {
                int updatedCredits = result.get("newBalance").getAsInt();
                currentPassenger.setNovaCredits(updatedCredits);
                loadScene("fxml/uiPassenger1/PassengerBookingThanksScreen.fxml", e);
            } else {
                loadScene("fxml/uiPassenger1/PassengerTimeSlotAlreadyTakenScreen.fxml", e);
            }
        }
    }

    @FXML private VBox bookingReviewContainer; // VBox to display booking reviews

    /**
     * Initializes the booking review screen.
     * This method retrieves the bookings for the current passenger and displays them in the UI.
     */
    public void initializeBookingReviewScreen() {
        List<Booking> bookings = socketService.getBookingsForPassenger(currentPassenger.getPassengerId()).stream().sorted(Comparator.comparing(Booking::getSlotId)).toList();

        bookingReviewContainer.getChildren().clear();

        for (Booking b : bookings) {
            SpacewalkSlot slot = socketService.getSlotById(b.getSlotId());
            String tier = switch (b.getTier()) {
                case 1 -> "Space Essential";
                case 2 -> "Space Comfort";
                case 3 -> "Space Prestige";
                default -> "Unknown";
            };

            HBox row = new HBox(30);
            row.setAlignment(Pos.CENTER);
            row.setStyle("-fx-padding: 10;");

            Label packageLabel = new Label(tier);
            packageLabel.setPrefWidth(200);
            packageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Label timeLabel = new Label(slot.getSlotTime());
            timeLabel.setPrefWidth(200);
            timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Label approvalLabel = new Label(b.getStatus());
            approvalLabel.setPrefWidth(200);
            approvalLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Button cancelButton = new Button("Cancel");
            cancelButton.setPrefWidth(120);
            if ("denied".equals(b.getStatus())) {
                cancelButton.setDisable(true);
            }

            int bookingId = b.getBookingId();
            cancelButton.setOnAction(e -> {
                JsonObject response = socketService.cancelBooking(bookingId);

                if ("OK".equals(response.get("status").getAsString())) {
                    currentPassenger.setNovaCredits(response.get("newBalance").getAsInt());
                    initializeBookingReviewScreen();
                } else {
                    logger.info("Cancel failed: " + response.get("message").getAsString());
                }
            });

            row.getChildren().addAll(packageLabel, timeLabel, approvalLabel, cancelButton);
            bookingReviewContainer.getChildren().add(row);

        }
    }

    /**
     * Handles the action when the user selects a timeslot for the comfort package.
     * Navigates to the booking confirmation screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void selectComfortTimeslot(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerBookingCheckScreen.fxml", event);
    }



    /**
     * Handles the back button action from the booking review screen.
     * Navigates back to the package selection screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void backToPackageSelectionScreen(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerBookingPackageSelectionScreen.fxml", event);
    }

    /**
     * Handles the back button action from the timeslot selection screen.
     * Navigates back to the package selection screen.
     *
     * @param event The action event triggered by the UI.
     */
    public void backToTimeslotSelectionScreen(ActionEvent event) {
        loadScene("fxml/uiPassenger1/PassengerBookingTimeslotSelectionScreen.fxml", event);
    }

    /**
     * Initializes the PassengerController.
     * This method is called when the controller is loaded.
     * It starts polling for booking status, broadcasts, surveys, and checkouts.
     */
    @FXML
    public void initialize() {
        if (socketService.isConnected()) {
            startBookingStatusPolling();
            startBroadcastPolling();
            startSurveyPolling();
            startCheckoutPolling();
        }
    }

    private Timeline surveyPoller; // Timeline for polling survey triggers
    private boolean surveyPollerStarted = false; // Flag to track if the survey poller has started

    /**
     * Starts the survey polling process.
     * This method creates a Timeline that checks for survey triggers every second.
     * If a survey is triggered, it navigates to the survey screen.
     */
    private void startSurveyPolling() {
        if (surveyPollerStarted) return;
        surveyPollerStarted = true;

        surveyPoller = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> checkSurveyTrigger())
        );
        surveyPoller.setCycleCount(Timeline.INDEFINITE);
        surveyPoller.play();
    }

    /**
     * Checks if the survey has been triggered and navigates to the survey screen if so.
     * This method is called periodically to check for survey triggers.
     */
    private void checkSurveyTrigger() {
        if (socketService.isSurveyTriggered()) {
            surveyPoller.stop();
            Platform.runLater(() -> loadSceneWithoutEvent("fxml/uiPassenger1/PassengerSurvey1Screen.fxml"));
        }
    }

    /**
     * Starts the booking status polling process.
     * This method creates a Timeline that checks for booking status changes every second.
     * If a change is detected, it displays an alert with the updated status.
     */
    private void startBookingStatusPolling() {
        bookingStatusPoller = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> checkBookingStatusChanges())
        );
        bookingStatusPoller.setCycleCount(Timeline.INDEFINITE);
        bookingStatusPoller.play();
    }

    /**
     * Checks for changes in booking status and displays alerts if there are any updates.
     * This method compares the current bookings with the previous bookings and shows alerts for any changes.
     */
    private void checkBookingStatusChanges() {
        List<Booking> current = socketService.getBookingsForPassenger(currentPassenger.getPassengerId());

        for (Booking b : current) {
            for (Booking old : previousBookings) {
                if (b.getBookingId() == old.getBookingId() && !b.getStatus().equals(old.getStatus())) {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Booking Update for: " + currentPassenger.getUsername());
                        alert.setHeaderText(null);

                        if (b.getStatus().equals("approved")) {
                            alert.setContentText(currentPassenger.getUsername() +": Your spacewalk booking has been approved!");
                        } else if (b.getStatus().equals("denied")) {
                            alert.setContentText(currentPassenger.getUsername() +": Your spacewalk was denied. NovaCredits refunded.");
                        }

                        alert.show();
                    });
                }
            }
        }

        previousBookings = current;
    }

    private Timeline broadcastPoller; // Timeline for polling broadcasts

    /**
     * Starts the broadcast polling process.
     * This method creates a Timeline that checks for new broadcasts every second.
     * If a new broadcast is detected, it displays an alert with the broadcast message.
     */
    private void startBroadcastPolling() {
        broadcastPoller = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> checkForBroadcast())
        );
        broadcastPoller.setCycleCount(Timeline.INDEFINITE);
        broadcastPoller.play();
    }

    /**
     * Checks for new broadcasts from the server and displays an alert if a new broadcast is detected.
     * The broadcast message is shown in an alert dialog.
     */
    private void checkForBroadcast() {
        JsonObject response = socketService.getLatestBroadcast();

        if (!"OK".equals(response.get("status").getAsString())) return;

        if (!response.has("message") || response.get("message").isJsonNull()) return;

        String message = response.get("message").getAsString();
        long timestamp = response.has("timestamp") ? response.get("timestamp").getAsLong() : 0;

        if (timestamp > lastBroadcastSeen) {
            lastBroadcastSeen = timestamp;

            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Pilot Broadcast");
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.show();
            });
        }
    }


    private Timeline checkoutPoller; // Timeline for polling checkout status
    private Timeline checkInPoller; // Timeline for polling check-in status

    private boolean checkoutPollerStarted = false; // Flag to track if the checkout poller has started

    /**
     * Starts the checkout polling process.
     * This method creates a Timeline that checks if the passenger is checked out every second.
     * If the passenger is checked out, it navigates to the checked-out screen.
     */
    private void startCheckoutPolling() {
        if (checkoutPollerStarted) return;
        checkoutPollerStarted = true;

        checkoutPoller = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> checkCheckoutTrigger())
        );
        checkoutPoller.setCycleCount(Timeline.INDEFINITE);
        checkoutPoller.play();
    }

    /**
     * Checks if the passenger is checked out and updates the UI accordingly.
     * If the passenger is checked out, it stops the checkout poller and navigates to the checked-out screen.
     */
    private void checkCheckoutTrigger() {
        if (socketService.isCheckedOut(currentPassenger.getPassengerId())) {
            checkoutPoller.stop();
            Platform.runLater(() -> {
                loadSceneWithoutEvent("fxml/uiPassenger1/PassengerCheckedOutScreen.fxml");
                checkInButton.setDisable(true);
                startCheckinPolling();
            });
        }
    }

    /**
     * Starts the check-in polling process.
     * This method creates a Timeline that checks if the passenger is checked in every second.
     * If the passenger is not checked out, it enables the check-in button.
     */
    private void startCheckinPolling() {
        checkInPoller = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> checkCheckInTrigger())
        );
        checkInPoller.setCycleCount(Timeline.INDEFINITE);
        checkInPoller.play();
    }

    /**
     * Checks if the passenger is checked in and updates the UI accordingly.
     * If the passenger is not checked out, the check-in button is enabled.
     */
    private void checkCheckInTrigger() {
        if (!socketService.isCheckedOut(currentPassenger.getPassengerId())) {
            checkInPoller.stop();
            Platform.runLater(() -> {
                checkInButton.setDisable(false);
            });
        }
    }

    /**
     * Handles the check-in button action.
     * Navigates to the passenger start screen and starts checkout polling to enable checking out again.
     *
     * @param e The action event triggered by the UI.
     */
    public void checkInButton(ActionEvent e){
        loadScene("fxml/uiPassenger1/PassengerStartScreen.fxml", e);
        checkoutPollerStarted = false;
        startCheckoutPolling();
    }


}