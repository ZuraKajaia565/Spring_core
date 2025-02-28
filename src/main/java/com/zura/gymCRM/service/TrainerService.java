package com.zura.gymCRM.service;

import com.zura.gymCRM.dao.TrainerRepository;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.exceptions.AddException;
import com.zura.gymCRM.exceptions.NotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {
  private TrainerRepository trainerRepository;

  @Autowired
  public void setTrainerRepository(TrainerRepository trainerRepository) {
    this.trainerRepository = trainerRepository;
    ;
  }

  private static final Logger logger =
      LoggerFactory.getLogger(TrainerService.class);

  public Trainer createTrainer(Trainer trainer) {

    logger.info("Attempting to create trainer: {} {}",
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName());

    List<Trainer> userlist = trainerRepository.findAll();
    trainer.getUser().setUsername(UsernameGenerator.generateUsername(
        trainer.getUser().getFirstName(), trainer.getUser().getLastName(),
        userlist));
    trainer.getUser().setPassword(generateRandomPassword());
    trainerRepository.save(trainer);
    logger.info("Trainer created successfully: {}",
                trainer.getUser().getUsername());

    return trainer;
  }

  public Optional<Trainer> getTrainer(Long userId) {

    logger.info("Fetching trainer with ID: {}", userId);
    Optional<Trainer> trainer = trainerRepository.findById(userId);
    trainer.ifPresentOrElse(
        t
        -> logger.info("Trainer found: {}", t.getUser().getUsername()),
        () -> logger.warn("Trainer not found with ID: {}", userId));

    return trainer;
  }

  public Trainer findTrainerByUsername(String username)
      throws NotFoundException {
    return trainerRepository.findByUser_Username(username).orElseThrow(
        () -> new NotFoundException(username));
  }

  @Transactional
  public void changePassword(Long trainerId, String newPassword)
      throws EntityNotFoundException {
    Optional<Trainer> trainerOpt = trainerRepository.findById(trainerId);
    if (trainerOpt.isPresent()) {
      Trainer trainer = trainerOpt.get();
      trainer.getUser().setPassword(newPassword);
      trainerRepository.save(trainer); // Persist the updated entity
    } else {
      throw new EntityNotFoundException("Trainee not found");
    }
  }

  @Transactional
  public Trainer activateTrainer(Long id) throws NotFoundException {
    Trainer trainer = trainerRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Not found" + id));

    if (trainer.getUser().getIsActive()) {
      System.out.println("Trainer with ID " + id +
                         " is already active, reactivating.");
    } else {
      trainer.getUser().setIsActive(true);
    }

    return trainerRepository.save(trainer);
  }

  @Transactional
  public Trainer deactivateTrainer(Long id) throws NotFoundException {
    Trainer trainer = trainerRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Not found" + id));

    if (!trainer.getUser().getIsActive()) {
      System.out.println("Trainer with ID " + id +
                         " is already inactive, deactivating.");
    } else {
      trainer.getUser().setIsActive(false);
    }

    return trainerRepository.save(trainer);
  }

  @Transactional
  public Trainer updateTrainer(@Valid Trainer updatedTrainer)
      throws NotFoundException {
    Long id = updatedTrainer.getId();
    logger.info("Attempting to update Trainer with ID: {}", id);

    Trainer trainer = trainerRepository.findById(id).orElseThrow(
        () -> new NotFoundException("Not found" + id));

    logger.info("Updating Trainer with ID: {}", id);
    trainer.getUser().setFirstName(updatedTrainer.getUser().getFirstName());
    trainer.getUser().setLastName(updatedTrainer.getUser().getLastName());
    trainer.getUser().setUsername(updatedTrainer.getUser().getUsername());
    trainer.getUser().setPassword(updatedTrainer.getUser().getPassword());
    trainer.getUser().setIsActive(updatedTrainer.getUser().getIsActive());
    trainer.setSpecialization(updatedTrainer.getSpecialization());

    Trainer updated = trainerRepository.save(trainer);
    logger.info("Trainer with ID: {} successfully updated", id);

    return updated;
  }

  private String generateRandomPassword() {
    return UUID.randomUUID().toString().substring(0, 10);
  }
}
