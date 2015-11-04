package org.books.data.dto;

import java.math.BigDecimal;
import org.books.data.entity.Book;


public class BookInfo {

	private String isbn;
	private String title;
	private BigDecimal price;

	public BookInfo() {
	}

	public BookInfo(String isbn, String title, BigDecimal price) {
		this.isbn = isbn;
		this.title = title;
		this.price = price;
	}

	public BookInfo(Book book) {
		this.isbn = book.getIsbn();
		this.title = book.getTitle();
		this.price = book.getPrice();
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
