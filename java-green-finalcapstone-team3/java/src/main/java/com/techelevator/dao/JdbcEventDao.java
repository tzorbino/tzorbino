package com.techelevator.dao;

import com.techelevator.model.Event;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for handling database interactions related to events.
 */
@Component
public class JdbcEventDao implements EventDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcEventDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> findAllEvents() {
        String sql = "SELECT event_id, event_name, event_date, start_time, end_time, created_by FROM events";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        return mapEvents(rowSet);
    }

    @Override
    public Event createEvent(Event event) {
        String sql = "INSERT INTO events (event_name, event_date, start_time, end_time, created_by) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING event_id";
        try {
            // Format the LocalTime values to "HH:mm:ss"
            String formattedStartTime = event.getStartTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            String formattedEndTime = event.getEndTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

            System.out.println("DEBUG: Inserting event -> Name: " + event.getName() +
                    ", Date: " + event.getEventDate() +
                    ", Start Time: " + formattedStartTime +
                    ", End Time: " + formattedEndTime +
                    ", Created By: " + event.getCreatedBy());

            int eventId = jdbcTemplate.queryForObject(sql, Integer.class,
                    event.getName(),
                    java.sql.Date.valueOf(event.getEventDate()),
                    java.sql.Time.valueOf(formattedStartTime),
                    java.sql.Time.valueOf(formattedEndTime),
                    event.getCreatedBy());

            event.setEventID(eventId);
            System.out.println("DEBUG: Event inserted successfully! ID: " + eventId);
            return event;
        } catch (Exception e) {
            e.printStackTrace();  // Print full stack trace for detailed diagnosis
            System.out.println("ERROR: SQL Insert Failed. " + e.getMessage());
            throw new RuntimeException("Database error: " + e.getMessage());
        }
    }

    @Override
    public Event findEventById(int eventId) {
        String sql = "SELECT event_id, event_name, event_date, start_time, end_time, created_by FROM events WHERE event_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, eventId);

        if (rowSet.next()) {
            return mapRowToEvent(rowSet);
        }
        return null;
    }

    @Override
    public List<Event> findFilteredEvents(String name, LocalDate date) {
        StringBuilder sql = new StringBuilder(
                "SELECT e.event_id, e.event_name, e.event_date, e.start_time, e.end_time, e.created_by, " +
                        "COALESCE(array_agg(eh.user_id) FILTER (WHERE eh.user_id IS NOT NULL), '{}') AS hosts " +
                        "FROM events e " +
                        "LEFT JOIN event_hosts eh ON e.event_id = eh.event_id " +
                        "WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (name != null && !name.isBlank()) {
            sql.append(" AND LOWER(e.event_name) LIKE LOWER(?)");
            params.add("%" + name + "%");
        }
        if (date != null) {
            sql.append(" AND e.event_date = ?");
            params.add(date);
        }

        sql.append(" GROUP BY e.event_id, e.event_name, e.event_date, e.start_time, e.end_time, e.created_by");

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql.toString(), params.toArray());
        return mapEvents(rowSet);
    }

    @Override
    public void assignHost(int eventId, int hostId) {
        String sql = "INSERT INTO event_hosts (event_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, eventId, hostId);
    }

    private List<Event> mapEvents(SqlRowSet rowSet) {
        List<Event> events = new ArrayList<>();
        while (rowSet.next()) {
            events.add(mapRowToEvent(rowSet));
        }
        return events;
    }

    private Event mapRowToEvent(SqlRowSet rowSet) {
        Event event = new Event();
        event.setEventID(rowSet.getInt("event_id"));
        event.setName(rowSet.getString("event_name"));
        event.setEventDate(rowSet.getDate("event_date").toLocalDate());
        event.setStartTime(rowSet.getTime("start_time").toLocalTime());
        event.setEndTime(rowSet.getTime("end_time").toLocalTime());
        event.setCreatedBy(rowSet.getInt("created_by"));

        // Parse the aggregated host IDs returned as a string (e.g., "{2,3}")
        String hostsStr = rowSet.getString("hosts");
        List<Integer> hosts = new ArrayList<>();
        if (hostsStr != null && hostsStr.length() > 2) { // "{2,3}"
            hostsStr = hostsStr.substring(1, hostsStr.length() - 1); // Remove curly braces
            String[] hostArr = hostsStr.split(",");
            for (String hostId : hostArr) {
                try {
                    hosts.add(Integer.parseInt(hostId.trim()));
                } catch (NumberFormatException ex) {
                    System.err.println("Error parsing host ID: " + hostId);
                }
            }
        }
        event.setHosts(hosts);
        return event;
    }
}