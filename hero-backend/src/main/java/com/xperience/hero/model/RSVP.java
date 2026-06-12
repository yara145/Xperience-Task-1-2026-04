package com.xperience.hero.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rsvps", schema = "hero")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RSVP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long invitationId;

    @Column(nullable = false)
    private Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RSVPStatus responseStatus;

    @Column(name = "is_waitlisted")
    private Boolean isWaitlisted = false;

    @Column(name = "waitlist_position")
    private Integer waitlistPosition;

    private LocalDateTime respondedAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum RSVPStatus {
        YES_CONFIRMED, YES_WAITLISTED, NO, MAYBE
    }
}
