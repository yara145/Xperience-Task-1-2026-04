package com.xperience.hero.service;

import com.xperience.hero.model.Event;
import com.xperience.hero.model.Invitation;
import com.xperience.hero.model.RSVP;
import com.xperience.hero.repository.EventRepository;
import com.xperience.hero.repository.InvitationRepository;
import com.xperience.hero.repository.RSVPRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RSVPService {

    private final RSVPRepository rsvpRepository;
    private final InvitationRepository invitationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    public RSVPService(RSVPRepository rsvpRepository, InvitationRepository invitationRepository,
                     EventRepository eventRepository, EventService eventService) {
        this.rsvpRepository = rsvpRepository;
        this.invitationRepository = invitationRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
    }

    public RSVP submitRSVP(String inviteToken, String responseStatus) {
        Invitation invitation = invitationRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));
        
        Event event = eventRepository.findById(invitation.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (eventService.isEventLocked(invitation.getEventId())) {
            throw new RuntimeException("Event is locked. RSVP changes are not allowed.");
        }

        Optional<RSVP> existing = rsvpRepository.findByInvitationId(invitation.getId());
        RSVP.RSVPStatus previousStatus = existing.map(RSVP::getResponseStatus).orElse(null);
        RSVP rsvp;

        if (existing.isPresent()) {
            rsvp = existing.get();
            rsvp.setResponseStatus(RSVP.RSVPStatus.valueOf(responseStatus));
        } else {
            rsvp = new RSVP();
            rsvp.setInvitationId(invitation.getId());
            rsvp.setEventId(invitation.getEventId());
            rsvp.setResponseStatus(RSVP.RSVPStatus.valueOf(responseStatus));
        }

        if (responseStatus.equals("YES_CONFIRMED")) {
            boolean hasCapacity = eventService.hasCapacityAvailable(invitation.getEventId());
            
            if (hasCapacity) {
                rsvp.setResponseStatus(RSVP.RSVPStatus.YES_CONFIRMED);
                rsvp.setIsWaitlisted(false);
                rsvp.setWaitlistPosition(null);
            } else {
                rsvp.setResponseStatus(RSVP.RSVPStatus.YES_WAITLISTED);
                rsvp.setIsWaitlisted(true);
                int position = getNextWaitlistPosition(invitation.getEventId());
                rsvp.setWaitlistPosition(position);
            }
        } else {
            rsvp.setIsWaitlisted(false);
            rsvp.setWaitlistPosition(null);
        }

        rsvp.setRespondedAt(LocalDateTime.now());
        RSVP saved = rsvpRepository.save(rsvp);

        if (previousStatus == RSVP.RSVPStatus.YES_CONFIRMED && responseStatus.equals("NO")) {
            promoteFromWaitlist(invitation.getEventId());
        }

        return saved;
    }

    private int getNextWaitlistPosition(Long eventId) {
        List<RSVP> waitlisted = rsvpRepository.findWaitlistedByEventOrderByPosition(eventId);
        if (waitlisted.isEmpty()) {
            return 1;
        }
        return waitlisted.get(waitlisted.size() - 1).getWaitlistPosition() + 1;
    }

    private void promoteFromWaitlist(Long eventId) {
        if (!eventService.hasCapacityAvailable(eventId)) {
            return;
        }

        List<RSVP> waitlisted = rsvpRepository.findWaitlistedByEventOrderByPosition(eventId);
        if (!waitlisted.isEmpty()) {
            RSVP toPromote = waitlisted.get(0);
            toPromote.setResponseStatus(RSVP.RSVPStatus.YES_CONFIRMED);
            toPromote.setIsWaitlisted(false);
            toPromote.setWaitlistPosition(null);
            rsvpRepository.save(toPromote);
        }
    }

    public RSVP getRSVPByToken(String inviteToken) {
        Invitation invitation = invitationRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));
        
        return rsvpRepository.findByInvitationId(invitation.getId())
                .orElse(null);
    }

    public List<RSVP> getRSVPsByEvent(Long eventId) {
        return rsvpRepository.findByEventId(eventId);
    }
}
