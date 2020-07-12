package com.exarlabs.timescheduler.model.event;

/**
 * Event markers, specific point during the event when anotification should be sent.
 */
public enum Marker {
    NO_EVENT,
    MARKER,
    END,
    TEN_MINUTES_REMINDER,
    BREAK_3_2_1;
}
