package com.techelevator.dao;

import com.techelevator.model.RegisterUserDto;
import com.techelevator.model.User;

import java.util.List;

/**
 * DAO Interface for managing user-related database operations.
 * Defines methods for retrieving users, creating new users, and filtering users by roles.
 */
public interface UserDao {

    /**
     * Retrieves a list of all users.
     */
    List<User> getUsers();

    /**
     * Retrieves a user by their unique ID.
     */
    User getUserById(int id);

    /**
     * Retrieves a user by their username.
     */
    User getUserByUsername(String username);

    /**
     * Registers a new user in the system.
     */
    User createUser(RegisterUserDto user);

    /**
     * Retrieves all users assigned as Hosts.
     */
    List<User> getHosts();

    /**
     * Retrieves all users assigned as DJs.
     */
    List<User> getDJs();
}


