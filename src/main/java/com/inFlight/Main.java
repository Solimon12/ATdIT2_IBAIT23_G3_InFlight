package com.inFlight;

import com.inFlight.server.db.SQLiteConnector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Main class for the JavaFX application.
 * This class initializes the JavaFX application and sets up the initial scene.
 * It also handles the database reset on application close for Demonstration.
 */
public class Main extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    /**
     * Starts the JavaFX application.
     * Loads the initial FXML file and sets up the primary stage.
     *
     * @param primaryStage The primary stage for this application.
     * @throws Exception If an error occurs during loading the FXML file.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/role_selector.fxml"));
        primaryStage.setTitle("Select Role");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            resetDatabase();
            System.exit(0);
        });
    }

    /**
     * Resets the database to a demo state.
     * This method clears bookings, messages, and resets inventory and passengers.
     */
    public static void resetDatabase() {
        try (Connection conn = SQLiteConnector.getConnection();
             Statement stmt = conn.createStatement()) {

            // Clear bookings and messages
            stmt.executeUpdate("DELETE FROM bookings;");
            stmt.executeUpdate("INSERT INTO bookings (passenger_id, slot_id, tier, status) VALUES (1, 3, 1, 'pending');");
            stmt.executeUpdate("INSERT INTO bookings (passenger_id, slot_id, tier, status) VALUES (1, 6, 3,'approved');");
            stmt.executeUpdate("INSERT INTO bookings (passenger_id, slot_id, tier, status) VALUES (2, 8, 2,'denied');");
            stmt.executeUpdate("DELETE FROM chat_message;");

            // Reset inventory
            stmt.executeUpdate("UPDATE inventory_item SET checked_out = 0, available = 1, condition = 'Perfect. Approved by Ground Crew' WHERE owner_role = 'Attendant';");
            stmt.executeUpdate("UPDATE inventory_item SET checked_out = 0, available = 1, condition = 'Perfect. Tested by Agency' WHERE owner_role = 'Photographer';");

            // Reset passengers
            stmt.executeUpdate("UPDATE passengers SET checked_out = 0;");
            stmt.executeUpdate("UPDATE passengers SET novaCredits = 1000000 WHERE passenger_id = 1;");
            stmt.executeUpdate("UPDATE passengers SET novaCredits = 0 WHERE passenger_id = 2;");

            // Reset photographer
            stmt.executeUpdate("UPDATE photographer SET checked_out = 0;");

            // Reset timeslots
            stmt.executeUpdate("UPDATE spacewalk_slots SET available = 1 WHERE slot_id NOT IN (3,6,8);");
            stmt.executeUpdate("UPDATE spacewalk_slots SET available = 0 WHERE slot_id IN (3,6,8);");

            logger.info("Database reset to demo state.");

        } catch (SQLException e) {
            logger.error("Failed to reset DB: " + e.getMessage());
        }
    }

    /**
     * Main method to launch the JavaFX application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
