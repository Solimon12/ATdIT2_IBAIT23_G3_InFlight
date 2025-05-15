package com.inFlight.server.service;

import com.inFlight.server.dao.PassengerDAO;
import com.inFlight.shared.model.Passenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * PassengerService is a service class that handles passenger-related operations.
 * It interacts with the PassengerDAO to manage passenger data.
 */
public class PassengerService {
    private static final Logger logger = LoggerFactory.getLogger(PassengerService.class);

    private final PassengerDAO passengerDAO; // DAO for passenger operations

    /**
     * Default constructor initializes the DAO.
     */
    public PassengerService() {
        this.passengerDAO = new PassengerDAO();
    }

    /**
     * Constructor for dependency injection
     */
    public PassengerService(PassengerDAO passengerDAO) {
        this.passengerDAO = passengerDAO;
    }

    /**
     * finds a passenger by their username
     * @param username the username of the passenger to find
     * @return the Passenger object if found, null otherwise
     */
    public Passenger findPassenger(String username) {
        return passengerDAO.getPassengerByUsername(username);
    }

    /**
     * top up novaCredits for a passenger
     * @param passengerId the ID of the passenger
     * @param amount the amount to top up
     */
    public void topUpCredits(int passengerId, int amount) {
        Passenger passenger = passengerDAO.getPassengerById(passengerId);
        if (passenger != null) {
            int newBalance = passenger.getNovaCredits() + amount;
            passengerDAO.updateNovaCredits(passengerId, newBalance);
            logger.info("NovaCredits topped up. New balance: " + newBalance);
        } else {
            logger.info("Passenger not found for top-up.");
        }
    }

    /**
     * deduct novaCredits for a passenger
     * @param passengerId the ID of the passenger
     * @param amount the amount to deduct
     * @return true if the deduction was successful, false otherwise
     */
    public boolean deductCredits(int passengerId, int amount) {
        Passenger passenger = passengerDAO.getPassengerById(passengerId);
        if (passenger != null && passenger.getNovaCredits() >= amount) {
            int newBalance = passenger.getNovaCredits() - amount;
            passengerDAO.updateNovaCredits(passengerId, newBalance);
            logger.info("Deducted " + amount + " novaCredits. Remaining: " + newBalance);
            return true;
        } else {
            logger.info("Not enough novaCredits or passenger not found.");
            return false;
        }
    }


}
