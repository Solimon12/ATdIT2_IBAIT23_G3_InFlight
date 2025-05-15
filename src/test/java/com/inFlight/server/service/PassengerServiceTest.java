package com.inFlight.server.service;

import com.inFlight.server.dao.PassengerDAO;
import com.inFlight.shared.model.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PassengerServiceTest {

    private PassengerDAO mockDAO;
    private PassengerService passengerService;

    @BeforeEach
    void setUp() {
        mockDAO = mock(PassengerDAO.class);
        passengerService = new PassengerService(mockDAO); // requires constructor injection for test
    }

    @Test
    void testFindPassengerByUsername() {
        Passenger mockPassenger = new Passenger(1, "john_doe", "secret", 100);
        when(mockDAO.getPassengerByUsername("john_doe")).thenReturn(mockPassenger);

        Passenger result = passengerService.findPassenger("john_doe");

        assertNotNull(result);
        assertEquals("john_doe", result.getUsername());
        verify(mockDAO, times(1)).getPassengerByUsername("john_doe");
    }

    @Test
    void testTopUpCredits_passengerExists() {
        Passenger mockPassenger = new Passenger(1, "jane", "pw", 50);
        when(mockDAO.getPassengerById(1)).thenReturn(mockPassenger);

        passengerService.topUpCredits(1, 30);

        verify(mockDAO, times(1)).updateNovaCredits(1, 80); // 50 + 30
    }

    @Test
    void testTopUpCredits_passengerNotFound() {
        when(mockDAO.getPassengerById(999)).thenReturn(null);

        passengerService.topUpCredits(999, 20);

        verify(mockDAO, never()).updateNovaCredits(anyInt(), anyInt());
    }

    @Test
    void testDeductCredits_success() {
        Passenger mockPassenger = new Passenger(1, "alice", "pw", 100);
        when(mockDAO.getPassengerById(1)).thenReturn(mockPassenger);

        boolean result = passengerService.deductCredits(1, 40);

        assertTrue(result);
        verify(mockDAO).updateNovaCredits(1, 60); // 100 - 40
    }

    @Test
    void testDeductCredits_notEnoughCredits() {
        Passenger mockPassenger = new Passenger(1, "bob", "pw", 20);
        when(mockDAO.getPassengerById(1)).thenReturn(mockPassenger);

        boolean result = passengerService.deductCredits(1, 50);

        assertFalse(result);
        verify(mockDAO, never()).updateNovaCredits(anyInt(), anyInt());
    }

    @Test
    void testDeductCredits_passengerNotFound() {
        when(mockDAO.getPassengerById(42)).thenReturn(null);

        boolean result = passengerService.deductCredits(42, 10);

        assertFalse(result);
        verify(mockDAO, never()).updateNovaCredits(anyInt(), anyInt());
    }
}