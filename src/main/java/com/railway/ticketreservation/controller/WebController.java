package com.railway.ticketreservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/book")
    public String bookTicket() {
        return "book";
    }

    @GetMapping("/cancel")
    public String cancelTicket() {
        return "cancel";
    }

    @GetMapping("/tickets")
    public String viewTickets() {
        return "tickets";
    }
}