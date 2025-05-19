package com.zura.gymCRM;

import com.zura.gymCRM.dao.TrainerRepository;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.User;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.security.PasswordUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {
  @Autowired
  private PasswordUtil passwordUtil;
  private TrainerRepository trainerRepository;

  @Autowired
  public void setTrainerRepository(TrainerRepository trainerRepository) {
    this.trainerRepository = trainerRepository;
    ;
  }

  private static final Logger logger =
      LoggerFactory.getLogger(TrainerService.class);

  public Trainer createTrainer(Trainer trainer) {
    String transactionId = MDC.get("transactionId");

    logger.info("Transaction ID: {} Attempting to create trainer: {} {}",
            transactionId,
            trainer.getUser().getFirstName(),
            trainer.getUser().getLastName());

    List<Trainer> userlist = trainerRepository.findAll();
    trainer.getUser().setUsername(UsernameGenerator.generateUsername(
            trainer.getUser().getFirstName(), trainer.getUser().getLastName(),
            userlist));

    // Generate a random password
    String rawPassword = generateRandomPassword();
    logger.debug("Generated raw password: {}", rawPassword);

    // Store the encoded password in the database
    String encodedPassword = passwordUtil.encodePassword(rawPassword);
    logger.debug("Password encoded successfully");
    trainer.getUser().setPassword(encodedPassword);

    Trainer savedTrainer = trainerRepository.save(trainer);
    logger.debug("Trainer saved to database with encoded password");

    // Create a detached copy for the response with raw password
    Trainer responseTrainer = new Trainer();
    User responseUser = new User();

    // Copy relevant properties
    responseUser.setUsername(savedTrainer.getUser().getUsername());
    responseUser.setFirstName(savedTrainer.getUser().getFirstName());
    responseUser.setLastName(savedTrainer.getUser().getLastName());
    responseUser.setPassword(rawPassword); // Use raw password for response
    responseUser.setIsActive(savedTrainer.getUser().getIsActive());

    responseTrainer.setUser(responseUser);
    responseTrainer.setId(savedTrainer.getId());
    responseTrainer.setSpecialization(savedTrainer.getSpecialization());

    logger.info("Transaction ID: {} Trainer created successfully: {}",
            transactionId,
            trainer.getUser().getUsername());

    return responseTrainer;
  }

  public Optional<Trainer> getTrainer(Long userId) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Fetching trainer with ID: {}", transactionId, userId);
    Optional<Trainer> trainer = trainerRepository.findById(userId);
    trainer.ifPresentOrElse(
        t
        -> logger.info("TransactionID: {} Trainer found: {}",transactionId, t.getUser().getUsername()),
        () -> logger.warn("Transaction ID: {} Trainer not found with ID: {}", transactionId, userId));

    return trainer;
  }

  public Optional<Trainer> findTrainerByUsername(String username) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Fetching trainer with username: {}", transactionId, username);

    Optional<Trainer> trainer = trainerRepository.findByUser_Username(username);

    trainer.ifPresentOrElse(
            t -> logger.info("Transaction ID: {} Trainer found: {}", transactionId, t.getUser().getUsername()),
            () -> logger.warn("Transaction ID: {} Trainer not found with username: {}", transactionId, username)
    );

    return trainer;
  }

  @Transactional
  public void changePassword(String username, String newPassword)
          throws EntityNotFoundException {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Attempting to change password for username: {}", transactionId, username);

    Optional<Trainer> trainerOpt = trainerRepository.findByUser_Username(username);

    if (trainerOpt.isPresent()) {
      Trainer trainer = trainerOpt.get();
      logger.info("Transaction ID: {} Trainer found: {}. Changing password.", transactionId, trainer.getUser().getUsername());

      // Encode the new password before storing
      String encodedPassword = passwordUtil.encodePassword(newPassword);
      trainer.getUser().setPassword(encodedPassword);
      trainerRepository.save(trainer);

      logger.info("Transaction ID: {} Password successfully changed for trainer: {}", transactionId, trainer.getUser().getUsername());
    } else {
      logger.warn("Transaction ID: {} Trainer not found with username: {}", transactionId, username);
      throw new EntityNotFoundException("Trainer not found");
    }
  }

  @Transactional
  public Trainer activateTrainer(String username) throws NotFoundException {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Attempting to activate trainer with username: {}", transactionId, username);

    Trainer trainer = trainerRepository.findByUser_Username(username)
            .orElseThrow(() -> {
              logger.warn("Transaction ID: {} Trainer not found with username: {}", transactionId, username);
              return new NotFoundException("Not found" + username);
            });

    if (trainer.getUser().getIsActive()) {
      logger.info("Transaction ID: {} Trainer with username: {} is already active, reactivating.", transactionId, username);
    } else {
      trainer.getUser().setIsActive(true);
      logger.info("Transaction ID: {} Trainer with username: {} has been activated.", transactionId, username);
    }

    return trainerRepository.save(trainer);
  }

  @Transactional
  public Trainer deactivateTrainer(String username) throws NotFoundException {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Attempting to deactivate trainer with username: {}", transactionId, username);

    Trainer trainer = trainerRepository.findByUser_Username(username)
            .orElseThrow(() -> {
              logger.warn("Transaction ID: {} Trainer not found with username: {}", transactionId, username);
              return new NotFoundException("Not found" + username);
            });

    if (!trainer.getUser().getIsActive()) {
      logger.info("Transaction ID: {} Trainer with username: {} is already inactive, deactivating.", transactionId, username);
    } else {
      trainer.getUser().setIsActive(false);
      logger.info("Transaction ID: {} Trainer with username: {} has been deactivated.", transactionId, username);
    }

    return trainerRepository.save(trainer);
  }

  @Transactional
  public Trainer updateTrainer(@Valid Trainer updatedTrainer)
      throws NotFoundException {
    Long id = updatedTrainer.getId();
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Attempting to update Trainer with ID: {}", transactionId, id);

    Trainer trainer = trainerRepository.findById(id).orElseThrow(() -> {
      logger.warn("Transaction ID: {} Trainer not found with ID: {}", transactionId, id);
      return new NotFoundException("Not found" + id);
    });

    logger.info("Transaction ID: {} Updating Trainer with ID: {}", transactionId, id);
    trainer.getUser().setFirstName(updatedTrainer.getUser().getFirstName());
    trainer.getUser().setLastName(updatedTrainer.getUser().getLastName());
    trainer.getUser().setUsername(updatedTrainer.getUser().getUsername());
    trainer.getUser().setPassword(updatedTrainer.getUser().getPassword());
    trainer.getUser().setIsActive(updatedTrainer.getUser().getIsActive());
    trainer.setSpecialization(updatedTrainer.getSpecialization());

    Trainer updated = trainerRepository.save(trainer);
    logger.info("Transaction ID: {} Trainer with ID: {} successfully updated", transactionId, id);

    return updated;
  }

  @Transactional
  public List<Trainee> getTraineesList(Long id) {
    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Fetching trainees list for trainer with ID: {}", transactionId, id);

    List<Trainee> trainees = trainerRepository.findTrainees(id);

    logger.info("Transaction ID: {} Fetched {} trainees for trainer with ID: {}", transactionId, trainees.size(), id);

    return trainees;
  }

  @Transactional
  public List<Training>
  getTrainerTrainingsByCriteria(String username, Date fromDate, Date toDate,
                                String traineeName) throws NotFoundException {

    String transactionId = MDC.get("transactionId");
    logger.info("Transaction ID: {} Fetching trainings for trainer with username: {} from {} to {}", transactionId, username, fromDate, toDate);

    Optional<Trainer> trainerOptional = trainerRepository.findByUser_Username(username);
    if (trainerOptional.isEmpty()) {
      logger.warn("Transaction ID: {} Trainer not found with username: {}", transactionId, username);
      throw new NotFoundException("Not found" + username);
    }

    List<Training> trainings = trainerRepository.findTrainingsByCriteria(username, fromDate, toDate, traineeName);

    logger.info("Transaction ID: {} Fetched {} trainings for trainer with username: {}", transactionId, trainings.size(), username);

    return trainings;
  }

  private String generateRandomPassword() {
    return UUID.randomUUID().toString().substring(0, 10);
  }
}
