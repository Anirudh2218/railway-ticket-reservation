package com.railway.ticketreservation.repository;

import com.railway.ticketreservation.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}