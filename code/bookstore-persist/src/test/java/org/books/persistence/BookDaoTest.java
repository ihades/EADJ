package org.books.persistence;

import org.books.persistence.testdata.AbstractTestBase;
import java.util.List;
import org.books.persistence.dao.BookDao;
import org.books.persistence.dto.BookInfo;
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
    public void testSearchByKeywords() {

        BookDao bookDao = new BookDao(getEm());
        List<Book> result1 = bookDao.searchByKeywords(new String[]{"Programmieren", "Philip"});
        List<Book> result2 = bookDao.searchByKeywords(new String[]{"Habelitz"});
        List<Book> result3 = bookDao.searchByKeywords(new String[]{"Insel", "Reilly"});
        List<Book> result4 = bookDao.searchByKeywords(new String[]{"Galileo", "Reilly"});
        List<Book> result5 = bookDao.searchByKeywords(new String[]{"ckerma"});
        List<Book> result6 = bookDao.searchByKeywords(new String[]{"rungen", "anse"});

        assertTrue(result1.size() > 0);
        assertTrue(result2.size() > 0);
        assertTrue(result3.size() > 0);
        assertTrue(result4.size() > 0);
        assertTrue(result5.size() > 0);
        assertTrue(result6.size() > 0);

        assertEquals("Programmieren mit Java", result1.get(0).getTitle());
        assertEquals("Java für Dummies", result2.get(0).getTitle());
        assertEquals("Java Insel", result3.get(0).getTitle());

        assertTrue(result4.size() > 1);
        assertTrue(checkContainsBook("Java 8 - Die Neuerungen", result4));
        assertTrue(checkContainsBook("Java Insel", result4));

        assertEquals("Programmieren mit Java", result5.get(0).getTitle());

        assertTrue(result6.size() > 1);
        assertTrue(checkContainsBook("Java für Dummies", result6));
        assertTrue(checkContainsBook("Java 8 - Die Neuerungen", result6));
    }

    @Test
    public void testSearchConcatMatch() {
        BookDao bookDao = new BookDao(getEm());
        List<Book> result = bookDao.searchByKeywords(new String[]{"Holmes", "Learning"});
        assertTrue(result.isEmpty());

        result = bookDao.searchByKeywords(new String[]{"Holmes", "Java"});
        assertEquals(1, result.size());
    }

    @Test
    public void testLatest3Books() {
        BookDao bookDao = new BookDao(getEm());
        List<BookInfo> latestBooks = bookDao.findLatestBooks(3);

        assertNotNull(latestBooks);
        assertEquals(3, latestBooks.size());

        Integer prevPubYear = 9999;
        for (BookInfo latestBook : latestBooks) {
            final Integer pubYear = getEm().find(Book.class, latestBook.getId()).getPublicationYear();
            assertTrue(getEm().find(Book.class, latestBook.getId()).getPublicationYear() <= prevPubYear);
            prevPubYear = pubYear;
        }
    }

    private boolean checkContainsBook(String title, List<Book> bookList) {

        boolean ret = false;

        for (Book book : bookList) {
            if (title.equals(book.getTitle())) {
                ret = true;
                break;
            }
        }

        return ret;
    }
}
