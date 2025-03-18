package com.zura.gymCRM;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.TrainingType;
import com.zura.gymCRM.facade.GymFacade;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class LoginControllerTests {
    @Autowired
    GymFacade gymFacade;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Order(1)
    void login_Success_Trainee() throws Exception {
        Trainee trainee =gymFacade.addTrainee("Jane", "Doe", true, new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"), "123 Main St");
        String username = trainee.getUser().getUsername();
        String password = trainee.getUser().getPassword();

        mockMvc.perform(get("/api/login")
                        .param("username", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("Trainee"))
                .andExpect(jsonPath("$.message").value("Login successful!"));
    }

    @Test
    @Order(2)
    void login_Success_Trainer() throws Exception {
        Optional<TrainingType> trainingType = gymFacade.selectTrainingTypeByID(1L);
        Trainer trainer = gymFacade.addTrainer("Jan", "Do", true, trainingType.get());
        String username = trainer.getUser().getUsername();
        String password = trainer.getUser().getPassword();
        mockMvc.perform(get("/api/login")
                        .param("username", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("Trainer"))
                .andExpect(jsonPath("$.message").value("Login successful!"));
    }

    @Test
    @Order(3)
    void login_Failure_InvalidCredentials_Trainee() throws Exception {
        Trainee trainee =gymFacade.addTrainee("Jane", "Doe", true, new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"), "123 Main St");
        String username = trainee.getUser().getUsername();
        String password = "dwdwdwqddwqdwdqwd";

        mockMvc.perform(get("/api/login")
                        .param("username", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.role").value("Unknown"))
                .andExpect(jsonPath("$.message").value("Invalid username or password."));
    }

    @Test
    @Order(4)
    void login_Failure_InvalidCredentials_Trainer() throws Exception {
        Optional<TrainingType> trainingType = gymFacade.selectTrainingTypeByID(1L);
        Trainer trainer = gymFacade.addTrainer("Jan", "Do", true, trainingType.get());
        String username = trainer.getUser().getUsername();
        String password = "swswwdwqdqwd";

        mockMvc.perform(get("/api/login")
                        .param("username", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.role").value("Unknown"))
                .andExpect(jsonPath("$.message").value("Invalid username or password."));
    }

    @Test
    @Order(5)
    void testChangePasswordForTrainee_Success() throws Exception {
        Trainee trainee =gymFacade.addTrainee("Zaza", "Doe", true, new SimpleDateFormat("yyyy-MM-dd").parse("1990-01-01"), "123 Main St");
        String username = trainee.getUser().getUsername();
        String oldPassword = trainee.getUser().getPassword();
        String newPassword = "ewPassword";


        mockMvc.perform(put("/api/{username}/password", username)
                        .param("oldPassword", oldPassword)
                        .param("newPassword", newPassword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("Trainee"))
                .andExpect(jsonPath("$.message").value("Password changed successfully!"));
    }

    @Test
    @Order(6)
    void testChangePasswordForTrainer_Success() throws Exception {
        Optional<TrainingType> trainingType = gymFacade.selectTrainingTypeByID(1L);
        Trainer trainer = gymFacade.addTrainer("Zaur", "Do", true, trainingType.get());
        String username = trainer.getUser().getUsername();
        String oldPassword = trainer.getUser().getPassword();
        String newPassword = "1234567898";

        mockMvc.perform(put("/api/{username}/password", username)
                        .param("oldPassword", oldPassword)
                        .param("newPassword", newPassword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("Trainer"))
                .andExpect(jsonPath("$.message").value("Password changed successfully!"));
    }





}
