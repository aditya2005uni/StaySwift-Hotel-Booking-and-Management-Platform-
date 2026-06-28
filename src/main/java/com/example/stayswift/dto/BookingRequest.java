package com.example.stayswift.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequest {

    @NotNull
    private LocalDate checkInDate;

    @NotNull
    private LocalDate checkOutDate;

    // user picks a room by its ID (get the list from GET /rooms)
    @NotNull
    private Long roomId;
}