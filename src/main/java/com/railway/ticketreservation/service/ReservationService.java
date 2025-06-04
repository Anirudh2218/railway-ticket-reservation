package com.railway.ticketreservation.service;

import com.railway.ticketreservation.dto.PassengerDTO;
import com.railway.ticketreservation.dto.PaymentDTO;
import com.railway.ticketreservation.event.TicketEvent;
import com.railway.ticketreservation.exception.*;
import com.railway.ticketreservation.model.Passenger;
import com.railway.ticketreservation.model.Payment;
import com.railway.ticketreservation.model.Ticket;
import com.railway.ticketreservation.model.Train;
import com.railway.ticketreservation.repository.PassengerRepository;
import com.railway.ticketreservation.repository.PaymentRepository;
import com.railway.ticketreservation.repository.TicketRepository;
import com.railway.ticketreservation.repository.TrainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    private final TrainRepository trainRepository;
    private final PassengerRepository passengerRepository;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ReservationService(TrainRepository trainRepository,
                              PassengerRepository passengerRepository,
                              PaymentRepository paymentRepository,
                              TicketRepository ticketRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.trainRepository = trainRepository;
        this.passengerRepository = passengerRepository;
        this.paymentRepository = paymentRepository;
        this.ticketRepository = ticketRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Ticket bookTicket(int trainNumber, PassengerDTO passengerDTO, PaymentDTO paymentDTO)
            throws InvalidAgeException, InvalidContactException, InvalidAadhaarException, InvalidPaymentException {
        String currentDay = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        Train train = trainRepository.findById(trainNumber)
                .orElseThrow(() -> new IllegalArgumentException("Invalid train number."));
        if (!train.operatesOnDay(currentDay)) {
            throw new IllegalArgumentException("Train does not operate on " + currentDay);
        }

        // Use pessimistic locking to prevent concurrent seat booking
        Train lockedTrain = trainRepository.findByIdForUpdate(trainNumber)
                .orElseThrow(() -> new IllegalStateException("Train not found."));
        int seatNumber = lockedTrain.assignSeat();
        if (seatNumber == -1) {
            throw new IllegalStateException("No seats available.");
        }
        trainRepository.save(lockedTrain);

        Passenger passenger = new Passenger(passengerDTO.getName(), passengerDTO.getAge(),
                passengerDTO.getContact(), passengerDTO.getAadhaar());
        passengerRepository.save(passenger);

        Payment payment = new Payment();
        payment.setPaymentMethod(paymentDTO.getPaymentMethod());
        payment.setAmount(train.getBaseFare());
        payment.processPayment(paymentDTO.getPaymentDetails());
        paymentRepository.save(payment);

        Ticket ticket = new Ticket();
        ticket.setTrain(lockedTrain);
        ticket.setPassenger(passenger);
        ticket.setPayment(payment);
        ticket.setSeatNumber(seatNumber);
        ticket.setStatus("Booked");
        ticketRepository.save(ticket);

        eventPublisher.publishEvent(new TicketEvent(this, passenger.getName(),
                "Ticket booked successfully! Payment of ₹" + payment.getAmount() + " processed."));

        return ticket;
    }

    @Transactional
    public void cancelTicket(int ticketId) {
        logger.debug("Starting cancelTicket for ticketId: {}", ticketId);
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            throw new IllegalArgumentException("Ticket ID not found.");
        }
        Ticket ticket = ticketOpt.get();
        Train train = ticket.getTrain();
        logger.debug("Before increment: availableSeats = {}", train.getAvailableSeats());
        train.releaseSeat();
        logger.debug("After increment: availableSeats = {}", train.getAvailableSeats());
        ticket.cancel();
        logger.debug("After ticket.cancel(): availableSeats = {}", train.getAvailableSeats());
        ticketRepository.save(ticket);
        logger.debug("After ticket save: availableSeats = {}", train.getAvailableSeats());
        trainRepository.save(train);
        logger.debug("After train save: availableSeats = {}", train.getAvailableSeats());
        eventPublisher.publishEvent(new TicketEvent(this, ticket.getPassenger().getName(),
                "Ticket canceled. Refund initiated for ₹" + ticket.getPayment().getAmount()));
        logger.debug("After event publish: availableSeats = {}", train.getAvailableSeats());
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }
}