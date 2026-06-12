package com.xperience.hero.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendeeDTO {
    private String email;
    private String status;
    private boolean waitlisted;
    private LocalDateTime respondedAt;
}
