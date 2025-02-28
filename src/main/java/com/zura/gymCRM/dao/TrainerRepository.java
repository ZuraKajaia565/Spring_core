package com.zura.gymCRM.dao;

import com.zura.gymCRM.entities.Trainer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
  Optional<Trainer> findById(Long id);

  Optional<Trainer> findByUser_Username(String username);

  @Query("SELECT t FROM Trainer t WHERE t.id NOT IN "
         + "(SELECT tt.id FROM Trainee tr JOIN tr.trainers tt WHERE tr.id = "
         + ":traineeId)")
  List<Trainer>
  findTrainersNotAssignedToTrainee(Long traineeId);
}
