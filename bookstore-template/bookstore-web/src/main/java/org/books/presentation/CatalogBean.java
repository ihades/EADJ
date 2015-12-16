package org.books.presentation;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.books.application.CatalogService;
import org.books.application.exception.BookNotFoundException;
import org.books.data.entity.Book;

@Named
@SessionScoped
public class CatalogBean implements Serializable {

	@EJB
	private CatalogService catalogService;
	private String isbn;
	private Book book;

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Book getBook() {
		return book;
	}

	public void findBook() {
		book = null;
		try {
			book = catalogService.findBook(isbn);
		} catch (BookNotFoundException ex) {
			MessageFactory.info("org.books.Bookstore.BOOK_NOT_FOUND");
		}
	}
}
