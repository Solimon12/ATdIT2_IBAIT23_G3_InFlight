package com.inFlight.server.service;

import com.inFlight.server.dao.PhotographerDAO;
import com.inFlight.shared.model.Photographer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PhotographerService is a service class that handles photographer-related operations.
 * It interacts with the PhotographerDAO to manage photographers.
 */
public class PhotographerService {
    private static final Logger logger = LoggerFactory.getLogger(PhotographerService.class);

    private final PhotographerDAO photographerDAO; // DAO for photographer operations

    /**
     * Default constructor initializes the DAO.
     */
    public PhotographerService () {
        this.photographerDAO = new PhotographerDAO();
    }

    /**
     * Constructor for dependency injection
     *
     * @param photographerDAO the PhotographerDAO instance to use
     */
    public PhotographerService (PhotographerDAO photographerDAO) {
        this.photographerDAO = photographerDAO;
    }

    /**
     * Registers a photographer by their name.
     * If the photographer already exists, it does nothing.
     *
     * @param name the name of the photographer to register
     */
    public void registerPhotographer(String name) {
        Photographer existing = photographerDAO.getPhotographerByName(name);
        if (existing == null) {
            photographerDAO.insertPhotographer(new Photographer(name));
        }
    }

    /**
     * Retrieves a photographer by their name.
     *
     * @param name the name of the photographer to retrieve
     * @return the Photographer object if found, null otherwise
     */
    public Photographer getByName(String name) {
        return photographerDAO.getPhotographerByName(name);
    }

    /**
     * Retrieves a photographer by their ID.
     *
     * @param id the ID of the photographer to retrieve
     * @return the Photographer object if found, null otherwise
     */
    public Photographer getById(int id) {
        return photographerDAO.getPhotographerById(id);
    }

    /**
     * Sets the checked-out status of a photographer.
     *
     * @param id     the ID of the photographer
     * @param status the checked-out status to set
     */
    public void setCheckedOut(int id, boolean status) {
        photographerDAO.setCheckedOutStatus(id, status);
    }
}
