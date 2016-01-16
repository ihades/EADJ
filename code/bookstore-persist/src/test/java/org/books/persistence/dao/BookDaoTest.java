package org.books.persistence.dao;

import java.math.BigDecimal;
import java.util.Arrays;
import org.books.persistence.testdata.AbstractTestBase;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import org.books.persistence.dao.BookDao;
import org.books.persistence.dto.BookInfo;
import org.books.persistence.entity.Book;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

public class BookDaoTest extends AbstractTestBase {

    @Test
    public void testGetByIsbn() {
        BookDao bookDao = new BookDao(getEm());
        Book book = bookDao.findByIsbn("0321246780");
        assertNotNull(book);
        assertEquals("The Java Language Specification (3rd Edition)", book.getTitle());
    }

    @Test
    public void testGetByUnknownIsbn() {
        BookDao bookDao = new BookDao(getEm());
        assertNull(bookDao.findByIsbn("1234257890"));
    }

    @Test
    public void testGetByInvalidIsbn() {
        BookDao bookDao = new BookDao(getEm());
        assertNull(bookDao.findByIsbn("asdds1234875üé"));
    }

    @Test
    public void testGetById() {
        BookDao bookDao = new BookDao(getEm());
        Book book = bookDao.getById(9l);
        assertNotNull(book);
        assertEquals("Learning Java (3rd Edition)", book.getTitle());
    }

    @Test
    public void testNotValidSearch() {
        BookDao bookDao = new BookDao(getEm());
        List<BookInfo> result1 = searchByKeywords(bookDao, new String[]{"Programmieren", "Philip"});
        List<BookInfo> result2 = searchByKeywords(bookDao, new String[]{"Habelitz"});
        List<BookInfo> result3 = searchByKeywords(bookDao, new String[]{"Insel", "Reilly"});
        List<BookInfo> result4 = searchByKeywords(bookDao, new String[]{"Galileo", "Reilly"});
        List<BookInfo> result5 = searchByKeywords(bookDao, new String[]{"ckerma"});
        List<BookInfo> result = searchByKeywords(bookDao, new String[]{"Holmes", "Learning"});
        assertTrue("Holmes and Learning is not occuring within the same Book", result.isEmpty());
        assertTrue("Size of \"Programmieren Philip\"", result1.isEmpty());
        assertTrue("Size of \"Habelitz\"", result2.isEmpty());
        assertTrue("Size of \"Insel Reilly\"", result3.isEmpty());
        assertTrue("Size of \"Galileo Reilly\"", result4.isEmpty());
        assertTrue("Size of \"ckerma\"", result5.isEmpty());
    }

    @Test
    public void testValidSearch() {
        BookDao bookDao = new BookDao(getEm());
        List<BookInfo> result = searchByKeywords(bookDao, new String[]{"Holmes", "Java"});
        assertEquals(1, result.size());

        result = searchByKeywords(bookDao, "Pro", "W");
        assertEquals(1, result.size());

        result = searchByKeywords(bookDao, "Pro", "w");
        assertEquals(1, result.size());

        result = searchByKeywords(bookDao, "Pro", "Java");
        assertEquals(2, result.size());

        result = searchByKeywords(bookDao, "Da");
        assertEquals(4, result.size());

        result = searchByKeywords(bookDao, "Da", "NutSHell");
        assertEquals(2, result.size());
    }

    @Test
    public void updateBook() {
        BookDao bookDao = new BookDao(getEm());
        Book book = bookDao.getById(9l);
        assertNotNull(book);
        assertNotSame("Globi", book.getAuthors());
        book.setPrice(new BigDecimal(75.9));

        try {
            getEm().getTransaction().begin();
            book.setAuthors("Globi");
            bookDao.update(book);
        } catch (RollbackException e) {
            getEm().getTransaction().rollback();
            getEm().clear();
            throw e;
        } finally {
            getEm().getTransaction().commit();
            getEm().clear();
        }

        book = bookDao.getById(9l);
        assertEquals("Globi", book.getAuthors());
    }

    @Test(expected = RollbackException.class)
    public void updateBookMaxPrice() {
        BookDao bookDao = new BookDao(getEm());
        Book book = bookDao.getById(9l);
        assertNotNull(book);
        book.setPrice(new BigDecimal(75000000.9)); //to high...

        try {
            getEm().getTransaction().begin();
            book.setAuthors("Globi");
            bookDao.update(book);
        } catch (RollbackException e) {
            getEm().getTransaction().rollback();
            getEm().clear();
            throw e;
        } finally {
            getEm().getTransaction().commit();
            getEm().clear();
        }
    }

    @Ignore
    @Test(expected = IllegalStateException.class)
    public void updateInexistingBook() {
        BookDao bookDao = new BookDao(getEm());
        Book book = new Book();

        book.setAuthors("Globi");
        book.setBinding(Book.Binding.Unknown);
        book.setIsbn("1234567890");
        book.setNumberOfPages(42);
        book.setPrice(new BigDecimal(75.9));
        book.setPublicationYear(1984);
        book.setPublisher("der Verlag");
        book.setTitle("the Book");

        try {
            getEm().getTransaction().begin();
            bookDao.update(book);
            getEm().getTransaction().commit();
        } catch (RollbackException e) {
            getEm().getTransaction().rollback();
            getEm().clear();
            throw e;
        } finally {
            getEm().clear();
        }
    }

    @Test
    public void insertInexistingBook() {
        BookDao bookDao = new BookDao(getEm());
        Book book = new Book();

        book.setAuthors("Globi");
        book.setBinding(Book.Binding.Unknown);
        book.setIsbn("1234567890");
        book.setNumberOfPages(42);
        book.setPrice(new BigDecimal(75.9));
        book.setPublicationYear(1984);
        book.setPublisher("der Verlag");
        book.setTitle("the Book");

        try {
            getEm().getTransaction().begin();
            bookDao.create(book);
            getEm().getTransaction().commit();
        } catch (RollbackException e) {
            getEm().getTransaction().rollback();
            getEm().clear();
            throw e;
        } finally {
            getEm().clear();
        }

    }

    public List<BookInfo> searchByKeywords(BookDao bookDao, String... keywords) {
        return bookDao.searchByKeywords(Arrays.asList(keywords));
    }

}
