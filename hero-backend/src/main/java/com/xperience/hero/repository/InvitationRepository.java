package com.xperience.hero.repository;

import com.xperience.hero.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    List<Invitation> findByEventId(Long eventId);

    Optional<Invitation> findByInviteToken(String inviteToken);

    Optional<Invitation> findByEventIdAndInviteeEmail(Long eventId, String inviteeEmail);

}
