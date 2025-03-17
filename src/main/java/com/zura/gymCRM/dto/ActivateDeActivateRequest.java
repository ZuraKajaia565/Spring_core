package com.zura.gymCRM.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ActivateDeActivateRequest {
  @NotNull(message = "active status is required")
  private Boolean isActive;

  public ActivateDeActivateRequest(Boolean isActive) {
    this.isActive = isActive;
  }

  public ActivateDeActivateRequest() {

  }
  

  public Boolean getIsActive() { return isActive; }

  public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
