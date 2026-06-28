package com.example.stayswift.controller;

import com.example.stayswift.dto.RoomRequest;
import com.example.stayswift.entity.Booking;
import com.example.stayswift.entity.Room;
import com.example.stayswift.service.BookingService;
import com.example.stayswift.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookingService bookingService;
    private final RoomService roomService;

    // GET /admin/bookings — see all bookings
    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // PUT /admin/bookings/{id}/cancel — cancel any booking
    @PutMapping("/bookings/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            Booking booking = bookingService.cancelBookingByAdmin(id);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST /admin/rooms — add a new room
    // Body: { "roomType": "Deluxe", "description": "Sea view", "pricePerNight": 4999.0, "totalRooms": 5 }
    @PostMapping("/rooms")
    public ResponseEntity<Room> addRoom(@RequestBody RoomRequest req) {
        Room room = new Room();
        room.setRoomType(req.getRoomType());
        room.setDescription(req.getDescription());
        room.setPricePerNight(req.getPricePerNight());
        room.setTotalRooms(req.getTotalRooms());
        return ResponseEntity.ok(roomService.addRoom(room));
    }

    // GET /admin/rooms — see all rooms
    @GetMapping("/rooms")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    // POST /admin/rooms/{id}/add-quantity — add more rooms to existing room type
    // Body: { "count": 3 }
    @PostMapping("/rooms/{id}/add-quantity")
    public ResponseEntity<?> addQuantity(@PathVariable Long id,
                                         @RequestBody Map<String, Integer> body) {
        try {
            int count = body.get("count");
            Room room = roomService.addQuantity(id, count);
            return ResponseEntity.ok(room);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
















//package com.example.stayswift.controller;
//
//import com.example.stayswift.dto.RoomRequest;
//import com.example.stayswift.entity.Booking;
//import com.example.stayswift.entity.Room;
//import com.example.stayswift.service.BookingService;
//import com.example.stayswift.service.RoomService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/admin")
//@RequiredArgsConstructor
//public class AdminController {
//
//    private final BookingService bookingService;
//    private final RoomService roomService;
//
//    // GET /admin/bookings — see all bookings
//    @GetMapping("/bookings")
//    public List<Booking> getAllBookings() {
//        return bookingService.getAllBookings();
//    }
//
//    // PUT /admin/bookings/{id}/cancel — cancel any booking
//    @PutMapping("/bookings/{id}/cancel")
//    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
//        try {
//            Booking booking = bookingService.cancelBookingByAdmin(id);
//            return ResponseEntity.ok(booking);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    // POST /admin/rooms — add a new room
//    // Body: { "roomType": "Deluxe", "description": "Sea view", "pricePerNight": 4999.0, "available": true }
//    @PostMapping("/rooms")
//    public ResponseEntity<Room> addRoom(@RequestBody RoomRequest req) {
//        Room room = new Room();
//        room.setRoomType(req.getRoomType());
//        room.setDescription(req.getDescription());
//        room.setPricePerNight(req.getPricePerNight());
//        room.setAvailable(req.isAvailable());
//        return ResponseEntity.ok(roomService.addRoom(room));
//    }
//
//    // GET /admin/rooms — see all rooms
//    @GetMapping("/rooms")
//    public List<Room> getAllRooms() {
//        return roomService.getAllRooms();
//    }
//}