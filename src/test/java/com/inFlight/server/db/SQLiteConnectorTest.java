package com.inFlight.server.db;

import com.inFlight.server.db.SQLiteConnector;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SQLiteConnectorTest {
    @Test
    void testGetConnectionReturnsValidConnection() throws SQLException {
        // Arrange: mock connection and DriverManager
        Connection mockConnection = mock(Connection.class);

        try (MockedStatic<DriverManager> driverManagerMockedStatic = mockStatic(DriverManager.class)) {
            driverManagerMockedStatic
                    .when(() -> DriverManager.getConnection("jdbc:sqlite:src/main/resources/inFlightDB.db"))
                    .thenReturn(mockConnection);

            // Act
            Connection connection = SQLiteConnector.getConnection();

            // Assert
            assertNotNull(connection);
            assertEquals(mockConnection, connection);
            driverManagerMockedStatic.verify(() ->
                    DriverManager.getConnection("jdbc:sqlite:src/main/resources/inFlightDB.db"), times(1));
        }
    }
}