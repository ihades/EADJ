package org.books.data.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {

	public enum Status {

		accepted, processing, shipped, canceled
	}

	private String number;
	private Date date;
	private BigDecimal amount;
	private Status status;
	private Customer customer;
	private Address address;
	private CreditCard creditCard;
	private List<OrderItem> items;

	public Order() {
	}

	public Order(String number, Date date, BigDecimal amount, Status status,
			Customer customer, Address address, CreditCard creditCard, List<OrderItem> items) {
		this.number = number;
		this.date = date;
		this.amount = amount;
		this.status = status;
		this.customer = customer;
		this.address = address;
		this.creditCard = creditCard;
		this.items = items;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard card) {
		this.creditCard = card;
	}

	public List<OrderItem> getItems() {
		if (items == null) {
			items = new ArrayList<>();
		}
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}
}
