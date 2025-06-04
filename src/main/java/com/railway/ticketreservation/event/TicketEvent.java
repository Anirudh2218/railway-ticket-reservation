package com.railway.ticketreservation.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TicketEvent extends ApplicationEvent {
    private final String passengerName;
    private final String message;

    public TicketEvent(Object source, String passengerName, String message) {
        super(source);
        this.passengerName = passengerName;
        this.message = message;
    }
}