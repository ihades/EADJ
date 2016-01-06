package org.books.ejb.impl;

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
    BookDao bookDao;

    @Override
    public void addBook(BookDTO bookDTO) throws BookAlreadyExistsException {

        if (bookDao.getByIsbn(bookDTO.getIsbn()) != null) {
            throw new BookAlreadyExistsException();
        }
        ModelMapper modelMapper = new ModelMapper();
        Book book = modelMapper.map(bookDTO, Book.class);
        bookDao.create(book);
    }

    @Override
    public BookDTO findBook(String isbn) throws BookNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<BookInfo> searchBooks(String keywords) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBook(BookDTO book) throws BookNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
