package com.xperience.hero.controller;

import com.xperience.hero.dto.RSVPResponseDTO;
import com.xperience.hero.model.Invitation;
import com.xperience.hero.model.RSVP;
import com.xperience.hero.service.InvitationService;
import com.xperience.hero.service.RSVPService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rsvp")
@CrossOrigin(origins = {"http://localhost:5171", "http://localhost:5173"})
public class RSVPController {

    private final RSVPService rsvpService;
    private final InvitationService invitationService;

    public RSVPController(RSVPService rsvpService, InvitationService invitationService) {
        this.rsvpService = rsvpService;
        this.invitationService = invitationService;
    }

    @PostMapping("/submit")
    public ResponseEntity<RSVP> submitRSVP(@RequestBody RSVPResponseDTO dto) {
        RSVP rsvp = rsvpService.submitRSVP(dto.getInviteToken(), dto.getResponseStatus());
        return ResponseEntity.ok(rsvp);
    }

    @GetMapping("/{inviteToken}")
    public ResponseEntity<RSVP> getRSVP(@PathVariable String inviteToken) {
        RSVP rsvp = rsvpService.getRSVPByToken(inviteToken);
        if (rsvp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rsvp);
    }

    @GetMapping("/invitation/{inviteToken}")
    public ResponseEntity<Map<String, Object>> getInvitationDetails(@PathVariable String inviteToken) {
        try {
            Invitation invitation = invitationService.getInvitationByToken(inviteToken);
            RSVP rsvp = rsvpService.getRSVPByToken(inviteToken);

            Map<String, Object> result = new HashMap<>();
            result.put("invitationId", invitation.getId());
            result.put("eventId", invitation.getEventId());
            result.put("inviteeEmail", invitation.getInviteeEmail());
            result.put("rsvpStatus", rsvp != null ? rsvp.getResponseStatus().toString() : null);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
