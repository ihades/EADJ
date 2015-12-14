package org.books.web.data.dto;

public class OrderItemDTO {

	private BookInfo book;
	private Integer quantity;

	public OrderItemDTO() {
	}

	public OrderItemDTO(BookInfo book, Integer quantity) {
		this.book = book;
		this.quantity = quantity;
	}

	public BookInfo getBook() {
		return book;
	}

	public void setBook(BookInfo book) {
		this.book = book;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}
