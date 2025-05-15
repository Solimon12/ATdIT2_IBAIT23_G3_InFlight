package com.inFlight.server.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * The ClientHandler class is responsible for handling communication with a connected client.
 * It reads input from the client, processes it using a ProtocolHandler, and sends responses back to the client.
 */
public class ClientHandler extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private final Socket clientSocket;
    private final ProtocolHandler handler;

    /**
     * Constructor for ClientHandler.
     * Handles communication with the client using the provided socket.
     * @param socket
     */
    public ClientHandler(Socket socket) {
        this.clientSocket = socket; // Socket for communication with the client
        this.handler = new ProtocolHandler(); // ProtocolHandler for processing client requests
    }

    /**
     * Constructor for dependency injection.
     */
    public ClientHandler(Socket socket, ProtocolHandler handler) {
        this.clientSocket = socket; // Socket for communication with the client
        this.handler = handler; // ProtocolHandler for processing client requests
    }

    /**
     * The run method is executed when the thread is started.
     * It continuously reads input from the client, processes it using the ProtocolHandler,
     * and sends responses back to the client.
     */
    public void run() {
        try (
                // Create input and output streams for communication with the client
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
        ) {
            String input;
            // Continuously read input from the client
            while ((input = in.readLine()) != null) {
                String response = handler.handle(input);
                out.write(response);
                out.newLine();
                out.flush();
            }
        // Handle exceptions related to input/output operations
        } catch (IOException e) {
            logger.info("Client disconnected: " + clientSocket.getInetAddress());
        }
    }
}