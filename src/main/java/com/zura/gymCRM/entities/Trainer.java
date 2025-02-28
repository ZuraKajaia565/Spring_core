package com.zura.gymCRM.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "trainer")
public class Trainer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "trainer_id")
  private Long id;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @ManyToOne
  @JoinColumn(name = "trainingtype_id", nullable = false)
  private TrainingType specialization;

  @ManyToMany(mappedBy = "trainers") private List<Trainee> trainees;

  public Trainer() {}

  // Parameterized constructor
  public Trainer(User user, TrainingType specialization,
                 List<Trainee> trainees) {
    this.user = user;
    this.specialization = specialization;
    this.trainees = trainees;
  }

  // Getters and Setters
  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public User getUser() { return user; }

  public void setUser(User user) { this.user = user; }

  public TrainingType getSpecialization() { return specialization; }

  public void setSpecialization(TrainingType specialization) {
    this.specialization = specialization;
  }

  public List<Trainee> getTrainees() { return trainees; }

  public void setTrainees(List<Trainee> trainees) { this.trainees = trainees; }
}
