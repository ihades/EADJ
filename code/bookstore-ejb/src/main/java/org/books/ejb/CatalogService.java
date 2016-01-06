package org.books.ejb;

import java.io.Serializable;
import org.books.ejb.exception.BookAlreadyExistsException;
import org.books.ejb.exception.BookNotFoundException;
import java.util.List;
import org.books.ejb.dto.BookDTO;
import org.books.persistence.dto.BookInfo;

public interface CatalogService extends Serializable {

    /**
     * Adds a book to the catalog.
     *
     * @param book the data of the book to be added
     * @throws BookAlreadyExistsException if a book with the same ISBN number
     * already exists
     */
    void addBook(BookDTO book) throws BookAlreadyExistsException;

    /**
     * Finds a book by ISBN number.
     *
     * @param isbn the ISBN number of the book
     * @return the data of the found book
     * @throws BookNotFoundException if no book with the specified ISBN number
     * exists
     */
    BookDTO findBook(String isbn) throws BookNotFoundException;

    /**
     * Searches for books by keywords. A book is included in the result list if
     * every keyword is contained in its title, authors or publisher field.
     *
     * @param keywords the keywords to search for
     * @return a list of matching books (may be empty)
     */
    List<BookInfo> searchBooks(String keywords);

    /**
     * Updates the data of a book.
     *
     * @param book the data of the book to be updated
     * @throws BookNotFoundException if no book with the specified ISBN number
     * exists
     */
    void updateBook(BookDTO book) throws BookNotFoundException;

}
