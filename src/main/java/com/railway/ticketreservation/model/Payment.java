package com.railway.ticketreservation.model;

import com.railway.ticketreservation.exception.InvalidPaymentException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
public class Payment {
    @Id
    private String paymentId = UUID.randomUUID().toString();
    private String paymentMethod;
    private double amount;
    private boolean isProcessed;

    public void processPayment(String paymentDetails) throws InvalidPaymentException {
        if (paymentMethod == null) {
            throw new InvalidPaymentException("Payment method cannot be null.");
        }
        switch (paymentMethod.toLowerCase()) {
            case "upi":
                if (!paymentDetails.matches("[a-zA-Z0-9]+@[a-zA-Z]+")) {
                    throw new InvalidPaymentException("Invalid UPI ID format.");
                }
                break;
            case "card":
                if (!paymentDetails.matches("\\d{16}")) {
                    throw new InvalidPaymentException("Card number must be 16 digits.");
                }
                break;
            case "netbanking":
                if (paymentDetails.trim().isEmpty()) {
                    throw new InvalidPaymentException("Net banking credentials cannot be empty.");
                }
                break;
            default:
                throw new InvalidPaymentException("Unsupported payment method: " + paymentMethod);
        }
        isProcessed = true;
    }
}