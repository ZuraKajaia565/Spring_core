package com.zura.gymCRM;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zura.gymCRM.dto.ActivateDeActivateRequest;
import com.zura.gymCRM.dto.TrainerRegistrationRequest;
import com.zura.gymCRM.dto.UpdateTrainerRequest;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
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

import java.text.SimpleDateFormat;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GymFacade gymFacade;

    private final ObjectMapper objectMapper = new ObjectMapper();



    @Test
    @Order(1)
    void registerTrainer_Success() throws Exception {
        Optional<TrainingType> trainingType = gymFacade.selectTrainingTypeByName("Cardio");
        assertTrue(trainingType.isPresent());
        TrainerRegistrationRequest request = new TrainerRegistrationRequest("Zura", "Doe", trainingType.get());

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Zura.Doe"));
    }

    @Test
    @Order(2)
    void registerTrainer_Failure() throws Exception {
        TrainingType invalidTrainingType = new TrainingType("Cardio12");
        TrainerRegistrationRequest request = new TrainerRegistrationRequest("John", "Doe", invalidTrainingType);

        mockMvc.perform(post("/api/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @Order(3)
    void getTraineeProfile_Success() throws Exception {
        String username = "Zura.Doe";
        Trainer trainer = gymFacade.selectTrainerByUsername(username).orElse(null);

            mockMvc.perform(get("/api/trainers/{username}/account", username)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.firstName").value(trainer.getUser().getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(trainer.getUser().getLastName()))
                    .andExpect(jsonPath("$.specialization").value(trainer.getSpecialization().getTrainingTypeName()))
                    .andExpect(jsonPath("$.isActive").value(trainer.getUser().getIsActive()));
    }

    @Test
    @Order(4)
    void getTraineeProfile_Failure() throws Exception {
        String username = "John.Doe123";
        Trainer trainer = gymFacade.selectTrainerByUsername(username).orElse(null);

        mockMvc.perform(get("/api/trainers/{username}/account", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    void updateTrainerProfile_Success() throws Exception {
        String existingUsername = "Zura.Doe";
        Optional<Trainer> existingTrainer = gymFacade.selectTrainerByUsername(existingUsername);
        assertTrue(existingTrainer.isPresent(), "Trainer should exist for update");

        UpdateTrainerRequest updateTrainerRequest = new UpdateTrainerRequest();
        updateTrainerRequest.setFirstName("Zura");
        updateTrainerRequest.setLastName("Updated");
        updateTrainerRequest.setSpecialization("Cardio");
        updateTrainerRequest.setIsActive(true);

        mockMvc.perform(put("/api/trainers/{username}/account-update", existingUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTrainerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Zura"))
                .andExpect(jsonPath("$.lastName").value("Updated"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    @Order(6)
    void updateTrainerProfile_Failure_TrainerNotFound() throws Exception {
        String nonExistentUsername = "NonExistentUser";
        UpdateTrainerRequest updateTrainerRequest = new UpdateTrainerRequest();
        updateTrainerRequest.setFirstName("Unknown");
        updateTrainerRequest.setLastName("User");
        updateTrainerRequest.setSpecialization("Cardio");
        updateTrainerRequest.setIsActive(true);
        mockMvc.perform(put("/api/trainers/{username}/account-update", nonExistentUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateTrainerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Trainer not found"));
    }

    @Test
    @Order(7)
    void activateTrainer_Success() throws Exception {
        String usernameToActivate = "Zura.Doe";
        ActivateDeActivateRequest request = new ActivateDeActivateRequest();

        Trainer trainerBeforeActivation = gymFacade.selectTrainerByUsername(usernameToActivate).orElse(null);
        assertNotNull(trainerBeforeActivation, "Trainer should exist for activation test");

        mockMvc.perform(patch("/api/trainers/{username}/activate", usernameToActivate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Trainer trainerAfterActivation = gymFacade.selectTrainerByUsername(usernameToActivate).orElse(null);
        assertNotNull(trainerAfterActivation, "Trainer should exist after activation");
        assertTrue(trainerAfterActivation.getUser().getIsActive(), "Trainer should be active now");
    }

    @Test
    @Order(8)
    void activateTrainer_Failure_TrainerNotFound() throws Exception {
        String invalidUsername = "NonExistentUser";
        ActivateDeActivateRequest request = new ActivateDeActivateRequest();

        mockMvc.perform(patch("/api/trainers/{username}/activate", invalidUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    void deactivateTrainer_Success() throws Exception {
        String usernameToDeactivate = "Zura.Doe";
        ActivateDeActivateRequest request = new ActivateDeActivateRequest();

        Trainer trainerBeforeDeactivation = gymFacade.selectTrainerByUsername(usernameToDeactivate).orElse(null);
        assertNotNull(trainerBeforeDeactivation, "Trainer should exist for deactivation test");
        assertTrue(trainerBeforeDeactivation.getUser().getIsActive(), "Trainer should initially be active");

        mockMvc.perform(patch("/api/trainers/{username}/deactivate", usernameToDeactivate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Trainer trainerAfterDeactivation = gymFacade.selectTrainerByUsername(usernameToDeactivate).orElse(null);
        assertNotNull(trainerAfterDeactivation, "Trainer should exist after deactivation");
        assertFalse(trainerAfterDeactivation.getUser().getIsActive(), "Trainer should be inactive now");
    }

    @Test
    @Order(10)
    void deactivateTrainer_Failure_TrainerNotFound() throws Exception {
        String invalidUsername = "NonExistentUser";
        ActivateDeActivateRequest request = new ActivateDeActivateRequest();

        mockMvc.perform(patch("/api/trainers/{username}/deactivate", invalidUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(11)
    void getTraineeTrainingsList_Success() throws Exception {
        String trainerUsername = "Jan.Do";
        String traineeUsername = "Jane.Doe";
        Optional<TrainingType> trainingType = gymFacade.selectTrainingTypeByID(1L);
        gymFacade.addTrainer("Jan", "Do", true, trainingType.get());
        gymFacade.addTrainee("Jane", "Doe", true, new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"), "123 Main St");

        Optional<Trainer> trainerOpt = gymFacade.selectTrainerByUsername(trainerUsername);
        Optional<Trainee> traineeOpt = gymFacade.selectTraineeByusername(traineeUsername);

        assertTrue(trainerOpt.isPresent(), "Trainer should exist for the given username");
        assertTrue(traineeOpt.isPresent(), "Trainee should exist for the given username");


        gymFacade.addTraining(traineeOpt.get(), trainerOpt.get(), "Yoga Class", trainingType.get(),
                new SimpleDateFormat("yyyy-MM-dd").parse("2025-02-01"), 60);

        gymFacade.addTraining(traineeOpt.get(), trainerOpt.get(), "Pilates Class", trainingType.get(),
                new SimpleDateFormat("yyyy-MM-dd").parse("2025-03-01"), 45);


        mockMvc.perform(get("/api/trainers/{username}/trainings", trainerUsername)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Yoga Class"))
                .andExpect(jsonPath("$[1].trainingName").value("Pilates Class"));
    }

    @Test
    @Order(12)
    void getTraineeTrainingsList_Failure_TrainerNotFound() throws Exception {
        String invalidTrainerUsername = "NonExistentTrainer";

        mockMvc.perform(get("/api/trainers/{username}/trainings", invalidTrainerUsername)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}
