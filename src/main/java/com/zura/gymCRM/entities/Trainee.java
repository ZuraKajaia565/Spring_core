package com.zura.gymCRM.entities;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "trainee")
public class Trainee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "trainee_id")
  private Long id;

  @OneToOne(cascade = {CascadeType.ALL})
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(nullable = true, name = "trainee_dateofbirth")
  private Date dateOfBirth;

  @Column(nullable = true, name = "trainee_address") private String address;

  @ManyToMany
  @JoinTable(name = "trainee2trainer",
             joinColumns = @JoinColumn(name = "trainee_id"),
             inverseJoinColumns = @JoinColumn(name = "trainer_id"))
  private List<Trainer> trainers;

  public Trainee() {}

  public Trainee(User user, Date dateOfBirth, String address,
                 List<Trainer> trainers) {
    this.user = user;
    this.dateOfBirth = dateOfBirth;
    this.address = address;
    this.trainers = trainers;
  }

  // Getters and Setters
  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public User getUser() { return user; }

  public void setUser(User user) { this.user = user; }

  public Date getDateOfBirth() { return dateOfBirth; }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public String getAddress() { return address; }

  public void setAddress(String address) { this.address = address; }

  public List<Trainer> getTrainers() { return trainers; }

  public void setTrainers(List<Trainer> trainers) { this.trainers = trainers; }
}
