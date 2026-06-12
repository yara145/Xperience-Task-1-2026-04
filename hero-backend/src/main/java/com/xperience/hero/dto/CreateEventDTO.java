package com.xperience.hero.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventDTO {
    private String title;
    private String description;
    private String location;
    private LocalDateTime eventDateTime;
    private Integer maxCapacity;
}
