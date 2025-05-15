package com.inFlight.shared.protocol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionTypeTest {

    @Test
    void testEnumContainsAllExpectedConstants() {
        ActionType[] expected = {
                ActionType.LOGIN,
                ActionType.GET_BOOKINGS,
                ActionType.SEND_CHAT,
                ActionType.GET_CHAT,
                ActionType.UPDATE_NOVACREDITS,
                ActionType.TRIGGER_SURVEY,
                ActionType.GET_AVAILABLE_SLOTS,
                ActionType.BOOK_SLOT,
                ActionType.GET_BOOKINGS_FOR_PASSENGER,
                ActionType.GET_SLOT_BY_ID,
                ActionType.CANCEL_BOOKING,
                ActionType.APPROVE_BOOKING,
                ActionType.DENY_BOOKING,
                ActionType.GET_PASSENGER_BY_ID,
                ActionType.TRIGGER_BROADCAST,
                ActionType.GET_BROADCAST,
                ActionType.GET_LAST_CANCELLATION,
                ActionType.CHECK_SURVEY_TRIGGER,
                ActionType.GET_ALL_PASSENGERS,
                ActionType.CHECK_OUT_PASSENGER,
                ActionType.CHECK_CHECKOUT_STATUS,
                ActionType.SET_CHECKED_OUT_STATUS,
                ActionType.REGISTER_PHOTOGRAPHER,
                ActionType.GET_PHOTOGRAPHER_BY_NAME,
                ActionType.GET_PHOTOGRAPHER_BY_ID,
                ActionType.SET_PHOTOGRAPHER_CHECKED_OUT,
                ActionType.GET_INVENTORY_BY_ROLE,
                ActionType.UPDATE_INVENTORY_ITEM
        };

        ActionType[] actual = ActionType.values();
        assertArrayEquals(expected, actual, "All ActionType enum constants should match exactly.");
    }

    @Test
    void testValueOfReturnsCorrectEnum() {
        for (ActionType action : ActionType.values()) {
            String name = action.name();
            assertEquals(action, ActionType.valueOf(name), "Enum constant mismatch for: " + name);
        }
    }

    @Test
    void testInvalidEnumNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> ActionType.valueOf("UNKNOWN_ACTION"));
    }

    @Test
    void testEnumCount() {
        assertEquals(28, ActionType.values().length, "Enum should contain 28 constants.");
    }
}