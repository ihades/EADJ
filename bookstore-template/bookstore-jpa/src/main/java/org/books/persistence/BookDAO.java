package org.books.persistence;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.books.data.entity.Book;

@Stateless
@LocalBean
public class BookDAO {

	@PersistenceContext
	private EntityManager entityManager;

	public BookDAO() {
	}

	protected BookDAO(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Book create(Book book) {
		entityManager.persist(book);
		entityManager.flush();
		return book;
	}

	public Book find(long id) {
		return entityManager.find(Book.class, id);
	}

	public Book find(String isbn) {
		try {
			Query query = entityManager.createNamedQuery("findBookByIsbn").setParameter("isbn", isbn);
			return (Book) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public Book update(Book book) {
		return entityManager.merge(book);
	}

	public void delete(Book book) {
		book = entityManager.merge(book);
		entityManager.remove(book);
	}
}
