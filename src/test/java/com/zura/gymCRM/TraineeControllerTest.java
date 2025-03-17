package com.zura.gymCRM;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zura.gymCRM.dto.*;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.facade.GymFacade;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;


@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GymFacade gymFacade;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Order(1)
    void registerTrainee_Success() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest(
                "Jon1", "Doe", new Date(), "123 Main St"
        );

        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.password").exists());
    }

    @Test
    @Order(2)
    void registerTrainee_Failure() throws Exception {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest(
                "Jon", "", new Date(), "123 Main St"
        );

        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    void getTraineeProfile_Success() throws Exception {
        String username = "Jon1.Doe";

        mockMvc.perform(get("/api/trainees/{username}/account", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.dateOfBirth").exists())
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.isActive").exists());
    }

    @Test
    @Order(4)
    void getTraineeProfile_Failure_TraineeNotFound() throws Exception {
        String username = "non_existing_user";

        mockMvc.perform(get("/api/trainees/{username}/account", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Trainee not found"));
    }


    @Test
    @Order(5)
    void updateTraineeProfile_Success() throws Exception {
        String username = "Jon1.Doe";

        UpdateTraineeRequest request = new UpdateTraineeRequest(
                 "Jon", "UpdatedLast", new Date(), "Updated Address", true
        );

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/api/trainees/{username}/account-update", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value("Jon"))
                .andExpect(jsonPath("$.lastName").value("UpdatedLast"))
                .andExpect(jsonPath("$.dateOfBirth").exists())
                .andExpect(jsonPath("$.address").value("Updated Address"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    @Order(6)
    void updateTraineeProfile_Failure_TraineeNotFound() throws Exception {
        String username = "non_existing_user";


        UpdateTraineeRequest request = new UpdateTraineeRequest(
                 "John", "UpdatedLast", new Date(), "Updated Address", true
        );

        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(put("/api/trainees/{username}/account-update", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(7)
    void getUnassignedActiveTrainers_Success() throws Exception {
        String username = "Jon1.Doe";
        Optional<TrainingType> trainingType = gymFacade.selectTrainingTypeByID(1L);
        gymFacade.addTrainer("Jan", "Do", true, trainingType.get());
        mockMvc.perform(get("/api/trainees/{username}/unassigned-trainers", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("Jan"))
                .andExpect(jsonPath("$[0].lastName").value("Do"));
    }

    @Test
    @Order(8)
    void getUnassignedActiveTrainers_Failure() throws Exception {
        String username = "Jon45.Doe";
        Optional<TrainingType> trainingType = gymFacade.selectTrainingTypeByID(1L);
        gymFacade.addTrainer("Jane", "Do", true, trainingType.get());
        mockMvc.perform(get("/api/trainees/{username}/unassigned-trainers", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    void updateTraineeTrainerList_Success() throws Exception {
        Optional<TrainingType> trainingType = gymFacade.selectTrainingTypeByID(1L);
        gymFacade.addTrainer("jan", "doe", true, trainingType.get());
        gymFacade.addTrainer("sam", "smith", true, trainingType.get());
        mockMvc.perform(put("/api/trainees/{username}/update-trainers", "Jon1.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainersList\": [\"jan.doe\", \"sam.smith\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("jan.doe"))
                .andExpect(jsonPath("$[0].firstName").value("jan"))
                .andExpect(jsonPath("$[0].lastName").value("doe"))
                .andExpect(jsonPath("$[1].username").value("sam.smith"))
                .andExpect(jsonPath("$[1].firstName").value("sam"))
                .andExpect(jsonPath("$[1].lastName").value("smith"));
    }

    @Test
    @Order(10)
    void updateTraineeTrainerList_TraineeNotFound() throws Exception {
        mockMvc.perform(put("/api/trainees/{username}/update-trainers", "nonexistent.username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainersList\": [\"jan.doe\", \"sam.smith\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Trainee not found"));
    }

    @Test
    @Order(11)
    void getTraineeTrainingsList_Success() throws Exception {
        Trainee trainee =gymFacade.addTrainee("Jane", "Doe", true, new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"), "123 Main St");
        Optional<TrainingType> trainingType = gymFacade.selectTrainingTypeByID(1L);
        Trainer trainer =  gymFacade.addTrainer("Jan", "Do", true, trainingType.get());
        Training training = gymFacade.addTraining(trainee, trainer, "yoga", trainingType.get(),new Date(), 60);

        mockMvc.perform(get("/api/trainees/{username}/trainings", "Jane.Doe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].trainingName").value("yoga"))
                .andExpect(jsonPath("$[0].trainingType").value("Strength"))
                .andExpect(jsonPath("$[0].trainerName").value("Jan.Do1"));
    }

    @Test
    @Order(12)
    void getTraineeTrainingsList_TraineeNotFound() throws Exception {
        mockMvc.perform(get("/api/trainees/{username}/trainings", "NonExistentUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Trainee not found"));
    }

    @Test
    @Order(13)
    void deleteTrainee_Success() throws Exception {
        String username = "Jon1.Doe";

        mockMvc.perform(delete("/api/trainees/{username}/delete", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value("Trainee deleted successfully"));

        mockMvc.perform(get("/api/trainees/{username}/account", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(14)
    void activateTrainee_Success() throws Exception {
        ActivateDeActivateRequest request = new ActivateDeActivateRequest();
        request.setIsActive(true);


        mockMvc.perform(patch("/api/trainees/{username}/activate", "Jane.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("Jane.Doe"))
                .andExpect(jsonPath("$.user.isActive").value(true));
    }

    @Test
    @Order(15)
    void activateTrainee_Failure() throws Exception {
        ActivateDeActivateRequest request = new ActivateDeActivateRequest();
        request.setIsActive(false);
        mockMvc.perform(patch("/api/trainees/{username}/activate", "InvalidUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(16)
    void deactivateTrainee_Success() throws Exception {
        ActivateDeActivateRequest request = new ActivateDeActivateRequest();
        request.setIsActive(true);
        mockMvc.perform(patch("/api/trainees/{username}/deactivate", "Jane.Doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.username").value("Jane.Doe"))
                .andExpect(jsonPath("$.user.isActive").value(false));
    }

    @Test
    @Order(17)
    void deactivateTrainee_Failure() throws Exception {
        ActivateDeActivateRequest request = new ActivateDeActivateRequest();
        request.setIsActive(true);
        mockMvc.perform(patch("/api/trainees/{username}/deactivate", "InvalidUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


}

