package com.xperience.hero.service;

import com.xperience.hero.model.Event;
import com.xperience.hero.model.Invitation;
import com.xperience.hero.model.RSVP;
import com.xperience.hero.repository.EventRepository;
import com.xperience.hero.repository.InvitationRepository;
import com.xperience.hero.repository.RSVPRepository;
import com.xperience.hero.dto.AttendeeDTO;
import com.xperience.hero.dto.EventDTO;
import com.xperience.hero.dto.CreateEventDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final RSVPRepository rsvpRepository;
    private final InvitationRepository invitationRepository;

    public EventService(EventRepository eventRepository, RSVPRepository rsvpRepository, InvitationRepository invitationRepository) {
        this.eventRepository = eventRepository;
        this.rsvpRepository = rsvpRepository;
        this.invitationRepository = invitationRepository;
    }

    public Event createEvent(CreateEventDTO dto, Long hostId) {
        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setEventDateTime(dto.getEventDateTime());
        event.setMaxCapacity(dto.getMaxCapacity());
        event.setHostId(hostId);
        event.setStatus(Event.EventStatus.OPEN);
        return eventRepository.save(event);
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
    }

    public List<Event> getEventsByHost(Long hostId) {
        return eventRepository.findByHostIdOrderByEventDateTimeAsc(hostId);
    }

    public Event closeEvent(Long eventId) {
        Event event = getEventById(eventId);
        if (event.getStatus() != Event.EventStatus.OPEN) {
            throw new RuntimeException("Only OPEN events can be closed");
        }
        event.setStatus(Event.EventStatus.CLOSED);
        return eventRepository.save(event);
    }

    public Event cancelEvent(Long eventId) {
        Event event = getEventById(eventId);
        if (event.getStatus() == Event.EventStatus.CANCELLED) {
            throw new RuntimeException("Event is already cancelled");
        }
        event.setStatus(Event.EventStatus.CANCELLED);
        return eventRepository.save(event);
    }

    public Event lockEventIfStarted(Long eventId) {
        Event event = getEventById(eventId);
        if (LocalDateTime.now().isAfter(event.getEventDateTime())) {
            event.setStatus(Event.EventStatus.STARTED);
            return eventRepository.save(event);
        }
        return event;
    }

    public boolean isEventOpen(Long eventId) {
        Event event = getEventById(eventId);
        return event.getStatus() == Event.EventStatus.OPEN;
    }

    public boolean isEventLocked(Long eventId) {
        Event event = getEventById(eventId);
        return event.getStatus() == Event.EventStatus.STARTED ||
               event.getStatus() == Event.EventStatus.CLOSED ||
               event.getStatus() == Event.EventStatus.CANCELLED ||
               LocalDateTime.now().isAfter(event.getEventDateTime());
    }

    public boolean hasCapacityAvailable(Long eventId) {
        Event event = getEventById(eventId);
        if (event.getMaxCapacity() == null) {
            return true;
        }
        long confirmedCount = rsvpRepository.countConfirmedAttendees(eventId);
        return confirmedCount < event.getMaxCapacity();
    }

    public EventDTO getEventDashboard(Long eventId) {
        Event event = getEventById(eventId);
        List<RSVP> rsvps = rsvpRepository.findByEventId(eventId);

        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setLocation(event.getLocation());
        dto.setEventDateTime(event.getEventDateTime());
        dto.setMaxCapacity(event.getMaxCapacity());
        dto.setStatus(event.getStatus().toString());
        dto.setHostId(event.getHostId());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());

        int confirmCount = 0;
        int maybeCount = 0;
        int declinedCount = 0;
        int waitlistCount = 0;

        for (RSVP rsvp : rsvps) {
            if (rsvp.getResponseStatus() == RSVP.RSVPStatus.YES_CONFIRMED) {
                confirmCount++;
            } else if (rsvp.getResponseStatus() == RSVP.RSVPStatus.YES_WAITLISTED) {
                waitlistCount++;
            } else if (rsvp.getResponseStatus() == RSVP.RSVPStatus.MAYBE) {
                maybeCount++;
            } else if (rsvp.getResponseStatus() == RSVP.RSVPStatus.NO) {
                declinedCount++;
            }
        }

        dto.setConfirmCount(confirmCount);
        dto.setMaybeCount(maybeCount);
        dto.setDeclinedCount(declinedCount);
        dto.setWaitlistCount(waitlistCount);

        List<Invitation> invitations = invitationRepository.findByEventId(eventId);
        Map<Long, String> invitationEmailMap = invitations.stream()
                .collect(Collectors.toMap(Invitation::getId, Invitation::getInviteeEmail));

        List<AttendeeDTO> attendees = rsvps.stream()
                .map(rsvp -> new AttendeeDTO(
                        invitationEmailMap.getOrDefault(rsvp.getInvitationId(), "unknown"),
                        rsvp.getResponseStatus().toString(),
                        Boolean.TRUE.equals(rsvp.getIsWaitlisted()),
                        rsvp.getRespondedAt()
                ))
                .collect(Collectors.toList());

        dto.setAttendees(attendees);

        return dto;
    }
}
