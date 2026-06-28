package com.example.stayswift.service;

import com.example.stayswift.entity.Room;
import com.example.stayswift.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    // admin adds a new room
    public Room addRoom(Room room) {
        return roomRepository.save(room);
    }

    // everyone can see all rooms
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // used by BookingService to fetch room by id
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
    }

    // used by BookingService to update room count after booking/cancel/delete
    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    // admin adds more quantity to existing room
    public Room addQuantity(Long roomId, int count) {
        Room room = getRoomById(roomId);
        room.setTotalRooms(room.getTotalRooms() + count);
        return roomRepository.save(room);
    }
}














//package com.example.stayswift.service;
//
//import com.example.stayswift.entity.Room;
//import com.example.stayswift.repository.RoomRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class RoomService {
//
//    private final RoomRepository roomRepository;
//
//    // Admin adds a new room
//    public Room addRoom(Room room) {
//        return roomRepository.save(room);
//    }
//
//    // Everyone can see all rooms
//    public List<Room> getAllRooms() {
//        return roomRepository.findAll();
//    }
//
//    // Used internally by BookingService to fetch the room being booked
//    public Room getRoomById(Long id) {
//        return roomRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
//    }
//}