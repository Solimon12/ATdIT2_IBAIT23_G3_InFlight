package com.inFlight.server.service;

import com.inFlight.server.dao.PhotographerDAO;
import com.inFlight.shared.model.Photographer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PhotographerServiceTest {

    private PhotographerService service;
    private PhotographerDAO mockDAO;

    @BeforeEach
    public void setUp() {
        mockDAO = mock(PhotographerDAO.class);
        service = new PhotographerService(mockDAO);
    }

    @Test
    public void testRegisterPhotographer_WhenNotExists() {
        when(mockDAO.getPhotographerByName("Alex")).thenReturn(null);

        service.registerPhotographer("Alex");

        verify(mockDAO).insertPhotographer(argThat(p ->
                p.getName().equals("Alex") && !p.isCheckedOut() && p.getId() == -1));
    }

    @Test
    public void testRegisterPhotographer_WhenAlreadyExists() {
        Photographer existing = new Photographer(1, "Alex", false);
        when(mockDAO.getPhotographerByName("Alex")).thenReturn(existing);

        service.registerPhotographer("Alex");

        verify(mockDAO, never()).insertPhotographer(any());
    }

    @Test
    public void testGetByName_ReturnsPhotographer() {
        Photographer expected = new Photographer(2, "Taylor", true);
        when(mockDAO.getPhotographerByName("Taylor")).thenReturn(expected);

        Photographer result = service.getByName("Taylor");

        assertNotNull(result);
        assertEquals("Taylor", result.getName());
        assertTrue(result.isCheckedOut());
        assertEquals(2, result.getId());
    }

    @Test
    public void testGetById_ReturnsPhotographer() {
        Photographer expected = new Photographer(3, "Jordan", false);
        when(mockDAO.getPhotographerById(3)).thenReturn(expected);

        Photographer result = service.getById(3);

        assertNotNull(result);
        assertEquals("Jordan", result.getName());
        assertFalse(result.isCheckedOut());
        assertEquals(3, result.getId());
    }

    @Test
    public void testSetCheckedOut_CallsDAO() {
        service.setCheckedOut(4, true);

        verify(mockDAO).setCheckedOutStatus(4, true);
    }
}