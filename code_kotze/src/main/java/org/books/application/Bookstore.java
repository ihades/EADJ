package org.books.application;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.books.data.Book;

@ApplicationScoped
public class Bookstore {

	private static final String CATALOG_DATA = "/data/catalog.xml";
	private static final Logger logger = Logger.getLogger(Bookstore.class.getName());
	private final List<Book> books = new ArrayList<>();

	@PostConstruct
	public void init() {
		for (Book book : XmlParser.parse(CATALOG_DATA, Book.class)) {
			books.add(book);
		}
	}

	public Book findBook(String isbn) throws BookNotFoundException {
		logger.log(Level.INFO, "Finding book with isbn ''{0}''", isbn);
		for (Book book : books) {
			if (book.getIsbn().equals(isbn)) {
				return book;
			}
		}
		throw new BookNotFoundException();
	}

	public List<Book> searchBooks(String keywords) {
		logger.log(Level.INFO, "Searching books with keywords ''{0}''", keywords);
		keywords = keywords.toLowerCase();
		List<Book> results = new ArrayList<>();
		loop:
		for (Book book : books) {
			for (String keyword : keywords.split("\\s+")) {
				if (!book.getTitle().toLowerCase().contains(keyword)
						&& !book.getAuthors().toLowerCase().contains(keyword)
						&& !book.getPublisher().toLowerCase().contains(keyword)) {
					continue loop;
				}
			}
			results.add(book);
		}
		return results;
	}
}
