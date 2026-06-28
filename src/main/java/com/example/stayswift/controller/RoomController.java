package com.example.stayswift.controller;

import com.example.stayswift.entity.Room;
import com.example.stayswift.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // GET /rooms  — public, no token needed
    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }
}