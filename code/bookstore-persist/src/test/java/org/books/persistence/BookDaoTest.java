package org.books.persistence;

import java.util.Arrays;
import org.books.persistence.testdata.AbstractTestBase;
import java.util.List;
import org.books.persistence.dao.BookDao;
import org.books.persistence.entity.Book;
import static org.junit.Assert.*;
import org.junit.Test;

public class BookDaoTest extends AbstractTestBase {

    @Test
    public void testGetByIsbn() {
        BookDao bookDao = new BookDao(getEm());
        Book book = bookDao.getByIsbn("0321246780");
        assertNotNull(book);
        assertEquals("The Java Language Specification (3rd Edition)", book.getTitle());
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
        List<Book> result1 = searchByKeywords(bookDao, new String[]{"Programmieren", "Philip"});
        List<Book> result2 = searchByKeywords(bookDao, new String[]{"Habelitz"});
        List<Book> result3 = searchByKeywords(bookDao, new String[]{"Insel", "Reilly"});
        List<Book> result4 = searchByKeywords(bookDao, new String[]{"Galileo", "Reilly"});
        List<Book> result5 = searchByKeywords(bookDao, new String[]{"ckerma"});
        List<Book> result = searchByKeywords(bookDao, new String[]{"Holmes", "Learning"});
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
        List<Book> result = searchByKeywords(bookDao, new String[]{"Holmes", "Java"});
        assertEquals(1, result.size());

        result = searchByKeywords(bookDao, new String[]{"Pro", "W"});
        assertEquals(1, result.size());

        result = searchByKeywords(bookDao, new String[]{"Pro", "w"});
        assertEquals(1, result.size());

        result = searchByKeywords(bookDao, new String[]{"Pro", "Java"});
        assertEquals(2, result.size());

        result = searchByKeywords(bookDao, new String[]{"Da"});
        assertEquals(4, result.size());

        result = searchByKeywords(bookDao, new String[]{"Da", "NutSHell"});
        assertEquals(2, result.size());
    }

    public List<Book> searchByKeywords(BookDao bookDao, String[] keywords) {
        return bookDao.searchByKeywords(Arrays.asList(keywords));
    }

}
