package com.zura.gymCRM.dao;

import com.zura.gymCRM.exceptions.AddException;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.model.Trainee;
import com.zura.gymCRM.storage.TraineeStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TraineeDAO {
  private TraineeStorage storage;

  @Autowired
  public void setTraineeStorage(TraineeStorage storage) {
    this.storage = storage;
  }

  public TraineeDAO(TraineeStorage storage) { this.storage = storage; }

  public void addTrainee(Trainee trainee) throws AddException {
    if (storage.getTraineeMap().get(trainee.getUserId()) != null) {
      throw new AddException("Trainee already exists");
    }
    storage.getTraineeMap().put(trainee.getUserId(), trainee);
  }

  public Trainee getTrainee(int userId) throws NotFoundException {
    if (storage.getTraineeMap().get(userId) == null) {
      throw new NotFoundException("Trainee not found");
    }
    return storage.getTraineeMap().get(userId);
  }

  public boolean updateTrainee(Trainee updatedTrainee) {
    System.out.println(updatedTrainee.getPassword().length());
    if (storage.getTraineeMap().containsKey(updatedTrainee.getUserId()) &&
        (updatedTrainee.getPassword().length() == 10)) {

      storage.getTraineeMap().put(updatedTrainee.getUserId(), updatedTrainee);
      return true;
    }
    return false;
  }

  public void deleteTrainee(int userId) {
    storage.getTraineeMap().remove(userId);
  }
}
