package com.zura.gymCRM.model;

public abstract class User {
  private String firstName;
  private String lastName;
  private String userName;
  private String password;
  private boolean isActive;

  public User(String firstName, String lastName, String username,
              String password, boolean isActive) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.userName = username;
    this.password = password;
    this.isActive = isActive;
  }

  public String getFirstName() { return firstName; }

  public void setFirstName(String firstName) { this.firstName = firstName; }

  public String getLastName() { return lastName; }

  public void setLastName(String lastName) { this.lastName = lastName; }

  public String getUserName() { return userName; }

  public void setUserName(String userName) { this.userName = userName; }

  public String getPassword() { return password; }

  public void setPassword(String password) { this.password = password; }

  public boolean isActive() { return isActive; }

  public void setActive(boolean isActive) { this.isActive = isActive; }
}
