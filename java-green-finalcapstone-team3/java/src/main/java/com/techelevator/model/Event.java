package com.techelevator.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Event {
    private int eventID;
    private String name;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int createdBy;
    private List<Integer> hosts;  // New property

    public Event() {}

    public Event(int eventID, String name, LocalDate eventDate, LocalTime startTime, LocalTime endTime, int createdBy, List<Integer> hosts) {
        this.eventID = eventID;
        this.name = name;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdBy = createdBy;
        this.hosts = hosts;
    }

    // Getters and setters
    public int getEventID() {
        return eventID;
    }
    public void setEventID(int eventID) {
        this.eventID = eventID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public LocalDate getEventDate() {
        return eventDate;
    }
    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }
    public LocalTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    public LocalTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    public int getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    public List<Integer> getHosts() {
        return hosts;
    }
    public void setHosts(List<Integer> hosts) {
        this.hosts = hosts;
    }
}
