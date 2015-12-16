package org.books.application;

import java.math.BigDecimal;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.books.application.exception.BookAlreadyExistsException;
import org.books.application.exception.BookNotFoundException;
import org.books.data.entity.Book;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

public class CatalogServiceIT {

	private static final String CATALOG_SERVICE_NAME = "java:global/bookstore-app/bookstore-ejb/CatalogService";
	private static CatalogService catalogService;

	private Book book = new org.books.data.entity.Book("143024626X", "Beginning Java EE 7", "Antonio Goncalves", "Apress", 2013, org.books.data.entity.Book.Binding.Paperback, 608, new BigDecimal("49.99"));

	@BeforeClass
	public void lookupService() throws Exception {
		Context jndiContext = new InitialContext();
		catalogService = (CatalogService) jndiContext.lookup(CATALOG_SERVICE_NAME);
	}

	@Test
	public void addBook() throws BookAlreadyExistsException {
		catalogService.addBook(book);
	}

	@Test(dependsOnMethods = "addBook")
	public void findBook() throws BookNotFoundException {
		assertNotNull(catalogService.findBook(book.getIsbn()));
	}
}
