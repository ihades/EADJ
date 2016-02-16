package org.books.ejb;

import java.util.List;
import org.books.ejb.exception.BookNotFoundException;
import org.books.persistence.entity.Book;

public interface AmazonCatalogService {

    /**
     * Finds a book by ISBN number.
     *
     * @param isbn the ISBN number of the book
     * @return the data of the found book
     * @throws BookNotFoundException if no book with the specified ISBN number
     * exists
     */
    Book findByIsbn(String isbn) throws BookNotFoundException;

    /**
     * Searches for books by keywords. A book is included in the results list if
     * all keywords are contained in its title, authors or publisher field. If
     * the given parameter is null or empty an IllegalArgumentException will be
     * thrown.
     *
     * @param keywords The keywords (white-space-separated).
     * @return All Books that match all keywords.
     */
    public List<Book> searchBooks(String keywords) throws IllegalArgumentException;
}
