package com.techelevator.dao;

import com.techelevator.model.Song;
import java.util.List;

/**
 * DAO Interface for managing song-related database operations.
 * Defines methods for retrieving, adding, voting, and vetoing songs in an event.
 */
public interface SongDao {

    /**
     * Retrieves a list of all songs submitted for a specific event.
     */
    List<Song> getSongsByEvent(int eventId);

    /**
     * Adds a new song to an event's playlist.
     */
    Song addSong(Song song);

    /**
     * Allows a user to vote on a song.
     */
    void voteOnSong(int songId, int userId, String voteType);

    /**
     * Allows a host to veto a song from the event's playlist.
     */
    void vetoSong(int songId);
}
