package com.zura.gymCRM.dao;

import com.zura.gymCRM.entities.TrainingType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingTypeRepository
    extends JpaRepository<TrainingType, Long> {
  TrainingType findByTrainingTypeName(String trainingTypeName);

  @Override
  @Deprecated
  default void delete(TrainingType entity) {
    throw new UnsupportedOperationException("Deletion not allowed");
  }

  @Override
  @Deprecated
  default void deleteById(Long id) {
    throw new UnsupportedOperationException("Deletion not allowed");
  }

  @Override
  @Deprecated
  default<S extends TrainingType> S save(S entity) {
    throw new UnsupportedOperationException("Updates not allowed");
  }
}
