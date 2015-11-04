package org.books.data.dto;

import java.math.BigDecimal;
import java.util.Date;
import org.books.data.entity.Order;
import org.books.data.entity.Order.Status;

public class OrderInfo {

	private String number;
	private Date date;
	private BigDecimal amount;
	private Status status;

	public OrderInfo() {
	}

	public OrderInfo(String number, Date date, BigDecimal amount, Status status) {
		this.number = number;
		this.date = date;
		this.amount = amount;
		this.status = status;
	}

	public OrderInfo(Order order) {
		this.number = order.getNumber();
		this.date = order.getDate();
		this.amount = order.getAmount();
		this.status = order.getStatus();
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
}
