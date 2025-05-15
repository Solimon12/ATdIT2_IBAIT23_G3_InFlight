package com.inFlight.client.controller;

import com.inFlight.server.Server;
import com.inFlight.shared.model.UserRole;
import com.inFlight.shared.util.RoleWindowLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller class for managing the role selection screen in the application.
 * This class handles the actions for selecting different user roles and starting the server.
 */
public class RoleSelectorController {
    private static final Logger logger = LoggerFactory.getLogger(RoleSelectorController.class);

    public Button ServerButton; // Button to start the server

    /**
     * launches a passenger role window
     */
    @FXML
    private void onPassenger() {
        new RoleWindowLoader().launchRoleWindow(UserRole.PASSENGER);
    }

    /**
     * launches a pilot role window
     */
    @FXML
    private void onPilot() {
        new RoleWindowLoader().launchRoleWindow(UserRole.PILOT);
    }

    /**
     * launches an attendant role window
     */
    @FXML
    private void onAttendant() {
        new RoleWindowLoader().launchRoleWindow(UserRole.ATTENDANT);
    }

    /**
     * launches a photographer role window
     */
    @FXML
    private void onPhotographer() {
        new RoleWindowLoader().launchRoleWindow(UserRole.PHOTOGRAPHER);
    }

    /**
     * launches an air traffic controller role window
     */
    @FXML
    private void onATC() {
        new RoleWindowLoader().launchRoleWindow(UserRole.ATC);
    }

    /**
     * Starts the server when the server button is clicked and disables the button to prevent multiple clicks.
     *
     * @param event The action event triggered by the UI.
     */
    @FXML
    public void onServer(ActionEvent event) {
        ServerButton.setDisable(true);
        new Thread(() -> {
            try {
                Server.startServer();
            } catch (Exception e) {
                ServerButton.setDisable(false);
                // If the server fails to start, re-enable the button and log the error
               logger.error("Failed to start server", e);
            }
        }).start();
    }
}
