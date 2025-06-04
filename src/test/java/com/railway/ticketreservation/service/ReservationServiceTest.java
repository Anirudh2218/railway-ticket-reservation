package com.railway.ticketreservation.service;

import com.railway.ticketreservation.dto.PassengerDTO;
import com.railway.ticketreservation.dto.PaymentDTO;
import com.railway.ticketreservation.event.TicketEvent;
import com.railway.ticketreservation.exception.*;
import com.railway.ticketreservation.model.*;
import com.railway.ticketreservation.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTest {
    @Mock private TrainRepository trainRepository;
    @Mock private PassengerRepository passengerRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private ReservationService reservationService;

    private PassengerDTO passengerDTO;
    private PaymentDTO paymentDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passengerDTO = new PassengerDTO();
        passengerDTO.setName("John Doe");
        passengerDTO.setAge(25);
        passengerDTO.setContact("1234567890");
        passengerDTO.setAadhaar("123456789012");
        paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentMethod("UPI");
        paymentDTO.setPaymentDetails("john@upi");
    }

    @Test
    void testBookTicketSuccess() throws Exception {
        Train train = new Train(16589, "Rani Chennamma Express", "Express", "KSR Bengaluru", "Miraj",
                "07:45", "07:50", "Wed", 50, 750.0); // Set to today (Wed, June 4, 2025)
        when(trainRepository.findById(16589)).thenReturn(Optional.of(train));
        when(passengerRepository.save(any(Passenger.class))).thenAnswer(i -> i.getArgument(0));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));
        when(ticketRepository.findAll()).thenReturn(List.of());
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));
        when(trainRepository.save(any(Train.class))).thenAnswer(i -> i.getArgument(0));

        Ticket ticket = reservationService.bookTicket(16589, passengerDTO, paymentDTO);
        assertNotNull(ticket);
        assertEquals("Booked", ticket.getStatus());
        assertEquals(50, ticket.getSeatNumber());
        verify(trainRepository, times(1)).save(train);
        verify(eventPublisher, times(1)).publishEvent(any(TicketEvent.class));
    }

    @Test
    void testBookTicketInvalidAge() {
        Train train = new Train(16589, "Rani Chennamma Express", "Express", "KSR Bengaluru", "Miraj",
                "07:45", "07:50", "Wed", 50, 750.0);
        passengerDTO.setAge(15);
        when(trainRepository.findById(16589)).thenReturn(Optional.of(train));
        assertThrows(InvalidAgeException.class, () -> reservationService.bookTicket(16589, passengerDTO, paymentDTO));
    }

    @Test
    void testBookTicketInvalidTrain() {
        when(trainRepository.findById(99999)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> reservationService.bookTicket(99999, passengerDTO, paymentDTO));
    }

    @Test
    void testCancelTicketSuccess() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(1);
        ticket.setStatus("Booked");
        Train train = new Train(16589, "Rani Chennamma Express", "Express", "KSR Bengaluru", "Miraj",
                "07:45", "07:50", "Daily", 50, 750.0);
        ticket.setTrain(train);
        try {
            ticket.setPassenger(new Passenger("John Doe", 25, "1234567890", "123456789012"));
        } catch (InvalidAgeException | InvalidContactException | InvalidAadhaarException e) {
            throw new RuntimeException("Unexpected exception for valid passenger data", e);
        }
        ticket.setPayment(new Payment());
        when(ticketRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));
        when(trainRepository.save(any(Train.class))).thenAnswer(i -> i.getArgument(0));

        System.out.println("Initial available seats: " + train.getAvailableSeats());
        reservationService.cancelTicket(1);
        System.out.println("Final available seats: " + train.getAvailableSeats());

        assertEquals("Canceled", ticket.getStatus());
        assertEquals(51, train.getAvailableSeats(), "Available seats should be 51 after cancellation");
        verify(trainRepository, times(1)).save(train);
        verify(eventPublisher, times(1)).publishEvent(any(TicketEvent.class));
    }
}