package com.railway.ticketreservation.controller;

import com.railway.ticketreservation.dto.BookingRequestDTO;
import com.railway.ticketreservation.dto.PassengerDTO;
import com.railway.ticketreservation.dto.PaymentDTO;
import com.railway.ticketreservation.model.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReservationControllerTest {
    @Autowired private TestRestTemplate restTemplate;

    @Test
    void testGetAllTrains() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/reservation/trains", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Rani Chennamma Express"));
    }

    @Test
    void testBookTicketSuccess() {
        BookingRequestDTO request = new BookingRequestDTO();
        PassengerDTO passenger = new PassengerDTO();
        passenger.setName("John Doe");
        passenger.setAge(25);
        passenger.setContact("1234567890");
        passenger.setAadhaar("123456789012");
        PaymentDTO payment = new PaymentDTO();
        payment.setPaymentMethod("UPI");
        payment.setPaymentDetails("john@upi");
        request.setPassengerDTO(passenger);
        request.setPaymentDTO(payment);

        ResponseEntity<Ticket> response = restTemplate.postForEntity(
                "/api/reservation/book?trainNumber=16589", request, Ticket.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Booked", response.getBody().getStatus());
    }

    @Test
    void testBookTicketInvalidAge() {
        BookingRequestDTO request = new BookingRequestDTO();
        PassengerDTO passenger = new PassengerDTO();
        passenger.setName("John Doe");
        passenger.setAge(15);
        passenger.setContact("1234567890");
        passenger.setAadhaar("123456789012");
        PaymentDTO payment = new PaymentDTO();
        payment.setPaymentMethod("UPI");
        payment.setPaymentDetails("john@upi");
        request.setPassengerDTO(passenger);
        request.setPaymentDTO(payment);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/reservation/book?trainNumber=16589", request, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Passenger must be above 18 years old."));
    }
}