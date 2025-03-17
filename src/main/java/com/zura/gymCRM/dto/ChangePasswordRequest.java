package com.zura.gymCRM.dto;

import jakarta.validation.constraints.NotBlank;

public class ChangePasswordRequest {

  @NotBlank(message = "Username is required") private String username;

  @NotBlank(message = "Old password is required") private String oldPassword;

  @NotBlank(message = "New password is required") private String newPassword;


  public ChangePasswordRequest(String username, String oldPassword,
                               String newPassword) {
    this.username = username;
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
  }

  public String getUsername() { return username; }

  public void setUsername(String username) { this.username = username; }

  public String getOldPassword() { return oldPassword; }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public String getNewPassword() { return newPassword; }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
