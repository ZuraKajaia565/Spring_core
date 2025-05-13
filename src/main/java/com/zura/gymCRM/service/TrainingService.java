package com.zura.gymCRM.service;

import com.zura.gymCRM..TrainingRepository;
import com.example.micro.entities.Training;
import com.example.micro.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TrainingService {
  private static final Logger logger = LoggerFactory.getLogger(TrainingService.class);

  private final TrainingRepository trainingRepository;
  private final WorkloadNotificationService workloadNotificationService;

  @Autowired
  public TrainingService(
          TrainingRepository trainingRepository,
          WorkloadNotificationService workloadNotificationService) {
    this.trainingRepository = trainingRepository;
    this.workloadNotificationService = workloadNotificationService;
  }

  /**
   * Creates a new training and notifies the workload service
   */
  @Transactional
  public Training createTraining(Training training) {
    logger.info("Creating training: {}", training.getTrainingName());

    // Save the training to the repository
    Training savedTraining = trainingRepository.save(training);

    // Notify workload service
    workloadNotificationService.notifyTrainingCreated(savedTraining);

    logger.info("Training created successfully: ID={}", savedTraining.getId());
    return savedTraining;
  }

  /**
   * Updates an existing training and notifies the workload service
   */
  @Transactional
  public Training updateTraining(Training training) {
    logger.info("Updating training: ID={}", training.getId());

    // Check if the training exists
    if (!trainingRepository.existsById(training.getId())) {
      throw new NotFoundException("Training not found: " + training.getId());
    }

    // Save the updated training
    Training updatedTraining = trainingRepository.save(training);

    // Notify workload service
    workloadNotificationService.notifyTrainingUpdated(updatedTraining);

    logger.info("Training updated successfully: ID={}", updatedTraining.getId());
    return updatedTraining;
  }

  /**
   * Deletes a training and notifies the workload service
   */
  @Transactional
  public void deleteTraining(Long id) {
    logger.info("Deleting training: ID={}", id);

    // Find the training
    Training training = trainingRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Training not found: " + id));

    // Notify workload service before deleting
    workloadNotificationService.notifyTrainingDeleted(training);

    // Delete the training
    trainingRepository.delete(training);

    logger.info("Training deleted successfully: ID={}", id);
  }

  /**
   * Gets a training by its ID
   */
  public Optional<Training> getTraining(Long trainingId) {
    logger.info("Fetching training: ID={}", trainingId);
    return trainingRepository.findById(trainingId);
  }
}