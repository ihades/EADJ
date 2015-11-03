package org.books.data.dto;

import org.books.data.entity.Customer;

public class CustomerInfo {

	private String email;
	private String firstName;
	private String lastName;

	public CustomerInfo() {
	}

	public CustomerInfo(String firstName, String lastName, String email) {
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public CustomerInfo(Customer customer) {
		this.email = customer.getEmail();
		this.firstName = customer.getFirstName();
		this.lastName = customer.getLastName();
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
