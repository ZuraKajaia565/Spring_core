package com.zura.gymCRM;

import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.User;
import java.util.List;

public class UsernameGenerator {
  public static <T> String generateUsername(String firstName, String lastName,
                                            List<T> userList) {
    String userName = firstName + "." + lastName;
    int cnt = 0;

    for (T user : userList) {
      if (user instanceof Trainee trainee) {
        User traineeUser = trainee.getUser();
        if (traineeUser.getFirstName().equals(firstName) &&
            traineeUser.getLastName().equals(lastName)) {
          cnt++;
        }
      } else if (user instanceof Trainer trainer) {
        User trainerUser = trainer.getUser();
        if (trainerUser.getFirstName().equals(firstName) &&
            trainerUser.getLastName().equals(lastName)) {
          cnt++;
        }
      }
    }

    if (cnt > 0) {
      userName += cnt;
    }

    return userName;
  }
}
