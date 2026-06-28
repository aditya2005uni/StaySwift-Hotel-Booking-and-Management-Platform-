package com.example.stayswift.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomType;
    private String description;
    private Double pricePerNight;

    // how many rooms of this type are available
    // admin sets this when adding a room
    private int totalRooms;
}



//package com.example.stayswift.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Table(name = "rooms")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class Room {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // e.g.  Standard, Deluxe, Suite
//    private String roomType;
//
//    private String description;
//
//    private Double pricePerNight;
//
//    // admin can mark a room as unavailable
//    private boolean available;
//}