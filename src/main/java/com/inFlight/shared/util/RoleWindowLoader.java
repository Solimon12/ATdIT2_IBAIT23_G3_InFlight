package com.inFlight.shared.util;

import com.inFlight.client.socket.ClientSocketService;
import com.inFlight.shared.model.UserRole;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.Map;

/**
 * RoleWindowLoader is responsible for loading the appropriate FXML file based on the user's role.
 * It uses an EnumMap to map UserRole to the corresponding FXML file path.
 */
public class RoleWindowLoader {
    private static final Logger logger = LoggerFactory.getLogger(RoleWindowLoader.class);

    private final Map<UserRole, String> fxmlMap = new EnumMap<>(UserRole.class); // Map to store FXML file paths

     {
        fxmlMap.put(UserRole.PASSENGER, "fxml/uiPassenger1/PassengerWelcomeScreen.fxml");
        fxmlMap.put(UserRole.PILOT, "fxml/uiPilot/PilotWelcomeScreen.fxml");
        fxmlMap.put(UserRole.ATTENDANT, "fxml/uiFlightAttendant/FlightAttendantWelcomeScreen.fxml");
        fxmlMap.put(UserRole.PHOTOGRAPHER, "fxml/uiPhotographer/PhotographerWelcomeScreen.fxml");
        fxmlMap.put(UserRole.ATC, "fxml/uiAirTraffic/AirTrafficWelcomeScreen.fxml");
    }

    /**
     * Launches the appropriate role window based on the user's role.
     *
     * @param role The UserRole of the user.
     * @throws IllegalArgumentException if the role is not recognized.
     */
    public void launchRoleWindow(UserRole role) {
        try {
            FXMLLoader loader = new FXMLLoader(RoleWindowLoader.class.getClassLoader().getResource(fxmlMap.get(role)));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(role.name() + " View");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            logger.error("Failed to load role window: " + role, e);
        }
    }
}

