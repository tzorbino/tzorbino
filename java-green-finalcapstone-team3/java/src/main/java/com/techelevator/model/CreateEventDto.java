package com.techelevator.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class CreateEventDto {
    private String name;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<Integer> hostIds; // List of host user IDs to assign

    // Getters and setters
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
    public List<Integer> getHostIds() {
        return hostIds;
    }
    public void setHostIds(List<Integer> hostIds) {
        this.hostIds = hostIds;
    }
}

