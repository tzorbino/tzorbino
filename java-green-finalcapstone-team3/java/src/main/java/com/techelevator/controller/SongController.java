package com.techelevator.controller;

import com.techelevator.dao.SongDao;
import com.techelevator.dao.UserDao;
import com.techelevator.model.Song;
import com.techelevator.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

/**
 * Controller for handling song-related requests within an event.
 * Provides endpoints for retrieving, submitting, voting, and vetoing songs.
 */
@RestController
@CrossOrigin // Allows frontend to make requests from a different domain
@RequestMapping("/events/{eventId}/songs")
public class SongController {

    private final SongDao songDao;
    private final UserDao userDao;

    public SongController(SongDao songDao, UserDao userDao) {
        this.songDao = songDao;
        this.userDao = userDao;
    }

    /**
     * Retrieves all songs submitted for a specific event.
     */
    @GetMapping
    public List<Song> getSongsForEvent(@PathVariable int eventId) {
        return songDao.getSongsByEvent(eventId);
    }

    /**
     * Allows a logged-in user to submit a new song to an event.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Return HTTP 201 Created on success
    public Song submitSong(@PathVariable int eventId, @RequestBody Song song) {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        song.setEventId(eventId); // Associate song with the event
        song.setSubmittedBy(username); // Set the logged-in user as the song submitter
        return songDao.addSong(song);
    }

    /**
     * Allows a logged-in user to vote on a song.
     */
    @PostMapping("/{songId}/vote")
    public void voteOnSong(@PathVariable int eventId, @PathVariable int songId, @RequestParam String voteType) {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        int userId = userDao.getUserByUsername(username).getId();
        songDao.voteOnSong(songId, userId, voteType); // Store the vote in the database
    }

    /**
     * Allows a host to veto a song (remove it from the playlist).
     */
    @PostMapping("/{songId}/veto")
    public void vetoSong(@PathVariable int eventId, @PathVariable int songId) {
        if (!SecurityUtils.userHasRole("ROLE_HOST")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only hosts can veto songs.");
        }

        songDao.vetoSong(songId); // Marks the song as vetoed in the database
    }
}
