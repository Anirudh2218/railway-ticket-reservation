package com.railway.ticketreservation.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TicketEventListener {
    @EventListener
    public void handleTicketEvent(TicketEvent event) {
        System.out.println("Notification to " + event.getPassengerName() + ": " + event.getMessage());
    }
}