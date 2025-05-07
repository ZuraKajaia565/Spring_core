package com.zura.gymCRM.service;

import com.zura.gymCRM.dao.TrainingRepository;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    private TrainingService trainingService;
    private TrainingRepository trainingRepository;

    @BeforeEach
    void setUp() {
        trainingRepository = mock(TrainingRepository.class);
        trainingService = new TrainingService();
        trainingService.setTrainingRepository(trainingRepository);
    }

    @Test
    void testCreateTraining() {
        Training training = new Training();
        when(trainingRepository.save(any())).thenReturn(training);

        Training result = trainingService.createTraining(training);

        assertNotNull(result);
        verify(trainingRepository, times(1)).save(training);
    }

    @Test
    void testDeleteTraining() {
        Long trainingId = 1L;
        Training training = new Training();

        when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));

        trainingService.deleteTraining(trainingId);

        verify(trainingRepository, times(1)).delete(training);
    }

    @Test
    void testDeleteTrainingNotFound() {
        Long trainingId = 1L;
        when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> trainingService.deleteTraining(trainingId));
    }
}