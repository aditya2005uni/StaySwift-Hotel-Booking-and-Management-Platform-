package com.example.stayswift.dto;

import lombok.Data;

@Data
public class RoomRequest {
    private String roomType;
    private String description;
    private Double pricePerNight;
    private int totalRooms;
}




//package com.example.stayswift.dto;
//
//import lombok.Data;
//
//@Data
//public class RoomRequest {
//    private String roomType;
//    private String description;
//    private Double pricePerNight;
//    private boolean available;
//}