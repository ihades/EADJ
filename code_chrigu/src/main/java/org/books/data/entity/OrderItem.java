package org.books.data.entity;

import java.math.BigDecimal;

public class OrderItem {

	private Book book;
	private BigDecimal price;
	private Integer quantity;

	public OrderItem() {
	}

	public OrderItem(Book book, BigDecimal price, Integer quantity) {
		this.book = book;
		this.price = price;
		this.quantity = quantity;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
