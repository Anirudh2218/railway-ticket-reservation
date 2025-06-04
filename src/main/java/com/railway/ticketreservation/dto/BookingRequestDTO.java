package com.railway.ticketreservation.dto;

import lombok.Data;

@Data
public class BookingRequestDTO {
    private PassengerDTO passengerDTO;
    private PaymentDTO paymentDTO;
}