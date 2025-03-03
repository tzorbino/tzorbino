BEGIN TRANSACTION;

DROP TABLE IF EXISTS playlist_songs;
DROP TABLE IF EXISTS votes;
DROP TABLE IF EXISTS playlists;
DROP TABLE IF EXISTS event_hosts;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS songs;
DROP TABLE IF EXISTS users;

-- Users Table
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(200) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('ROLE_USER', 'ROLE_DJ', 'ROLE_HOST', 'ROLE_ADMIN'))
);

-- Events Table
CREATE TABLE events (
    event_id SERIAL PRIMARY KEY,
    event_name VARCHAR(100) NOT NULL,
    event_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    created_by INT,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Event Hosts Table
CREATE TABLE event_hosts (
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (event_id, user_id),
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Songs Table
CREATE TABLE songs (
    song_id SERIAL,
    song_name VARCHAR(200) NOT NULL,
    song_genre VARCHAR(100) NOT NULL,
    PRIMARY KEY (song_id)
);

-- Playlists Table
CREATE TABLE playlists (
    playlist_id SERIAL NOT NULL,
    event_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (playlist_id),
    FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE
);

-- Playlist Songs Table
CREATE TABLE playlist_songs (
    playlist_song_id SERIAL,
    playlist_id INT NOT NULL,
    song_id INT NOT NULL,
    added_by INT NOT NULL,
    PRIMARY KEY (playlist_song_id),
    FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id) ON DELETE CASCADE,
    FOREIGN KEY (song_id) REFERENCES songs(song_id) ON DELETE CASCADE,
    FOREIGN KEY (added_by) REFERENCES users(user_id) ON DELETE SET NULL
);

INSERT INTO events (event_name, event_date, start_time, end_time) VALUES
('Wedding', '12-6-25', '12:30', '6:00'),
('Birthday Party', '12-8-25', '10:00', '4:00');

COMMIT TRANSACTION;
