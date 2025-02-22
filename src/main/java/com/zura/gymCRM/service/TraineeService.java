package com.zura.gymCRM.service;

import com.zura.gymCRM.dao.TraineeDAO;
import com.zura.gymCRM.exceptions.AddException;
import com.zura.gymCRM.exceptions.NotFoundException;
import com.zura.gymCRM.model.Trainee;
import com.zura.gymCRM.model.Training;
import com.zura.gymCRM.storage.TraineeStorage;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TraineeService {
	private TraineeDAO traineeDAO;
	private TraineeStorage traineeStorage;

	@Autowired
	public void setTraineeDAO(TraineeDAO traineeDAO) {
		this.traineeDAO = traineeDAO;
	}

	@Autowired
	public void setTraineeStorage(TraineeStorage traineeStorage) {
		this.traineeStorage = traineeStorage;
	}

	private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);

	public Trainee createTrainee(int userId, String firstName, String lastName,
			Boolean isAcive, LocalDate dateOfBirth,
			String address, Training training) {
		try {
			logger.info("Attempting to create trainee: {} {}", firstName, lastName);

			String username = generateUsername(firstName, lastName);
			String password = generateRandomPassword();
			Trainee trainee = new Trainee(userId, firstName, lastName, username, password, isAcive,
					dateOfBirth, address, training);

			traineeDAO.addTrainee(trainee);
			logger.info("Trainee created successfully: {}", username);

			return trainee;
		} catch (Exception e) {
			logger.error("Error while creating trainee: {} {}", firstName, lastName,
					e);
			throw new AddException("Failed to create trainee: " + firstName + " " +
					lastName);
		}
	}

	public Trainee updateTrainee(Trainee trainee) {
		try {
			logger.info("Attempting to update trainee: {}", trainee.getUsername());

			if (traineeDAO.updateTrainee(trainee)) {
				logger.info("Trainee updated successfully: {}", trainee.getUsername());
				return trainee;
			} else {
				logger.warn("Trainee not found: {}", trainee.getUsername());
				throw new RuntimeException("Trainee not found: " +
						trainee.getUsername());
			}
		} catch (Exception e) {
			logger.error("Error while updating trainee: {}", trainee.getUsername(),
					e);
			throw new RuntimeException(
					"Failed to update trainee: " + trainee.getUsername(), e);
		}
	}

	public void deleteTrainee(int userId) {
		traineeDAO.deleteTrainee(userId);
	}

	public Trainee selectTrainee(int userId) {
		try {
			logger.info("Fetching trainee with ID: {}", userId);
			Trainee trainee = traineeDAO.getTrainee(userId);
			if (trainee != null) {
				logger.info("Trainee found: {}", trainee.getUsername());
			} else {
				logger.warn("Trainee not found with ID: {}", userId);
			}
			return trainee;
		} catch (Exception e) {
			logger.error("Error while selecting trainee with ID: {}", userId, e);
			throw new NotFoundException("Failed to retrieve trainee with ID: " +
					userId);
		}
	}

	private String generateUsername(String firstName, String lastName) {
		String s = firstName + "." + lastName;
		int cnt = 0;
		for (Map.Entry<Integer, Trainee> entry : traineeStorage.getTraineeMap().entrySet()) {
			Trainee trainee = entry.getValue();
			if (trainee.getFirstName().equals(firstName) &&
					trainee.getLastName().equals(lastName)) {
				cnt++;
			}
		}
		if (cnt != 0) {
			s += cnt;
		}
		return s;
	}

	private String generateRandomPassword() {
		return UUID.randomUUID().toString().substring(0, 10);
	}
}
