
package com.zura.gymCRM.storage;

import com.zura.gymCRM.model.Training;
import com.zura.gymCRM.model.TrainingType;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TrainingStorage {
  private Map<List<Integer>, Training> trainingMap = new HashMap<>();

  @Value("${storage.training.file}") private String trainingDataFilePath;

  public Map<List<Integer>, Training> getTrainingMap() { return trainingMap; }

  @PostConstruct
  private void loadTrainingData() {
    try (BufferedReader br =
             new BufferedReader(new FileReader(trainingDataFilePath))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] data = line.split(",");
        List<Integer> keys1 = Arrays.asList(Integer.parseInt(data[0].trim()),
                                            Integer.parseInt(data[1].trim()));

        Training training = new Training(
            Integer.parseInt(data[0].trim()), Integer.parseInt(data[1].trim()),
            data[2].trim(), data[3].trim(), LocalDate.parse(data[4].trim()),
            Integer.parseInt(data[5].trim()), new TrainingType(data[6].trim()));
        trainingMap.put(keys1, training);
      }
    } catch (IOException e) {
      System.err.println("Failed to load training data: " + e.getMessage());
    }
  }
}
