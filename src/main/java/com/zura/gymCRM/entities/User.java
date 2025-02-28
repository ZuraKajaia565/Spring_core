package com.zura.gymCRM.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "user")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(nullable = false, name = "user_firstname") private String firstName;

  @Column(nullable = false, name = "user_lastname") private String lastName;

  @Column(nullable = false, unique = true, name = "user_username")
  private String username;

  @Column(nullable = false, name = "user_password")
  @Size(min = 10, message = "Password must be at least 10 characters long")
  private String password;

  @Column(nullable = false, name = "user_isactive") private Boolean isActive;

  public User() {}

  // Parameterized constructor
  public User(String firstName, String lastName, String username,
              String password, Boolean isActive) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;
    this.password = password;
    this.isActive = isActive;
  }

  public Long getId() { return id; }

  public void setId(Long id) { this.id = id; }

  public String getFirstName() { return firstName; }

  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }

  public void setLastName(String lastName) { this.lastName = lastName; }

  public String getUsername() { return username; }

  public void setUsername(String username) { this.username = username; }

  public String getPassword() { return password; }

  public void setPassword(String password) { this.password = password; }

  public Boolean getIsActive() { return isActive; }

  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
