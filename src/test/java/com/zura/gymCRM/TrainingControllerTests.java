package com.zura.gymCRM;

import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.facade.GymFacade;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class TrainingControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GymFacade gymFacade;

    @Test
    @Order(1)
    void testAddTraining_Success() throws Exception {
        Trainee trainee = gymFacade.addTrainee("Jane", "Doe", true, new Date(), "123 Main St");

        Optional<TrainingType> trainingTypeOpt = gymFacade.selectTrainingTypeByID(1L);
        if (trainingTypeOpt.isEmpty()) {
            throw new IllegalStateException("TrainingType with ID 1 not found!");
        }
        TrainingType trainingType = trainingTypeOpt.get();

        Trainer trainer = gymFacade.addTrainer("Jan", "Do", true, trainingType);

        String traineeUsername = trainee.getUser().getUsername();
        String trainerUsername = trainer.getUser().getUsername();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String trainingDate = dateFormat.format(new Date());

        mockMvc.perform(post("/training")
                        .param("traineeUsername", traineeUsername)
                        .param("trainerUsername", trainerUsername)
                        .param("trainingName", "Cardio Session")
                        .param("trainingDate", trainingDate) // Pass formatted date
                        .param("trainingDuration", "60")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().string("Training added successfully"));
    }


    @Test
    @Order(2)
    void testAddTraining_Failure_InvalidTraineeOrTrainer() throws Exception {
        Optional<TrainingType> trainingTypeOpt = gymFacade.selectTrainingTypeByID(1L);
        TrainingType trainingType = trainingTypeOpt.get();

        Trainer validTrainer = gymFacade.addTrainer("Jan", "Do", true, trainingType);
        String validTrainerUsername = validTrainer.getUser().getUsername();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String trainingDate = dateFormat.format(new Date());

        mockMvc.perform(post("/training")
                        .param("traineeUsername", "InvalidTrainee")
                        .param("trainerUsername", validTrainerUsername)
                        .param("trainingName", "Strength Training")
                        .param("trainingDate", trainingDate)
                        .param("trainingDuration", "45")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Trainee not found"));

        Trainee validTrainee = gymFacade.addTrainee("Jane", "Doe", true, new Date(), "123 Main St");
        String validTraineeUsername = validTrainee.getUser().getUsername();


        mockMvc.perform(post("/training")
                        .param("traineeUsername", validTraineeUsername)
                        .param("trainerUsername", "InvalidTrainer")
                        .param("trainingName", "Yoga Session")
                        .param("trainingDate", trainingDate)
                        .param("trainingDuration", "30")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Trainer not found"));
    }



}
