package org.books.ejb.impl;

import java.util.List;
import javax.ejb.Stateless;
import org.books.ejb.CatalogService;
import org.books.ejb.dto.BookDTO;
import org.books.ejb.exception.BookAlreadyExistsException;
import org.books.ejb.exception.BookNotFoundException;
import org.books.persistence.dto.BookInfo;

/**
 *
 * @author cb
 */
@Stateless(name = "CatalogService")
public class CatalogSeviceBean implements CatalogService {

    @Override
    public void addBook(BookDTO book) throws BookAlreadyExistsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
