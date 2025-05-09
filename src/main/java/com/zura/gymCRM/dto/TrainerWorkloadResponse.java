package com.zura.gymCRM.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Response object for trainer workload summary
 */
public class TrainerWorkloadResponse {
    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private List<YearSummaryDto> years = new ArrayList<>();

    // No-argument constructor
    public TrainerWorkloadResponse() {}

    // Constructor with parameters
    public TrainerWorkloadResponse(String username, String firstName, String lastName, boolean isActive) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<YearSummaryDto> getYears() {
        return years;
    }

    public void setYears(List<YearSummaryDto> years) {
        this.years = years;
    }

    // Inner class YearSummaryDto
    public static class YearSummaryDto {
        private int year;
        private List<MonthSummaryDto> months = new ArrayList<>();

        // No-argument constructor
        public YearSummaryDto() {}

        // Constructor with parameters
        public YearSummaryDto(int year) {
            this.year = year;
        }

        // Getters and Setters
        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public List<MonthSummaryDto> getMonths() {
            return months;
        }

        public void setMonths(List<MonthSummaryDto> months) {
            this.months = months;
        }
    }

    // Inner class MonthSummaryDto
    public static class MonthSummaryDto {
        private int month;
        private int summaryDuration;

        // No-argument constructor
        public MonthSummaryDto() {}

        // Constructor with parameters
        public MonthSummaryDto(int month, int summaryDuration) {
            this.month = month;
            this.summaryDuration = summaryDuration;
        }

        // Getters and Setters
        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getSummaryDuration() {
            return summaryDuration;
        }

        public void setSummaryDuration(int summaryDuration) {
            this.summaryDuration = summaryDuration;
        }
    }
}