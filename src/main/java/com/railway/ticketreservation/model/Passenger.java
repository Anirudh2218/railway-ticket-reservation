package com.railway.ticketreservation.model;

import com.railway.ticketreservation.exception.InvalidAgeException;
import com.railway.ticketreservation.exception.InvalidContactException;
import com.railway.ticketreservation.exception.InvalidAadhaarException;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Passenger {
    @Id
    private String aadhaar;
    private String name;
    private int age;
    private String contact;

    public Passenger(String name, int age, String contact, String aadhaar)
            throws InvalidAgeException, InvalidContactException, InvalidAadhaarException {
        if (age <= 18) {
            throw new InvalidAgeException("Passenger must be above 18 years old.");
        }
        if (!contact.matches("\\d{10}")) {
            throw new InvalidContactException("Contact number must be exactly 10 digits.");
        }
        if (!aadhaar.matches("\\d{12}")) {
            throw new InvalidAadhaarException("Aadhaar number must be exactly 12 digits.");
        }
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.aadhaar = aadhaar;
    }

    public Passenger() {} // Default constructor for JPA
}