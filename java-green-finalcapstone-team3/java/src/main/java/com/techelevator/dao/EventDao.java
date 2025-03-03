package com.techelevator.dao;

import com.techelevator.model.Event;
import java.time.LocalDate;
import java.util.List;

public interface EventDao {

    List<Event> findAllEvents();
    Event findEventById(int eventId);
    Event createEvent(Event event);
    List<Event> findFilteredEvents(String name, LocalDate date);

    // New method to assign a host to an event
    void assignHost(int eventId, int hostId);
}
