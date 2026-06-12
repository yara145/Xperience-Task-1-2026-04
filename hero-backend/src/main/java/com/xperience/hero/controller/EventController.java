package com.xperience.hero.controller;

import com.xperience.hero.dto.CreateEventDTO;
import com.xperience.hero.dto.EventDTO;
import com.xperience.hero.dto.SendInvitationDTO;
import com.xperience.hero.exception.ForbiddenException;
import com.xperience.hero.exception.UnauthorizedException;
import com.xperience.hero.model.Event;
import com.xperience.hero.model.Host;
import com.xperience.hero.model.Invitation;
import com.xperience.hero.service.AuthService;
import com.xperience.hero.service.EventService;
import com.xperience.hero.service.InvitationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = {"http://localhost:5171", "http://localhost:5173"}, allowedHeaders = "*")
public class EventController {

    private final EventService eventService;
    private final InvitationService invitationService;
    private final AuthService authService;

    public EventController(EventService eventService, InvitationService invitationService, AuthService authService) {
        this.eventService = eventService;
        this.invitationService = invitationService;
        this.authService = authService;
    }

    private Host authenticate(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization required");
        }
        return authService.validateToken(authHeader.substring(7));
    }

    private void requireOwnership(Long eventId, Host host) {
        Event event = eventService.getEventById(eventId);
        if (!event.getHostId().equals(host.getId())) {
            throw new ForbiddenException("You are not the owner of this event");
        }
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CreateEventDTO dto) {
        Host host = authenticate(authHeader);
        Event event = eventService.createEvent(dto, host.getId());
        return ResponseEntity.ok(event);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getEventById(eventId));
    }

    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<Event>> getEventsByHost(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long hostId) {
        Host host = authenticate(authHeader);
        if (!host.getId().equals(hostId)) {
            throw new ForbiddenException("Cannot view another host's events");
        }
        return ResponseEntity.ok(eventService.getEventsByHost(hostId));
    }

    @GetMapping("/{eventId}/dashboard")
    public ResponseEntity<EventDTO> getDashboard(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long eventId) {
        Host host = authenticate(authHeader);
        requireOwnership(eventId, host);
        eventService.lockEventIfStarted(eventId);
        return ResponseEntity.ok(eventService.getEventDashboard(eventId));
    }

    @PostMapping("/{eventId}/close")
    public ResponseEntity<Event> closeEvent(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long eventId) {
        Host host = authenticate(authHeader);
        requireOwnership(eventId, host);
        return ResponseEntity.ok(eventService.closeEvent(eventId));
    }

    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<Event> cancelEvent(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long eventId) {
        Host host = authenticate(authHeader);
        requireOwnership(eventId, host);
        return ResponseEntity.ok(eventService.cancelEvent(eventId));
    }

    @PostMapping("/{eventId}/invite")
    public ResponseEntity<Map<String, String>> sendInvitations(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long eventId,
            @RequestBody SendInvitationDTO dto) {
        Host host = authenticate(authHeader);
        requireOwnership(eventId, host);
        invitationService.sendInvitations(eventId, dto.getInviteeEmails());
        return ResponseEntity.ok(Map.of("message", "Invitations sent successfully"));
    }

    @GetMapping("/{eventId}/invitations")
    public ResponseEntity<List<Invitation>> getInvitations(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Long eventId) {
        Host host = authenticate(authHeader);
        requireOwnership(eventId, host);
        return ResponseEntity.ok(invitationService.getInvitationsByEvent(eventId));
    }
}
