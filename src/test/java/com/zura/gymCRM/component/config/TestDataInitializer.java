package com.zura.gymCRM.component.config;

import com.zura.gymCRM.dao.TrainingTypeRepository;
import com.zura.gymCRM.entities.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Initializes test data for component tests using ContextRefreshedEvent
 * instead of @PostConstruct to ensure transactions work correctly
 */
@Component
@Profile("test")
public class TestDataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(TestDataInitializer.class);

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private boolean alreadyInitialized = false;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Prevent duplicate initialization
        if (alreadyInitialized) {
            return;
        }

        logger.info("Initializing test data...");

        // Check if training types exist, and if not, ensure they do using transactions
        ensureTrainingTypes();

        alreadyInitialized = true;
        logger.info("Test data initialization complete");
    }

    /**
     * Ensure all required training types exist
     */
    private void ensureTrainingTypes() {
        // Check and create training types using JPQL instead of native SQL
        ensureTrainingTypeExists("Strength", 1L);
        ensureTrainingTypeExists("Cardio", 2L);
        ensureTrainingTypeExists("Yoga", 3L);
        ensureTrainingTypeExists("Flexibility", 4L);
    }

    /**
     * Check if a training type exists, and if not, insert it
     */
    private void ensureTrainingTypeExists(String name, Long id) {
        if (trainingTypeRepository.findByTrainingTypeName(name).isEmpty()) {
            logger.info("Creating training type: {}", name);

            // Use JPQL update instead of native SQL
            entityManager.createNativeQuery("INSERT INTO trainingtype (trainingtype_id, training_type_name) VALUES (?1, ?2)")
                    .setParameter(1, id)
                    .setParameter(2, name)
                    .executeUpdate();

            logger.info("Created training type: {}", name);
        } else {
            logger.info("Training type already exists: {}", name);
        }
    }
}