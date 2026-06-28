package com.example.stayswift.controller;

import com.example.stayswift.dto.BookingRequest;
import com.example.stayswift.entity.Booking;
import com.example.stayswift.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // POST /booking — book a room
    @PostMapping
    public ResponseEntity<?> bookRoom(@RequestBody BookingRequest req,
                                      Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            Booking booking = bookingService.createBooking(
                    userEmail,
                    req.getRoomId(),
                    req.getCheckInDate(),
                    req.getCheckOutDate()
            );
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /booking/my-bookings — see my bookings
    @GetMapping("/my-bookings")
    public ResponseEntity<?> getMyBookings(Authentication authentication) {
        return ResponseEntity.ok(
                bookingService.getBookingsByEmail(authentication.getName()));
    }

    // DELETE /booking/{id} — delete my booking completely
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMyBooking(@PathVariable Long id,
                                             Authentication authentication) {
        try {
            bookingService.deleteBookingByUser(id, authentication.getName());
            return ResponseEntity.ok("Booking deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}












