package com.techelevator.model;

/**
 * Represents a Song submitted to an Event.
 * A song has a unique ID, belongs to an event, and can be voted on or vetoed.
 */
public class Song {

    private int songId;
    private int eventId;
    private String title;
    private String artist;
    private String submittedBy;
    private boolean vetoed;

    /**
     * Default constructor (required for Spring & ORM frameworks).
     */
    public Song() {}

    /**
     * Constructor to initialize a Song with all attributes.
     */
    public Song(int songId, int eventId, String title, String artist, String submittedBy, boolean vetoed) {
        this.songId = songId;
        this.eventId = eventId;
        this.title = title;
        this.artist = artist;
        this.submittedBy = submittedBy;
        this.vetoed = vetoed;
    }

    /**
     * Gets the unique identifier of the song.
     */
    public int getSongId() {
        return songId;
    }

    /**
     * Sets the unique identifier of the song.
     */
    public void setSongId(int songId) {
        this.songId = songId;
    }

    /**
     * Gets the event ID to which the song belongs.
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID to which the song belongs.
     */
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the title of the song.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the song.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the artist of the song.
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Sets the artist of the song.
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Gets the username of the person who submitted the song.
     */
    public String getSubmittedBy() {
        return submittedBy;
    }

    /**
     * Sets the username of the person who submitted the song.
     */
    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    /**
     * Checks whether the song has been vetoed.
     */
    public boolean isVetoed() {
        return vetoed;
    }

    /**
     * Sets the veto status of the song.
     */
    public void setVetoed(boolean vetoed) {
        this.vetoed = vetoed;
    }
}
