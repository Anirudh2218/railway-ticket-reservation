package com.railway.ticketreservation.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Train {
    @Id
    private int trainNumber;
    private String trainName;
    private String type;
    private String source;
    private String destination;
    private String arrivalTime;
    private String departureTime;
    private String daysOfOperation;
    private int availableSeats;
    private int totalSeats; // New field for total seats
    private double baseFare;

    // Constructor for DataInitializer
    public Train(int trainNumber, String trainName, String type, String source, String destination,
                 String arrivalTime, String departureTime, String daysOfOperation, int availableSeats, double baseFare) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.daysOfOperation = daysOfOperation;
        this.availableSeats = availableSeats;
        this.totalSeats = availableSeats; // Initialize total seats
        this.baseFare = baseFare;
    }

    // Default constructor for JPA
    public Train() {}

    public int assignSeat() {
        if (availableSeats > 0) {
            int assignedSeat = totalSeats - availableSeats + 1;
            availableSeats--;
            return assignedSeat;
        }
        return -1; // Indicate no seats available
    }

    public void releaseSeat() {
        availableSeats++;
    }

    public boolean operatesOnDay(String currentDay) {
        if (daysOfOperation.equalsIgnoreCase("Daily")) {
            return true;
        }
        return daysOfOperation.contains(currentDay);
    }
}