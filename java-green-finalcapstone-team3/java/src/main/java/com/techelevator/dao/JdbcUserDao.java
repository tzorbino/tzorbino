package com.techelevator.dao;

import com.techelevator.model.Authority;
import com.techelevator.model.RegisterUserDto;
import com.techelevator.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * DAO for handling user-related database operations.
 */
@Component
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Registers a new user in the database.
     */
    @Override
    public User createUser(RegisterUserDto user) {
        if (user.getUsername() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }

        String username = user.getUsername().trim().toLowerCase();
        String passwordHash = passwordEncoder.encode(user.getPassword());
        String role = user.getRole().toUpperCase().startsWith("ROLE_")
                ? user.getRole().toUpperCase()
                : "ROLE_" + user.getRole().toUpperCase();

        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?) RETURNING user_id";

        try {
            int newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, passwordHash, role);
            return getUserById(newUserId);
        } catch (CannotGetJdbcConnectionException e) {e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection error");
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
    }

    /**
     * Retrieves a user by their unique ID.
     */
    @Override
    public User getUserById(int userId) {
        String sql = "SELECT user_id, username, password_hash, role FROM users WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);

        if (rowSet.next()) {
            return mapRowToUser(rowSet);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }
    }

    /**
     * Retrieves a user by their username.
     */
    @Override
    public User getUserByUsername(String username) {
        if (username == null) throw new IllegalArgumentException("Username cannot be null");

        String sql = "SELECT user_id, username, password_hash, role FROM users WHERE username = LOWER(TRIM(?))";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);

        if (rowSet.next()) {
            return mapRowToUser(rowSet);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found in database.");
        }
    }

    /**
     * Retrieves a list of all users.
     */
    @Override
    public List<User> getUsers() {
        String sql = "SELECT user_id, username, role FROM users";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);

        List<User> users = new ArrayList<>();
        while (rowSet.next()) {
            users.add(mapRowToUser(rowSet));
        }
        return users;
    }

    /**
     * Retrieves all users assigned as DJs.
     */
    @Override
    public List<User> getDJs() {
        String sql = "SELECT user_id, username FROM users WHERE role = 'ROLE_DJ'";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);

        List<User> djs = new ArrayList<>();
        while (rowSet.next()) {
            User user = new User();
            user.setId(rowSet.getInt("user_id"));
            user.setUsername(rowSet.getString("username"));
            user.setAuthorities(Set.of(new Authority("ROLE_DJ")));
            djs.add(user);
        }
        return djs;
    }

    /**
     * Retrieves all users assigned as Hosts.
     */
    @Override
    public List<User> getHosts() {
        String sql = "SELECT user_id, username, password_hash, role FROM users WHERE role = 'ROLE_HOST'";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        List<User> hosts = new ArrayList<>();
        while (rowSet.next()) {
            User user = new User();
            user.setId(rowSet.getInt("user_id"));
            user.setUsername(rowSet.getString("username"));
            // For security, you might not want to set the password in the response.
            user.setPassword(rowSet.getString("password_hash"));
            // Authorities can be set if needed, e.g.:
            // user.setAuthorities(Set.of(new Authority(rowSet.getString("role"))));
            hosts.add(user);
        }
        return hosts;
    }

    /**
     * Maps a row from the SQL result set to a User object.
     */
    private User mapRowToUser(SqlRowSet rowSet) {
        User user = new User();
        user.setId(rowSet.getInt("user_id"));
        user.setUsername(rowSet.getString("username"));
        user.setPassword(rowSet.getString("password_hash"));
        user.setAuthorities(Set.of(new Authority(rowSet.getString("role"))));
        return user;
    }
}
