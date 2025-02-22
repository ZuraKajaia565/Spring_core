package com.zura.gymCRM.storage;

import com.zura.gymCRM.model.Trainee;
import com.zura.gymCRM.model.Training;
import com.zura.gymCRM.model.TrainingType;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TraineeStorage {
  private Map<Integer, Trainee> traineeMap = new HashMap<>();

  @Value("${storage.trainee.file}") private String traineeFilePath;
  @Value("${storage.training.file}") private String trainingFilePath;

  public Map<Integer, Trainee> getTraineeMap() { return traineeMap; }

  @PostConstruct
  public void initializeStorage() {
    loadTraineesFromFile();
  }

  private void loadTraineesFromFile() {
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
             new BufferedReader(new FileReader(traineeFilePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        System.out.println(line);
        String[] data = line.split(",");
        String[] temp = new String[7];
        for (String s : data1) {
          String[] t = s.split(",");
          if (t[0].equals(data[0])) {
            temp = t;
            break;
          }
        }
        Training training = new Training(
            Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()),
            temp[2].trim(), temp[3].trim(), LocalDate.parse(temp[4].trim()),
            Integer.parseInt(temp[5].trim()), new TrainingType(temp[6].trim()));
        Trainee trainee = new Trainee(
            Integer.parseInt(data[0].trim()), data[1].trim(), data[2].trim(),
            data[3].trim(), data[4].trim(),
            Boolean.parseBoolean(data[5].trim()),
            LocalDate.parse(data[6].trim(), DateTimeFormatter.ISO_LOCAL_DATE),
            data[7].trim(), training);
        traineeMap.put(Integer.parseInt(data[0].trim()), trainee);
      }
      System.out.println("Trainee storage initialized with " +
                         traineeMap.size() + " records.");
    } catch (IOException e) {
      System.err.println("Error loading trainees: " + e.getMessage());
    }
  }
}
