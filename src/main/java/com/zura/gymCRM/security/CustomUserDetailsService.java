package com.zura.gymCRM.security;

import com.zura.gymCRM.dao.TraineeRepository;
import com.zura.gymCRM.dao.TrainerRepository;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public CustomUserDetailsService(
            TraineeRepository traineeRepository,
            TrainerRepository trainerRepository,
            LoginAttemptService loginAttemptService) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip)) {
            throw new RuntimeException("Blocked due to too many failed login attempts");
        }

        Optional<Trainee> trainee = traineeRepository.findByUser_Username(username);
        if (trainee.isPresent() && trainee.get().getUser().getIsActive()) {
            User user = trainee.get().getUser();
            return createUserDetails(user, "TRAINEE");
        }

        Optional<Trainer> trainer = trainerRepository.findByUser_Username(username);

        logger.info("Loading user: {}, found: {}", username, trainee.isPresent() || trainer.isPresent());
        if (trainer.isPresent() && trainer.get().getUser().getIsActive()) {
            User user = trainer.get().getUser();
            return createUserDetails(user, "TRAINER");
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }

    private UserDetails createUserDetails(User user, String role) {
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role)
        );

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getIsActive(),
                true,
                true,
                true,
                authorities
        );
    }

    private String getClientIP() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}