package com.inFlight.shared.protocol;

/**
 * ActionType is an enum that defines the various actions that can be performed in the application.
 * Each action corresponds to a specific operation that can be requested by the client or server.
 */
public enum ActionType {
    LOGIN,
    GET_BOOKINGS,
    SEND_CHAT,
    GET_CHAT,
    UPDATE_NOVACREDITS,
    TRIGGER_SURVEY,
    GET_AVAILABLE_SLOTS,
    BOOK_SLOT,
    GET_BOOKINGS_FOR_PASSENGER,
    GET_SLOT_BY_ID,
    CANCEL_BOOKING,
    APPROVE_BOOKING,
    DENY_BOOKING,
    GET_PASSENGER_BY_ID,
    TRIGGER_BROADCAST,
    GET_BROADCAST,
    GET_LAST_CANCELLATION,
    CHECK_SURVEY_TRIGGER,
    GET_ALL_PASSENGERS,
    CHECK_OUT_PASSENGER,
    CHECK_CHECKOUT_STATUS,
    SET_CHECKED_OUT_STATUS,
    REGISTER_PHOTOGRAPHER,
    GET_PHOTOGRAPHER_BY_NAME,
    GET_PHOTOGRAPHER_BY_ID,
    SET_PHOTOGRAPHER_CHECKED_OUT,
    GET_INVENTORY_BY_ROLE,
    UPDATE_INVENTORY_ITEM
    // Add any additional action types here as needed
}

