package com.railway.ticketreservation.dto;

import lombok.Data;

@Data
public class PaymentDTO {
    private String paymentMethod;
    private String paymentDetails;
}