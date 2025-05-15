package com.inFlight.server.socket;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.Socket;
import static org.mockito.Mockito.*;

public class ClientHandlerTest {

    @Test
    public void testClientHandlerProcessesInputAndResponds() throws IOException {
        // Given
        Socket mockSocket = mock(Socket.class);
        ProtocolHandler mockProtocolHandler = mock(ProtocolHandler.class);

        // Prepare streams
        PipedInputStream clientInput = new PipedInputStream();
        PipedOutputStream inputToServer = new PipedOutputStream(clientInput);

        PipedOutputStream clientOutput = new PipedOutputStream();
        PipedInputStream outputFromServer = new PipedInputStream(clientOutput);

        when(mockSocket.getInputStream()).thenReturn(clientInput);
        when(mockSocket.getOutputStream()).thenReturn(clientOutput);

        // Prepare fake message and response
        String request = "{\"action\":\"PING\",\"data\":{}}";
        String expectedResponse = "{\"status\":\"OK\"}";

        when(mockProtocolHandler.handle(request)).thenReturn(expectedResponse);

        // Create and start handler thread
        ClientHandler handler = new ClientHandler(mockSocket, mockProtocolHandler);
        handler.start();

        // Send request to handler
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(inputToServer));
        writer.write(request);
        writer.newLine();
        writer.flush();

        // Read response
        BufferedReader reader = new BufferedReader(new InputStreamReader(outputFromServer));
        String response = reader.readLine();

        // Assert
        assert response.equals(expectedResponse);

        // Cleanup
        handler.interrupt();
    }
}