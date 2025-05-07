package com.zura.gymCRM.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zura.gymCRM.controller.LoginController;
import com.zura.gymCRM.controller.TraineeController;
import com.zura.gymCRM.controller.TrainerController;
import com.zura.gymCRM.dto.*;
import com.zura.gymCRM.entities.*;
import com.zura.gymCRM.facade.GymFacade;
import com.zura.gymCRM.security.AuthenticationService;
import com.zura.gymCRM.security.JwtService;
import com.zura.gymCRM.security.LoginAttemptService;
import com.zura.gymCRM.security.PasswordUtil;
import com.zura.gymCRM.security.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({TraineeController.class, TrainerController.class, LoginController.class})
public class ControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private GymFacade gymFacade;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @MockBean
    private PasswordUtil passwordUtil;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Mock security context
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test-user");
        SecurityContextHolder.setContext(securityContext);
    }

    // Helper methods
    private User createMockUser(String firstName, String lastName, String username, String password, boolean isActive) {
        User user = new User();
        user.setId(1L);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(isActive);
        return user;
    }

    private Trainee createMockTrainee(User user, Date dateOfBirth, String address) {
        Trainee trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);
        trainee.setTrainers(new ArrayList<>());
        return trainee;
    }

    private Trainer createMockTrainer(User user, TrainingType specialization) {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(user);
        trainer.setSpecialization(specialization);
        trainer.setTrainees(new ArrayList<>());
        return trainer;
    }

    private TrainingType createMockTrainingType(Long id, String name) {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(id);
        trainingType.setTrainingTypeName(name);
        return trainingType;
    }

    // Login Controller Tests
    @Test
    public void testLogin_Success() throws Exception {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("test-user", "password");
        AuthenticationResponse response = new AuthenticationResponse("token123", "Authentication successful");

        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(response);
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.message").value("Authentication successful"));

        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    public void testLogin_Failure_BlockedIP() throws Exception {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest("test-user", "password");

        when(loginAttemptService.isBlocked(anyString())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(429))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.message").value("Account locked due to too many failed attempts. Try again in 5 minutes."));

        verify(authenticationService, never()).authenticate(any(AuthenticationRequest.class));
    }

    @Test
    public void testLoginWithParams_Success() throws Exception {
        // Arrange
        String username = "test-user";
        String password = "password";
        AuthenticationResponse response = new AuthenticationResponse("token123", "Authentication successful");

        when(authenticationService.authenticate(any(AuthenticationRequest.class))).thenReturn(response);
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/api/login")
                        .param("username", username)
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"))
                .andExpect(jsonPath("$.message").value("Authentication successful"));

        verify(authenticationService, times(1)).authenticate(any(AuthenticationRequest.class));
    }



    @Test
    public void testLogout_Success() throws Exception {
        // Arrange
        String token = "validToken";
        doNothing().when(tokenBlacklistService).blacklistToken(token);

        // Act & Assert
        mockMvc.perform(post("/api/logout")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully logged out"))
                .andExpect(jsonPath("$.status").value("success"));

        verify(tokenBlacklistService, times(1)).blacklistToken(token);
    }

    // Trainee Controller Tests
    @Test
    public void testRegisterTrainee_Success() throws Exception {
        // Arrange
        TraineeRegistrationRequest request = new TraineeRegistrationRequest(
                "John", "Doe", new Date(), "123 Main St");

        User mockUser = createMockUser("John", "Doe", "John.Doe", "password", true);
        Trainee mockTrainee = createMockTrainee(mockUser, new Date(), "123 Main St");

        when(gymFacade.addTrainee(anyString(), anyString(), anyBoolean(), any(Date.class), anyString()))
                .thenReturn(mockTrainee);

        // Act & Assert
        mockMvc.perform(post("/api/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.password").value("password"));

        verify(gymFacade, times(1)).addTrainee(eq("John"), eq("Doe"), eq(true), any(Date.class), eq("123 Main St"));
    }

    @Test
    public void testGetTraineeProfile_Success() throws Exception {
        // Arrange
        String username = "John.Doe";
        User mockUser = createMockUser("John", "Doe", username, "password", true);
        Trainee mockTrainee = createMockTrainee(mockUser, new Date(), "123 Main St");

        List<Trainer> trainersList = new ArrayList<>();
        TrainingType mockTrainingType = createMockTrainingType(1L, "Strength");
        User trainerUser = createMockUser("Jane", "Smith", "Jane.Smith", "password", true);
        Trainer mockTrainer = createMockTrainer(trainerUser, mockTrainingType);
        trainersList.add(mockTrainer);

        when(gymFacade.selectTraineeByusername(username)).thenReturn(Optional.of(mockTrainee));
        when(gymFacade.getTrainersListofTrainee(mockTrainee)).thenReturn(trainersList);

        // Act & Assert
        mockMvc.perform(get("/api/trainees/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainerslist[0].username").value("Jane.Smith"))
                .andExpect(jsonPath("$.trainerslist[0].specialization").value("Strength"));

        verify(gymFacade, times(1)).selectTraineeByusername(username);
        verify(gymFacade, times(1)).getTrainersListofTrainee(mockTrainee);
    }

    @Test
    public void testUpdateTraineeProfile_Success() throws Exception {
        // Arrange
        String username = "John.Doe";
        User mockUser = createMockUser("John", "Doe", username, "password", true);
        Trainee mockTrainee = createMockTrainee(mockUser, new Date(), "123 Main St");

        UpdateTraineeRequest request = new UpdateTraineeRequest(
                "John", "Updated", new Date(), "New Address", true);

        List<Trainer> trainersList = new ArrayList<>();

        when(gymFacade.selectTraineeByusername(username)).thenReturn(Optional.of(mockTrainee));
        when(gymFacade.updateTrainee(any(Trainee.class))).thenReturn(mockTrainee);
        when(gymFacade.getTrainersListofTrainee(mockTrainee)).thenReturn(trainersList);

        // Act & Assert
        mockMvc.perform(put("/api/trainees/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));

        verify(gymFacade, times(1)).selectTraineeByusername(username);
        verify(gymFacade, times(1)).updateTrainee(any(Trainee.class));
        verify(gymFacade, times(1)).getTrainersListofTrainee(any(Trainee.class));
    }

    @Test
    public void testUpdateTraineeStatus_Success() throws Exception {
        // Arrange
        String username = "John.Doe";
        User mockUser = createMockUser("John", "Doe", username, "password", true);
        Trainee mockTrainee = createMockTrainee(mockUser, new Date(), "123 Main St");

        Map<String, Boolean> statusUpdate = Map.of("active", true);

        when(gymFacade.activateTrainee(username)).thenReturn(mockTrainee);

        // Act & Assert
        mockMvc.perform(patch("/api/trainees/{username}/status", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.active").value(true));

        verify(gymFacade, times(1)).activateTrainee(username);
    }

    @Test
    public void testDeleteTrainee_Success() throws Exception {
        // Arrange
        String username = "John.Doe";
        doNothing().when(gymFacade).deleteTraineeByUsername(username);

        // Act & Assert
        mockMvc.perform(delete("/api/trainees/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Trainee deleted successfully"));

        verify(gymFacade, times(1)).deleteTraineeByUsername(username);
    }

    // Trainer Controller Tests
    @Test
    public void testRegisterTrainer_Success() throws Exception {
        // Arrange
        TrainingType mockTrainingType = createMockTrainingType(1L, "Strength");
        TrainerRegistrationRequest request = new TrainerRegistrationRequest(
                "Jane", "Smith", mockTrainingType);

        User mockUser = createMockUser("Jane", "Smith", "Jane.Smith", "password", true);
        Trainer mockTrainer = createMockTrainer(mockUser, mockTrainingType);

        when(gymFacade.selectTrainingTypeByID(anyLong())).thenReturn(Optional.of(mockTrainingType));
        when(gymFacade.addTrainer(anyString(), anyString(), anyBoolean(), any(TrainingType.class)))
                .thenReturn(mockTrainer);

        // Act & Assert
        mockMvc.perform(post("/api/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Jane.Smith"))
                .andExpect(jsonPath("$.password").value("password"));

        verify(gymFacade, times(1)).selectTrainingTypeByID(anyLong());
        verify(gymFacade, times(1)).addTrainer(eq("Jane"), eq("Smith"), eq(true), any(TrainingType.class));
    }

    @Test
    public void testGetTrainerProfile_Success() throws Exception {
        // Arrange
        String username = "Jane.Smith";
        TrainingType mockTrainingType = createMockTrainingType(1L, "Strength");
        User mockUser = createMockUser("Jane", "Smith", username, "password", true);
        Trainer mockTrainer = createMockTrainer(mockUser, mockTrainingType);

        List<Trainee> traineesList = new ArrayList<>();
        User traineeUser = createMockUser("John", "Doe", "John.Doe", "password", true);
        Trainee mockTrainee = createMockTrainee(traineeUser, new Date(), "123 Main St");
        traineesList.add(mockTrainee);

        when(gymFacade.selectTrainerByUsername(username)).thenReturn(Optional.of(mockTrainer));
        when(gymFacade.getTraineesListofTrainer(mockTrainer)).thenReturn(traineesList);

        // Act & Assert
        mockMvc.perform(get("/api/trainers/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.specialization").value("Strength"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainees[0].username").value("John.Doe"));

        verify(gymFacade, times(1)).selectTrainerByUsername(username);
        verify(gymFacade, times(1)).getTraineesListofTrainer(mockTrainer);
    }

    @Test
    public void testUpdateTrainerProfile_Success() throws Exception {
        // Arrange
        String username = "Jane.Smith";
        TrainingType mockTrainingType = createMockTrainingType(1L, "Strength");
        User mockUser = createMockUser("Jane", "Smith", username, "password", true);
        Trainer mockTrainer = createMockTrainer(mockUser, mockTrainingType);

        UpdateTrainerRequest request = new UpdateTrainerRequest(
                "Jane", "Updated", "Strength", true);

        List<Trainee> traineesList = new ArrayList<>();

        when(gymFacade.selectTrainerByUsername(username)).thenReturn(Optional.of(mockTrainer));
        when(gymFacade.updateTrainer(any(Trainer.class))).thenReturn(mockTrainer);
        when(gymFacade.getTraineesListofTrainer(mockTrainer)).thenReturn(traineesList);

        // Act & Assert
        mockMvc.perform(put("/api/trainers/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));

        verify(gymFacade, times(1)).selectTrainerByUsername(username);
        verify(gymFacade, times(1)).updateTrainer(any(Trainer.class));
        verify(gymFacade, times(1)).getTraineesListofTrainer(any(Trainer.class));
    }

    @Test
    public void testUpdateTrainerStatus_Success() throws Exception {
        // Arrange
        String username = "Jane.Smith";
        TrainingType mockTrainingType = createMockTrainingType(1L, "Strength");
        User mockUser = createMockUser("Jane", "Smith", username, "password", true);
        Trainer mockTrainer = createMockTrainer(mockUser, mockTrainingType);

        Map<String, Boolean> statusUpdate = Map.of("active", true);

        when(gymFacade.activateTrainer(username)).thenReturn(mockTrainer);

        // Act & Assert
        mockMvc.perform(patch("/api/trainers/{username}/status", username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.active").value(true));

        verify(gymFacade, times(1)).activateTrainer(username);
    }

    @Test
    public void testGetTrainerTrainingsList_TrainerNotFound() throws Exception {
        // Arrange
        String username = "NonExistent";

        when(gymFacade.selectTrainerByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/trainers/{username}/trainings", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Trainer not found"));

        verify(gymFacade, times(1)).selectTrainerByUsername(username);
        verify(gymFacade, never()).getTrainerTrainingsByCriteria(
                anyString(), any(Date.class), any(Date.class), anyString());
    }
}