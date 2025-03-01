package com.zura.gymCRM.service;

import com.zura.gymCRM.dao.TraineeRepository;
import com.zura.gymCRM.dao.TrainerRepository;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.exceptions.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeService {
  private TraineeRepository traineeRepository;
  private TrainerRepository trainerRepository;

  @Autowired
  public void setTraineeRepository(TraineeRepository traineeRepository) {
    this.traineeRepository = traineeRepository;
  }

  @Autowired
  public void setTrainerRepository(TrainerRepository trainerRepository) {
    this.trainerRepository = trainerRepository;
  }

  private static final Logger logger =
      LoggerFactory.getLogger(TraineeService.class);

  @Transactional
  public Trainee createTrainee(@Valid Trainee trainee) {
    logger.info("Attempting to create trainee: {}",
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName());

    List<Trainee> userlist = traineeRepository.findAll();
    trainee.getUser().setUsername(UsernameGenerator.generateUsername(
        trainee.getUser().getFirstName(), trainee.getUser().getLastName(),
        userlist));
    trainee.getUser().setPassword(generateRandomPassword());

    traineeRepository.save(trainee);
    logger.info("Trainee created successfully: {}",
                trainee.getUser().getUsername());

    return trainee;
  }

  @Transactional
  public void deleteTrainee(Long traineeId) {
    traineeRepository.deleteById(traineeId);
  }

  public Trainee selectTraineeByUsername(String username)
      throws NotFoundException {
    return traineeRepository.findByUser_Username(username).orElseThrow(
        () -> new NotFoundException(username));
  }

  @Transactional
  public void changePassword(String username, String newPassword)
      throws EntityNotFoundException {

    Optional<Trainee> traineeOpt =
        traineeRepository.findByUser_Username(username);
    if (traineeOpt.isPresent()) {
      Trainee trainee = traineeOpt.get();
      trainee.getUser().setPassword(newPassword);
      traineeRepository.save(trainee);
    } else {
      throw new EntityNotFoundException("Trainee not found");
    }
  }

  @Transactional
  public Trainee activateTrainee(String username) throws NotFoundException {
    Trainee trainee =
        traineeRepository.findByUser_Username(username).orElseThrow(
            () -> new NotFoundException("Not found" + username));

    if (trainee.getUser().getIsActive()) {
      logger.info(
          "Trainee with this username is already active, reactivating.");
    } else {
      trainee.getUser().setIsActive(true);
    }

    return traineeRepository.save(trainee);
  }

  @Transactional
  public Trainee deactivateTrainee(String username) throws NotFoundException {
    Trainee trainee =
        traineeRepository.findByUser_Username(username).orElseThrow(
            () -> new NotFoundException("Not found" + username));

    if (!trainee.getUser().getIsActive()) {
      logger.info(
          "Trainee with this username is already inactive, reactivating.{}",
          username);

    } else {
      trainee.getUser().setIsActive(false);
    }

    return traineeRepository.save(trainee);
  }

  @Transactional
  public void deleteTraineeByUsername(String username)
      throws NotFoundException {
    logger.info("Attempting to delete Trainee with Username: {}", username);

    Trainee trainee =
        traineeRepository.findByUser_Username(username).orElseThrow(
            () -> new NotFoundException("Not found" + username));

    logger.info("Trainee with Username: {} found. Proceeding with deletion.",
                username);

    traineeRepository.deleteByUser_Username(username);

    logger.info("Trainee with Username: {} successfully deleted.", username);
  }

  @Transactional
  public List<Training>
  getTraineeTrainingsByCriteria(String username, Date fromDate, Date toDate,
                                String trainerName, String trainingType)
      throws NotFoundException {

    Optional<Trainee> traineeOptional =
        traineeRepository.findByUser_Username(username);
    if (traineeOptional.isEmpty()) {
      throw new NotFoundException("Not found" + username);
    }

    List<Training> trainings = traineeRepository.findTrainingsByCriteria(
        username, fromDate, toDate, trainerName, trainingType);

    return trainings;
  }

  @Transactional
  public List<Trainer> getUnassignedTrainersForTrainee(String username)
      throws NotFoundException {

    Optional<Trainee> traineeOptional =
        traineeRepository.findByUser_Username(username);
    if (traineeOptional.isEmpty()) {
      throw new NotFoundException(username);
    }
    Trainee trainee = traineeOptional.get();
    return trainerRepository.findTrainersNotAssignedToTrainee(trainee.getId());
  }

  @Transactional
  public Trainee updateTrainee(@Valid Trainee updatedTrainee)
      throws NotFoundException {
    Long id = updatedTrainee.getId();
    logger.info("Attempting to update Trainer with ID: {}", id);

    Trainee trainee = traineeRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Not found" + id));

    logger.info("Updating Trainer with ID: {}", id);
    trainee.getUser().setFirstName(updatedTrainee.getUser().getFirstName());
    trainee.getUser().setLastName(updatedTrainee.getUser().getLastName());
    trainee.getUser().setUsername(updatedTrainee.getUser().getUsername());
    trainee.getUser().setPassword(updatedTrainee.getUser().getPassword());
    trainee.getUser().setIsActive(updatedTrainee.getUser().getIsActive());
    trainee.setDateOfBirth(updatedTrainee.getDateOfBirth());
    trainee.setAddress(updatedTrainee.getAddress());

    Trainee updated = traineeRepository.save(trainee);
    logger.info("Trainer with ID: {} successfully updated", id);

    return updated;
  }

  @Transactional
  public Trainee assignOrRemoveTrainer(String traineeUsername,
                                       String trainerUsername, boolean assign) {
    Trainee trainee =
        traineeRepository.findByUser_Username(traineeUsername)
            .orElseThrow(()
                             -> new NotFoundException("Trainee not found: " +
                                                      traineeUsername));

    Trainer trainer =
        trainerRepository.findByUser_Username(trainerUsername)
            .orElseThrow(()
                             -> new NotFoundException("Trainer not found: " +
                                                      trainerUsername));

    if (assign) {
      if (trainee.getTrainers().contains(trainer)) {
        throw new IllegalArgumentException(
            "Trainer is already assigned to this trainee.");
      }
      trainee.getTrainers().add(trainer);
      logger.info("Assigned trainer {} to trainee {}", trainerUsername,
                  traineeUsername);
    } else {
      if (!trainee.getTrainers().contains(trainer)) {
        throw new IllegalArgumentException(
            "Trainer is not assigned to this trainee.");
      }
      trainee.getTrainers().remove(trainer);
      logger.info("Removed trainer {} from trainee {}", trainerUsername,
                  traineeUsername);
    }

    return traineeRepository.save(trainee);
  }

  private String generateRandomPassword() {
    return UUID.randomUUID().toString().substring(0, 10);
  }
}
