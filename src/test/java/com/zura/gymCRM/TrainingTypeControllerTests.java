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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class TrainingTypeControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GymFacade gymFacade;

    @Test
    @Order(1)
    void testGetTrainingTypes() throws Exception {
        mockMvc.perform(get("/trainingTypes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingTypeId").value("2"))
                .andExpect(jsonPath("$[0].trainingTypeName").value("Cardio"))
                .andExpect(jsonPath("$[1].trainingTypeId").value("1"))
                .andExpect(jsonPath("$[1].trainingTypeName").value("Strength"))
        ;
    }



}
