package com.railway.ticketreservation.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ticketId;
    @ManyToOne
    private Train train;
    @ManyToOne
    private Passenger passenger;
    private int seatNumber;
    @ManyToOne
    private Payment payment;
    private String status;

    public void cancel() {
        this.status = "Canceled";
    }
}