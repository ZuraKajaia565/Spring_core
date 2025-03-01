package com.zura.gymCRM.dao;

import com.zura.gymCRM.entities.Trainee;
import com.zura.gymCRM.entities.Training;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {
  Optional<Trainee> findByUser_Username(String username);

  void deleteByUser_Username(String username);

  @Query("SELECT t FROM Training t "
         + "WHERE t.trainee.user.username = :username "
         + "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) "
         + "AND (:toDate IS NULL OR t.trainingDate <= :toDate) "
         + "AND (:trainerName IS NULL OR t.trainer.user.username LIKE "
         + "%:trainerName%) "
         + "AND (:trainingType IS NULL OR t.trainingType.trainingTypeName "
         + "LIKE %:trainingType%)")
  List<Training>
  findTrainingsByCriteria(@Param("username") String username,
                          @Param("fromDate") java.util.Date fromDate,
                          @Param("toDate") java.util.Date toDate,
                          @Param("trainerName") String trainerName,
                          @Param("trainingType") String trainingType);
}
