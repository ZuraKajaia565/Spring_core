package com.zura.gymCRM.dao;

import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Trainer;
import com.zura.gymCRM.entities.Training;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
  Optional<Trainer> findById(Long id);

  Optional<Trainer> findByUser_Username(String username);

  @Query("SELECT t FROM Trainer t "
         + "LEFT JOIN t.trainees tr WITH tr.id = :traineeId "
         + "WHERE tr IS NULL")
  List<Trainer>
  findTrainersNotAssignedToTrainee(Long traineeId);

  @Query("SELECT t FROM Training t "
         + "WHERE t.trainer.user.username = :username "
         + "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) "
         + "AND (:toDate IS NULL OR t.trainingDate <= :toDate) "
         + "AND (:traineeName IS NULL OR t.trainee.user.username LIKE "
         + "%:traineeName%)")
  List<Training>
  findTrainingsByCriteria(@Param("username") String username,
                          @Param("fromDate") java.util.Date fromDate,
                          @Param("toDate") java.util.Date toDate,
                          @Param("traineeName") String traineeName);

  @Query("SELECT t FROM Trainee t JOIN t.trainers tr WHERE tr.id = :id")
  List<Trainee> findTrainees(Long id);
}
