package com.railway.ticketreservation.controller;

import com.railway.ticketreservation.dto.BookingRequestDTO;
import com.railway.ticketreservation.exception.*;
import com.railway.ticketreservation.model.Ticket;
import com.railway.ticketreservation.model.Train;
import com.railway.ticketreservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/trains")
    public ResponseEntity<List<Train>> getAllTrains() {
        return ResponseEntity.ok(reservationService.getAllTrains());
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(reservationService.getAllTickets());
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookTicket(@RequestParam int trainNumber,
                                        @RequestBody BookingRequestDTO bookingRequestDTO) {
        try {
            Ticket ticket = reservationService.bookTicket(trainNumber,
                    bookingRequestDTO.getPassengerDTO(),
                    bookingRequestDTO.getPaymentDTO());
            return ResponseEntity.ok(ticket);
        } catch (InvalidAgeException | InvalidContactException | InvalidAadhaarException | InvalidPaymentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/cancel/{ticketId}")
    public ResponseEntity<String> cancelTicket(@PathVariable int ticketId) {
        try {
            reservationService.cancelTicket(ticketId);
            return ResponseEntity.ok("Ticket canceled successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}