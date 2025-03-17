package com.zura.gymCRM.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "trainingtype")
public class TrainingType {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "trainingtype_id")
  private Long id;

  @Column(nullable = false, unique = true, name = "trainingTypeName")
  private String trainingTypeName;

  public TrainingType() {}

  public TrainingType(String trainingTypeName) {
    this.trainingTypeName = trainingTypeName;
  }

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public String getTrainingTypeName() { return trainingTypeName; }

  public void setTrainingTypeName(String trainingTypeName) {
    this.trainingTypeName = trainingTypeName;
  }
}
