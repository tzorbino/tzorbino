package com.techelevator.controller;

import jakarta.validation.Valid;
import com.techelevator.exception.DaoException;
import com.techelevator.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

import com.techelevator.dao.UserDao;
import com.techelevator.security.jwt.JWTFilter;
import com.techelevator.security.jwt.TokenProvider;

@RestController
@CrossOrigin
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserDao userDao;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);

        User user = userDao.getUserByUsername(loginDto.getUsername());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new LoginResponseDto(jwt, user), httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDto newUser) {
        log.info("Registration attempt for username: {}", newUser.getUsername());
        System.out.println(newUser);
        // Ensure passwords match
        if (!newUser.getPassword().equals(newUser.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match.");
        }
        // Validate password strength: at least 8 characters, one uppercase, one lowercase, one number.
        if (!newUser.getPassword().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, and one number.");
        }
        // Validate role: only allow "HOST" or "DJ"
        String role = newUser.getRole().trim().toUpperCase();
        if (!(role.equals("HOST") || !role.equals("DJ") || !role.equals("ROLE_USER") || !role.equals("ROLE_ADMIN") || !role.equals("user"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be either HOST or DJ.");
        }
        newUser.setRole(role); // ensure the role is stored consistently

        try {
            User createdUser = userDao.createUser(newUser);
            // Remove sensitive data before returning response
            createdUser.setPassword(null);
            log.info("User {} registered successfully with role {}", createdUser.getUsername(), createdUser.getAuthorities());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (DataIntegrityViolationException e) {
            log.error("Registration failed for {}: Username already exists.", newUser.getUsername());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists.");
        } catch (Exception e) {
            log.error("Registration failed for {}: {}", newUser.getUsername(), e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User registration failed.");
        }
    }
}
