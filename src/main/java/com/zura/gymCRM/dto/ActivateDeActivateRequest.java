package com.zura.gymCRM.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ActivateDeActivateRequest {
  @NotBlank(message = "username is required") private String username;
  @NotNull(message = "active status is required")
  private Boolean isActive;

  public ActivateDeActivateRequest(String username, Boolean isActive) {
    this.username = username;
    this.isActive = isActive;
  }

  public ActivateDeActivateRequest() {

  }


  public String getUsername() { return username; }

  public void setUsername(String username) { this.username = username; }

  public Boolean getIsActive() { return isActive; }

  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
