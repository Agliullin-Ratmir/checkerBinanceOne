package com.example.checkerbinanceone.repository;

import com.example.checkerbinanceone.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
