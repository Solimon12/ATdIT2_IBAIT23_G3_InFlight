package com.inFlight.server;

import com.inFlight.server.socket.ClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The Server class is responsible for starting the server and listening for incoming client connections.
 * It creates a new thread for each client connection to handle communication.
 */
public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static final int PORT = 5555; // Port number for the server to listen on

    /**
     * startServer method initializes the server and starts listening for incoming client connections.
     * It creates a new ClientHandler thread for each client connection.
     */
    public static void startServer(){
        // Create a ServerSocket to listen for incoming connections
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server started listening on port " + PORT);


            // Continuously accept incoming client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Client connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        // Handle exceptions related to server socket operations
        } catch (IOException e) {
            logger.error("Error starting server: " + e.getMessage());
        }
    }
}
