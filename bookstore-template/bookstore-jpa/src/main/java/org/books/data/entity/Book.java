package org.books.data.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "findBookByIsbn", query = "SELECT b FROM Book b WHERE b.isbn = :isbn")
public class Book implements Serializable {

	public enum Binding {

		Hardcover, Paperback
	}

	@Id
	@GeneratedValue
	private Long id;
	private String isbn;
	private String title;
	private String authors;
	private String publisher;
	private Integer publicationYear;
	private Binding binding;
	private Integer numberOfPages;
	private BigDecimal price;

	public Book() {
	}

	public Book(String isbn, String title, String authors, String publisher,
			Integer publicationYear, Binding binding, Integer numberOfPages, BigDecimal price) {
		this.isbn = isbn;
		this.title = title;
		this.authors = authors;
		this.publisher = publisher;
		this.publicationYear = publicationYear;
		this.binding = binding;
		this.numberOfPages = numberOfPages;
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Integer getPublicationYear() {
		return publicationYear;
	}

	public void setPublicationYear(Integer publicationYear) {
		this.publicationYear = publicationYear;
	}

	public Binding getBinding() {
		return binding;
	}

	public void setBinding(Binding binding) {
		this.binding = binding;
	}

	public Integer getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
}
