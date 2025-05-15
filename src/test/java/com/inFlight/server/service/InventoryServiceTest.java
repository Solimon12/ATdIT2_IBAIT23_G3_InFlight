package com.inFlight.server.service;

import com.inFlight.server.dao.InventoryItemDAO;
import com.inFlight.shared.model.InventoryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    private InventoryItemDAO mockDAO;
    private InventoryService service;

    @BeforeEach
    void setUp() {
        mockDAO = mock(InventoryItemDAO.class);
        service = new InventoryService(mockDAO);
    }

    @Test
    void testAddItem() {
        service.addItem("Camera", "New", true, "photographer");

        verify(mockDAO).insertItem(argThat(item ->
                item.getName().equals("Camera") &&
                        item.getCondition().equals("New") &&
                        item.isAvailable() &&
                        item.getOwnerRole().equals("photographer") &&
                        !item.isCheckedOut()
        ));
    }

    @Test
    void testGetItemsByRole() {
        InventoryItem item = new InventoryItem(1, "Drill", "Used", true, "attendant", false);
        when(mockDAO.getItemsByRole("attendant")).thenReturn(List.of(item));

        List<InventoryItem> result = service.getItemsByRole("attendant");

        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getName());
        verify(mockDAO).getItemsByRole("attendant");
    }

    @Test
    void testUpdateItem() {
        InventoryItem item = new InventoryItem(5, "Helmet", "Good", true, "attendant", false);
        service.updateItem(item);

        verify(mockDAO).updateItem(item);
    }
}