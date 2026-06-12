package com.xperience.hero.service;

import com.xperience.hero.model.Event;
import com.xperience.hero.model.Invitation;
import com.xperience.hero.repository.EventRepository;
import com.xperience.hero.repository.InvitationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final EventRepository eventRepository;
    private final EmailService emailService;

    public InvitationService(InvitationRepository invitationRepository, EventRepository eventRepository, EmailService emailService) {
        this.invitationRepository = invitationRepository;
        this.eventRepository = eventRepository;
        this.emailService = emailService;
    }

    public Invitation createInvitation(Long eventId, String inviteeEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        var existing = invitationRepository.findByEventIdAndInviteeEmail(eventId, inviteeEmail);
        if (existing.isPresent()) {
            return existing.get();
        }

        Invitation invitation = new Invitation();
        invitation.setEventId(eventId);
        invitation.setInviteeEmail(inviteeEmail);
        invitation.setInviteToken(generateUniqueToken());
        
        Invitation saved = invitationRepository.save(invitation);
        
        String inviteLink = "http://localhost:5171/rsvp/" + saved.getInviteToken();
        emailService.sendRSVPInvitation(inviteeEmail, event.getTitle(), inviteLink);
        
        return saved;
    }

    public void sendInvitations(Long eventId, List<String> inviteeEmails) {
        for (String email : inviteeEmails) {
            createInvitation(eventId, email);
        }
    }

    public Invitation getInvitationByToken(String token) {
        return invitationRepository.findByInviteToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid invitation token"));
    }

    public List<Invitation> getInvitationsByEvent(Long eventId) {
        return invitationRepository.findByEventId(eventId);
    }

    private String generateUniqueToken() {
        return UUID.randomUUID().toString();
    }
}
