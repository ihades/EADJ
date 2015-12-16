package org.books.application;

import javax.ejb.Remote;
import org.books.application.exception.BookAlreadyExistsException;
import org.books.application.exception.BookNotFoundException;
import org.books.data.entity.Book;

@Remote
public interface CatalogService {

	public void addBook(Book book) throws BookAlreadyExistsException;

	public Book findBook(String isbn) throws BookNotFoundException;
}
