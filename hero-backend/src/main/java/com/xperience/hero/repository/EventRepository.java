package com.xperience.hero.repository;

import com.xperience.hero.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByHostIdOrderByEventDateTimeAsc(Long hostId);

    List<Event> findByStatus(Event.EventStatus status);

}
