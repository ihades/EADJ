package org.books.persistence;

import java.math.BigDecimal;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.books.data.entity.Book;
import org.books.data.entity.Book.Binding;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

public class BookDAOTest {

	private static final String PERSISTENCE_UNIT = "test";

	private static EntityManagerFactory entityManagerFactory;
	private static EntityManager entityManager;
	private static EntityTransaction transaction;
	private static BookDAO bookDAO;

	private Book book = new Book("143024626X", "Beginning Java EE 7", "Antonio Goncalves", "Apress", 2013, Binding.Paperback, 608, new BigDecimal("49.99"));

	@BeforeClass
	public void createDAO() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
		entityManager = entityManagerFactory.createEntityManager();
		transaction = entityManager.getTransaction();
		bookDAO = new BookDAO(entityManager);
	}

	@Test
	public void createBook() {
		transaction.begin();
		book = bookDAO.create(book);
		transaction.commit();
		assertNotNull(book.getId());
	}

	@Test(dependsOnMethods = "createBook")
	public void findBook() {
		entityManager.clear();
		assertNotNull(bookDAO.find(book.getId()));
		entityManager.clear();
		assertNotNull(bookDAO.find(book.getIsbn()));
	}

	@Test(dependsOnMethods = "createBook")
	public void updateBook() {
		book.setPrice(new BigDecimal("39.99"));
		transaction.begin();
		bookDAO.update(book);
		transaction.commit();
		entityManager.clear();
		assertEquals(bookDAO.find(book.getId()).getPrice(), book.getPrice());
	}

	@Test(dependsOnMethods = "updateBook")
	public void deleteBook() {
		transaction.begin();
		bookDAO.delete(book);
		transaction.commit();
		entityManager.clear();
		assertNull(bookDAO.find(book.getId()));
		assertNull(bookDAO.find(book.getIsbn()));
	}
}
