package com.techelevator.dao;

import com.techelevator.model.Song;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for handling database interactions related to songs.
 * This class allows retrieval, submission, voting, and vetoing of songs in an event.
 */
@Component // Marks this class as a Spring-managed component
public class JdbcSongDao implements SongDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructor to initialize JdbcTemplate for database queries.
     */
    public JdbcSongDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Retrieves a list of all songs submitted for a specific event.
     */
    @Override
    public List<Song> getSongsByEvent(int eventId) {
        String sql = "SELECT song_id, event_id, title, artist, submitted_by, vetoed FROM songs WHERE event_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, eventId);
        return mapSongs(rowSet);
    }

    /**
     * Adds a new song to an event.
     */
    @Override
    public Song addSong(Song song) {
        String sql = "INSERT INTO songs (event_id, title, artist, submitted_by) VALUES (?, ?, ?, ?) RETURNING song_id";
        int newId = jdbcTemplate.queryForObject(sql, Integer.class, song.getEventId(), song.getTitle(), song.getArtist(), song.getSubmittedBy());
        song.setSongId(newId); // Assign the generated song ID
        return song;
    }

    /**
     * Allows a user to vote on a song.
     */
    @Override
    public void voteOnSong(int songId, int userId, String voteType) {
        String sql = "INSERT INTO votes (song_id, user_id, vote_type) VALUES (?, ?, ?) " +
                "ON CONFLICT (song_id, user_id) DO UPDATE SET vote_type = EXCLUDED.vote_type";
        jdbcTemplate.update(sql, songId, userId, voteType);
    }

    /**
     * Allows a host to veto a song from the event's playlist.
     */
    @Override
    public void vetoSong(int songId) {
        String sql = "UPDATE songs SET vetoed = TRUE WHERE song_id = ?";
        jdbcTemplate.update(sql, songId);
    }

    /**
     * Maps a rowset of multiple song records to a list of Song objects.
     */
    private List<Song> mapSongs(SqlRowSet rowSet) {
        List<Song> songs = new ArrayList<>();
        while (rowSet.next()) {
            songs.add(mapRowToSong(rowSet));
        }
        return songs;
    }

    /**
     * Maps a single row from the SQL result set to a Song object.
     */
    private Song mapRowToSong(SqlRowSet rowSet) {
        Song song = new Song();
        song.setSongId(rowSet.getInt("song_id"));
        song.setEventId(rowSet.getInt("event_id"));
        song.setTitle(rowSet.getString("title"));
        song.setArtist(rowSet.getString("artist"));
        song.setSubmittedBy(rowSet.getString("submitted_by"));
        song.setVetoed(rowSet.getBoolean("vetoed"));
        return song;
    }
}
