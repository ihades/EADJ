package org.books.ejb.impl;

import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.books.ejb.CatalogServiceLocal;
import org.books.ejb.CatalogServiceRemote;
import org.books.ejb.dto.BookDTO;
import org.books.ejb.exception.BookAlreadyExistsException;
import org.books.ejb.exception.BookNotFoundException;
import org.books.persistence.dao.BookDao;
import org.books.persistence.dto.BookInfo;
import org.books.persistence.entity.Book;
import org.modelmapper.ModelMapper;

/**
 *
 * @author cb
 */
@Stateless(name = "CatalogService")
public class CatalogSeviceBean implements CatalogServiceRemote, CatalogServiceLocal {

    @EJB
    private BookDao bookDao;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public BookDTO addBook(BookDTO bookDTO) throws BookAlreadyExistsException {
        if (bookDao.findByIsbn(bookDTO.getIsbn()) != null) {
            throw new BookAlreadyExistsException();
        }
        Book book = modelMapper.map(bookDTO, Book.class);
        bookDao.create(book);
        return bookDTO;
    }

    @Override
    public BookDTO findBook(String isbn) throws BookNotFoundException {
        Book book = bookDao.findByIsbn(isbn);
        if (book == null) {
            throw new BookNotFoundException();
        }
        return modelMapper.map(book, BookDTO.class);
    }

    @Override
    public List<BookInfo> searchBooks(String keywords) {
        return bookDao.searchByKeywords(Arrays.asList(keywords.split(" ")));
    }

    @Override
    public void updateBook(BookDTO bookDTO) throws BookNotFoundException {
        Book book = bookDao.findByIsbn(bookDTO.getIsbn());
        if (book == null) {
            throw new BookNotFoundException();
        }
        modelMapper.map(bookDTO, book);
        bookDao.update(book);
    }

}
