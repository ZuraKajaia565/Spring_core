package com.zura.gymCRM.service;

import com.zura.gymCRM.dao.TraineeRepository;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.User;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.security.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    private TraineeService traineeService;
    private TraineeRepository traineeRepository;
    private PasswordUtil passwordUtil;

    @BeforeEach
    void setUp() {
        traineeRepository = mock(TraineeRepository.class);
        passwordUtil = mock(PasswordUtil.class);
        traineeService = new TraineeService();
        traineeService.setTraineeRepository(traineeRepository);
    }



    @Test
    void testActivateTrainee() {
        String username = "johndoe";
        Trainee trainee = new Trainee();
        User user = new User();
        user.setIsActive(false);
        trainee.setUser(user);

        when(traineeRepository.findByUser_Username(username)).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(any())).thenReturn(trainee);

        Trainee result = traineeService.activateTrainee(username);

        assertTrue(result.getUser().getIsActive());
        verify(traineeRepository, times(1)).save(trainee);
    }

    @Test
    void testActivateTraineeNotFound() {
        String username = "nonexistent";
        when(traineeRepository.findByUser_Username(username)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> traineeService.activateTrainee(username));
    }
}