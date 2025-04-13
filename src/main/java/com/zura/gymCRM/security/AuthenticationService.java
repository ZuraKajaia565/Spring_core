package com.zura.gymCRM.security;

import com.zura.gymCRM.dao.TraineeRepository;
import com.zura.gymCRM.dao.TrainerRepository;
import com.zura.gymCRM.dto.AuthenticationRequest;
import com.zura.gymCRM.dto.AuthenticationResponse;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordUtil passwordUtil;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsService userDetailsService,
            PasswordUtil passwordUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.passwordUtil = passwordUtil;
    }

// Improve the authenticate method in AuthenticationService

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        logger.info("Attempting to authenticate: {}", request.getUsername());

        try {
            Optional<Trainee> traineeOpt = traineeRepository.findByUser_Username(request.getUsername());
            Optional<Trainer> trainerOpt = trainerRepository.findByUser_Username(request.getUsername());

            if (traineeOpt.isPresent()) {
                Trainee trainee = traineeOpt.get();

                // Check if account is active
                if (!trainee.getUser().getIsActive()) {
                    logger.warn("Authentication failed for trainee: {} - Account is deactivated", request.getUsername());
                    throw new BadCredentialsException("Account is deactivated");
                }

                // Use passwordUtil to check if the raw password matches the stored hash
                if (passwordUtil.matches(request.getPassword(), trainee.getUser().getPassword())) {
                    logger.info("Password match successful for trainee: {}", request.getUsername());

                    // Create user details manually
                    UserDetails userDetails = createUserDetails(trainee.getUser(), "TRAINEE");

                    // Generate JWT token
                    String jwtToken = jwtService.generateToken(userDetails);

                    return new AuthenticationResponse(jwtToken, "Authentication successful");
                }
            } else if (trainerOpt.isPresent()) {
                Trainer trainer = trainerOpt.get();

                // Check if account is active
                if (!trainer.getUser().getIsActive()) {
                    logger.warn("Authentication failed for trainer: {} - Account is deactivated", request.getUsername());
                    throw new BadCredentialsException("Account is deactivated");
                }

                // Use passwordUtil to check if the raw password matches the stored hash
                if (passwordUtil.matches(request.getPassword(), trainer.getUser().getPassword())) {
                    logger.info("Password match successful for trainer: {}", request.getUsername());

                    // Create user details manually
                    UserDetails userDetails = createUserDetails(trainer.getUser(), "TRAINER");

                    // Generate JWT token
                    String jwtToken = jwtService.generateToken(userDetails);

                    return new AuthenticationResponse(jwtToken, "Authentication successful");
                }
            }

            // If we reach here, either user doesn't exist or password doesn't match
            logger.warn("Authentication failed for user: {} - Invalid credentials", request.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for user: {} - {}", request.getUsername(), e.getMessage());
            throw new BadCredentialsException(e.getMessage());
        } catch (Exception e) {
            logger.error("Authentication error for user: {}", request.getUsername(), e);
            throw new RuntimeException("Authentication error: " + e.getMessage(), e);
        }
    }

    private UserDetails createUserDetails(com.zura.gymCRM.entities.User user, String role) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getIsActive(),
                true,
                true,
                true,
                Collections.singletonList(authority)
        );
    }
}