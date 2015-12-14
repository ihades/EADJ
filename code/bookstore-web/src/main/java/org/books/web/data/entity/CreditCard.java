package org.books.web.data.entity;

public class CreditCard {

	public enum Type {

		MasterCard, Visa
	}
	private Type type;
	private String number;
	private Integer expirationMonth;
	private Integer expirationYear;

	public CreditCard() {
	}

	public CreditCard(Type type, String number, Integer expirationMonth, Integer expirationYear) {
		this.type = type;
		this.number = number;
		this.expirationMonth = expirationMonth;
		this.expirationYear = expirationYear;
	}

	public CreditCard(CreditCard other) {
		this.type = other.type;
		this.number = other.number;
		this.expirationMonth = other.expirationMonth;
		this.expirationYear = other.expirationYear;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Integer getExpirationMonth() {
		return expirationMonth;
	}

	public void setExpirationMonth(Integer expirationMonth) {
		this.expirationMonth = expirationMonth;
	}

	public Integer getExpirationYear() {
		return expirationYear;
	}

	public void setExpirationYear(Integer expirationYear) {
		this.expirationYear = expirationYear;
	}
}
