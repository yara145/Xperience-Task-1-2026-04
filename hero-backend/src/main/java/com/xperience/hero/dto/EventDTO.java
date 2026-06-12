package com.xperience.hero.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime eventDateTime;
    private Integer maxCapacity;
    private String status;
    private Long hostId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int confirmCount;
    private int maybeCount;
    private int declinedCount;
    private int waitlistCount;
    private List<AttendeeDTO> attendees;
}

