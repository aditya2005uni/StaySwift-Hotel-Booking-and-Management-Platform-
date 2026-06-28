package com.example.stayswift.repository;

import com.example.stayswift.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // find all bookings by this user's email
    List<Booking> findByEmail(String email);

    // find a booking by ID only if it belongs to this user (used for cancel)
    Optional<Booking> findByIdAndEmail(Long id, String email);
}