package com.zura.gymCRM.model;

import java.time.LocalDate;

public class Trainee extends User {
	private LocalDate dateOfBirth;
	private String address;
	private int userId;
	private Training training;

	public Trainee(int userId, String firstName, String lastName, String username,
			String password, Boolean isActive, LocalDate dateOfBirth,
			String address, Training training) {
		super(firstName, lastName, username, password, isActive);
		this.dateOfBirth = dateOfBirth;
		this.address = address;
		this.userId = userId;
		this.training = training;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Training getTraining() {
		return training;
	}

	public void setTraining(Training training) {
		this.training = training;
	}

	public String getFirstName() {
		return super.getFirstName();
	}

	public void setFirstName(String firstName) {
		super.setFirstName(firstName);
	}

	public String getLastName() {
		return super.getLastName();
	}

	public void setLastName(String lastName) {
		super.setLastName(lastName);
	}

	public String getUsername() {
		return super.getUserName();
	}

	public void setUsername(String username) {
		super.setUserName(username);
	}

	public String getPassword() {
		return super.getPassword();
	}

	public void setPassword(String password) {
		super.setPassword(password);
	}

	public boolean isActive() {
		return super.isActive();
	}

	public void setActive(boolean isActive) {
		super.setActive(isActive);
	}
}
