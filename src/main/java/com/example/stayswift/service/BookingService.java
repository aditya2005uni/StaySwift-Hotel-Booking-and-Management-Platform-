package com.example.stayswift.service;

import com.example.stayswift.entity.Booking;
import com.example.stayswift.entity.BookingStatus;
import com.example.stayswift.entity.Room;
import com.example.stayswift.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomService roomService;

    public Booking createBooking(String userEmail, Long roomId,
                                 LocalDate checkIn, LocalDate checkOut) {

        if (!checkOut.isAfter(checkIn)) {
            throw new RuntimeException("Check-out date must be after check-in date.");
        }

        Room room = roomService.getRoomById(roomId);

        // check if rooms are available
        if (room.getTotalRooms() <= 0) {
            throw new RuntimeException("Sorry, no rooms available for this room type.");
        }

        // simple calculation
        long numberOfNights = checkOut.toEpochDay() - checkIn.toEpochDay();
        double totalAmount = numberOfNights * room.getPricePerNight();

        // decrease room count by 1
        room.setTotalRooms(room.getTotalRooms() - 1);
        roomService.saveRoom(room);

        Booking booking = new Booking();
        booking.setEmail(userEmail);
        booking.setRoom(room);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setNumberOfNights(numberOfNights);
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByEmail(String email) {
        return bookingRepository.findByEmail(email);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // user deletes booking — increase room count back by 1
    public void deleteBookingByUser(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findByIdAndEmail(bookingId, userEmail)
                .orElseThrow(() -> new RuntimeException("Booking not found or you are not authorised."));

        // give the room back
        Room room = booking.getRoom();
        room.setTotalRooms(room.getTotalRooms() + 1);
        roomService.saveRoom(room);

        bookingRepository.delete(booking);
    }

    // admin cancels any booking — increase room count back by 1
    public Booking cancelBookingByAdmin(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled.");
        }

        // give the room back
        Room room = booking.getRoom();
        room.setTotalRooms(room.getTotalRooms() + 1);
        roomService.saveRoom(room);

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingRepository.save(booking);
    }
}



















//package com.example.stayswift.service;
//
//import com.example.stayswift.entity.Booking;
//import com.example.stayswift.entity.BookingStatus;
//import com.example.stayswift.entity.Room;
//import com.example.stayswift.repository.BookingRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class BookingService {
//
//    private final BookingRepository bookingRepository;
//    private final RoomService roomService;
//
//    public Booking createBooking(String userEmail, Long roomId,
//                                 LocalDate checkIn, LocalDate checkOut) {
//
//        if (!checkOut.isAfter(checkIn)) {
//            throw new RuntimeException("Check-out date must be after check-in date.");
//        }
//
//        Room room = roomService.getRoomById(roomId);
//
//        // simple calculation
//        long numberOfNights = checkOut.toEpochDay() - checkIn.toEpochDay();
//        double totalAmount = numberOfNights * room.getPricePerNight();
//
//        Booking booking = new Booking();
//        booking.setEmail(userEmail);
//        booking.setRoom(room);
//        booking.setCheckInDate(checkIn);
//        booking.setCheckOutDate(checkOut);
//        booking.setNumberOfNights(numberOfNights);
//        booking.setTotalAmount(totalAmount);
//        booking.setStatus(BookingStatus.CONFIRMED);
//
//        return bookingRepository.save(booking);
//    }
//
//    public List<Booking> getBookingsByEmail(String email) {
//        return bookingRepository.findByEmail(email);
//    }
//
//    public List<Booking> getAllBookings() {
//        return bookingRepository.findAll();
//    }
//
//    // user completely deletes their booking
//    public void deleteBookingByUser(Long bookingId, String userEmail) {
//        Booking booking = bookingRepository.findByIdAndEmail(bookingId, userEmail)
//                .orElseThrow(() -> new RuntimeException("Booking not found or you are not authorised."));
//        bookingRepository.delete(booking);
//    }
//
//    // admin cancels any booking
//    public Booking cancelBookingByAdmin(Long bookingId) {
//        Booking booking = bookingRepository.findById(bookingId)
//                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + bookingId));
//
//        if (booking.getStatus() == BookingStatus.CANCELLED) {
//            throw new RuntimeException("Booking is already cancelled.");
//        }
//
//        booking.setStatus(BookingStatus.CANCELLED);
//        return bookingRepository.save(booking);
//    }
//}
//
//
//
