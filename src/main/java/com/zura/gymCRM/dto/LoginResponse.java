package com.zura.gymCRM.dto;

public class LoginResponse {

  private String role;
  private String message;


  public LoginResponse(String role, String message) {
    this.role = role;
    this.message = message;
  }

  public String getRole() { return role; }

  public void setRole(String role) { this.role = role; }

  public String getMessage() { return message; }

  public void setMessage(String message) { this.message = message; }
}
