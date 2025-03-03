package com.techelevator.controller;

import com.techelevator.dao.EventDao;
import com.techelevator.dao.UserDao;
import com.techelevator.model.CreateEventDto;
import com.techelevator.model.Event;
import com.techelevator.model.User;
import com.techelevator.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for handling event-related requests.
 * Provides endpoints for retrieving and creating events.
 */
@RestController
@CrossOrigin
@RequestMapping("/events")
public class EventController {

    private final Logger log = LoggerFactory.getLogger(EventController.class);
    private final EventDao eventDao;
    private final UserDao userDao;

    public EventController(EventDao eventDao, UserDao userDao) {
        this.eventDao = eventDao;
        this.userDao = userDao;
    }

    /**
     * Retrieves a list of events.
     */
    @GetMapping
    public List<Event> getEvents(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) LocalDate date) {
        return eventDao.findFilteredEvents(name, date);
    }

    /**
     * Retrieves details of a single event by ID.
     */
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable int id) {
        Event event = eventDao.findEventById(id);
        if (event == null) {
            throw new RuntimeException("Event not found.");
        }
        return event;
    }

    /**
     * Allows a DJ to create an event and assign host(s) to it.
     * The event creator is assumed to be the DJ.
     */
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody CreateEventDto createEventDto) {
        log.info("Received event creation request: {}", createEventDto);

        // Verify that the user is logged in.
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (currentUsername == null) {
            log.warn("User not logged in. Cannot create event.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login required to create an event.");
        }
        // Ensure the user has the DJ role.
        if (!SecurityUtils.userHasRole("ROLE_DJ")) {
            log.warn("User {} does not have DJ role.", currentUsername);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only DJs can create events.");
        }
        // Validate that at least one host is assigned.
        if (createEventDto.getHostIds() == null || createEventDto.getHostIds().isEmpty()) {
            log.warn("No host IDs provided for event creation.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("At least one host must be assigned to the event.");
        }
        // Create the event from the provided details.
        Event event = new Event();
        event.setName(createEventDto.getName());
        event.setEventDate(createEventDto.getEventDate());
        event.setStartTime(createEventDto.getStartTime());
        event.setEndTime(createEventDto.getEndTime());

        // Log all event details
        log.info("Event Details: Name='{}', EventDate='{}', StartTime='{}', EndTime='{}'",
                event.getName(), event.getEventDate(), event.getStartTime(), event.getEndTime());

        // Retrieve the current user and set the createdBy field.
        User currentUser = userDao.getUserByUsername(currentUsername);
        if (currentUser == null) {
            log.error("Current user {} not found in database.", currentUsername);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Current user not found.");
        }
        event.setCreatedBy(currentUser.getId());
        log.info("Current user id set as createdBy: {}", currentUser.getId());

        // Insert the event into the database.
        Event createdEvent;
        try {
            createdEvent = eventDao.createEvent(event);
            log.info("Event created successfully with event_id: {}", createdEvent.getEventID());
        } catch (Exception e) {
            log.error("Error creating event: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error: " + e.getMessage());
        }

        // Now assign each host to the event.
        for (Integer hostId : createEventDto.getHostIds()) {
            try {
                eventDao.assignHost(createdEvent.getEventID(), hostId);
                log.info("Assigned host with id {} to event {}", hostId, createdEvent.getEventID());
            } catch (Exception e) {
                log.error("Error assigning host with id {} to event {}: {}", hostId, createdEvent.getEventID(), e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Database error while assigning host: " + e.getMessage());
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }
}
