package com.zura.gymCRM.dao;

import com.zura.gymCRM.exceptions.AddException;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.model.Training;
import com.zura.gymCRM.storage.TrainingStorage;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainingDAO {
  private TrainingStorage storage;

  @Autowired
  public TrainingDAO(TrainingStorage storage) {
    this.storage = storage;
  }

  public void addTraining(Training training) throws AddException {
    List<Integer> ke =
        Arrays.asList(training.getTraineeId(), training.getTrainerId());
    if (storage.getTrainingMap().containsKey(ke)) {
      throw new AddException("Training already exists");
    }
    storage.getTrainingMap().put(ke, training);
  }

  public Training getTraining(int traineeId, int trainerId)
      throws NotFoundException {
    List<Integer> ke = Arrays.asList(traineeId, trainerId);
    System.out.println(ke);
    if (!storage.getTrainingMap().containsKey(ke)) {
      throw new NotFoundException("Training doesnot already exists");
    }
    return storage.getTrainingMap().get(ke);
  }
}
