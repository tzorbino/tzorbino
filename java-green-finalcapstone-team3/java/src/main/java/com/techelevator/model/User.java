package com.techelevator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a User in the system.
 * A user has a unique ID, username, password, and assigned roles.
 */
public class User {

   private int id;
   private String username;
   @JsonIgnore
   private String password;
   private Set<Authority> authorities;

   public User() {
      this.authorities = new HashSet<>();
   }

   public User(int id, String username, String password, String role) {
      this.id = id;
      this.username = username;
      this.password = password;
      this.authorities = new HashSet<>();

      // Convert role string into a Set of Authority objects
      if (role != null) {
         this.authorities.add(new Authority(role));
      }
   }

   /**
    * Gets the unique identifier of the user.
    */
   public int getId() {
      return id;
   }

   /**
    * Sets the unique identifier of the user.
    */
   public void setId(int id) {
      this.id = id;
   }

   /**
    * Gets the username of the user.
    */
   public String getUsername() {
      return username;
   }

   /**
    * Sets the username of the user.
    */
   public void setUsername(String username) {
      this.username = username;
   }

   /**
    * Gets the hashed password of the user.
    */
   public String getPassword() {
      return password;
   }

   /**
    * Sets the hashed password of the user.
    */
   public void setPassword(String password) {
      this.password = password;
   }

   /**
    * Gets the set of authorities (roles) assigned to the user.
    */
   public Set<Authority> getAuthorities() {
      return authorities;
   }

   /**
    * Sets the authorities (roles) assigned to the user.
    */
   public void setAuthorities(Set<Authority> authorities) {
      this.authorities = authorities;
   }
}
