package com.zura.gymCRM.dao;

import com.zura.gymCRM.exceptions.AddException;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.model.Trainer;
import com.zura.gymCRM.storage.TrainerStorage;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainerDAO {
  private TrainerStorage storage;

  @Autowired
  public TrainerDAO(TrainerStorage storage) {
    this.storage = storage;
  }

  public void addTrainer(Trainer trainer) throws AddException {
    if (storage.getTrainerMap().containsKey(trainer.getUserId())) {
      throw new AddException("Trainer already exists");
    }
    storage.getTrainerMap().put(trainer.getUserId(), trainer);
  }

  public Trainer getTrainer(int userId) throws NotFoundException {
    if (!storage.getTrainerMap().containsKey(userId)) {
      throw new NotFoundException("Trainer doesnot exist");
    }
    return storage.getTrainerMap().get(userId);
  }

  public boolean updateTrainer(Trainer updatedTrainer) {
    if (storage.getTrainerMap().containsKey(updatedTrainer.getUserId()) &&
        updatedTrainer.getPassword().length() == 10) {
      storage.getTrainerMap().put(updatedTrainer.getUserId(), updatedTrainer);
      return true;
    }
    return false;
  }
}
