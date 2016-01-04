package org.books.ejb.dto;

public class AddressDTO {

	private String street;
	private String city;
	private String postalCode;
	private String country;

	public AddressDTO() {
	}

	public AddressDTO(String street, String city, String postalCode, String country) {
		this.street = street;
		this.city = city;
		this.postalCode = postalCode;
		this.country = country;
	}

	public AddressDTO(AddressDTO other) {
		this.street = other.street;
		this.city = other.city;
		this.postalCode = other.postalCode;
		this.country = other.country;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}
