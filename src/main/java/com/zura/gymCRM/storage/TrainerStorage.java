package com.zura.gymCRM.storage;

import com.zura.gymCRM.model.Trainer;
import com.zura.gymCRM.model.Training;
import com.zura.gymCRM.model.TrainingType;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TrainerStorage {
  private Map<Integer, Trainer> trainerMap = new HashMap<>();

  @Value("${storage.trainer.file}") private String trainerFilePath;
  @Value("${storage.training.file}") private String trainingFilePath;

  public Map<Integer, Trainer> getTrainerMap() { return trainerMap; }

  @PostConstruct
  public void initializeStorage() {
    loadTrainersFromFile();
  }

  private void loadTrainersFromFile() {
    List<String> data1 = new ArrayList<>();
    try (BufferedReader reader =
             new BufferedReader(new FileReader(trainingFilePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        data1.add(line);
      }
    } catch (IOException e) {
      System.err.println("Error loading trainees: " + e.getMessage());
    }

    try (BufferedReader reader =
             new BufferedReader(new FileReader(trainerFilePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
        String[] data = line.split(",");
        String[] temp = new String[7];
        for (String s : data1) {
          String[] t = s.split(",");
          if (t[1].equals(data[0])) {
            temp = t;
            break;
          }
        }
        Training training = new Training(
            Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()),
            temp[2].trim(), temp[3].trim(), LocalDate.parse(temp[4].trim()),
            Integer.parseInt(temp[5].trim()), new TrainingType(temp[6].trim()));
        Trainer trainee =
            new Trainer(Integer.parseInt(data[0].trim()), data[1].trim(),
                        data[2].trim(), data[3].trim(), data[4].trim(),
                        Boolean.parseBoolean(data[5].trim()), data[6].trim(),
                        new TrainingType(data[7].trim()), training);
        trainerMap.put(Integer.parseInt(data[0].trim()), trainee);
      }
      System.out.println("Trainee storage initialized with " +
                         trainerMap.size() + " records.");
    } catch (IOException e) {
      System.err.println("Error loading trainees: " + e.getMessage());
    }
  }
}
