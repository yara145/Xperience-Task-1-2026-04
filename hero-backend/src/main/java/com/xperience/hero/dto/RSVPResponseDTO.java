package com.xperience.hero.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RSVPResponseDTO {
    private String responseStatus; // YES, NO, MAYBE
    private String inviteToken;
}
