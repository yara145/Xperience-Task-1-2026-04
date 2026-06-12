package com.xperience.hero.repository;

import com.xperience.hero.model.RSVP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RSVPRepository extends JpaRepository<RSVP, Long> {

    Optional<RSVP> findByInvitationId(Long invitationId);

    List<RSVP> findByEventId(Long eventId);

    List<RSVP> findByEventIdAndResponseStatus(Long eventId, RSVP.RSVPStatus status);

    @Query("SELECT COUNT(r) FROM RSVP r WHERE r.eventId = :eventId AND r.responseStatus = 'YES_CONFIRMED'")
    long countConfirmedAttendees(Long eventId);

    @Query("SELECT r FROM RSVP r WHERE r.eventId = :eventId AND r.isWaitlisted = true ORDER BY r.waitlistPosition ASC")
    List<RSVP> findWaitlistedByEventOrderByPosition(Long eventId);

}
