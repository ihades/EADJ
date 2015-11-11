/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;
import org.books.application.Bookstore;
import org.books.application.BookstoreException;
import org.books.data.dto.BookInfo;
import org.books.data.entity.Book;
import org.books.presentation.util.MessageFactory;

/**
 *
 * @author cb
 */
@Named(value = "catalogBean")
@SessionScoped
public class CatalogBean implements Serializable {

    private static final String BOOK_DETAILS = "bookDetails";

    @Inject
    private Bookstore bookstore;

    private Book selectedBook;
    private BookInfo selectedBookInfo;
    private final List<BookInfo> books = new ArrayList<>();
    private String keywords = "";

    public void setKeywords(String book) {
        this.keywords = book;
    }

    public String getKeywords() {
        return keywords;
    }

    public List<BookInfo> getBooks() {
        return books;
    }

    public BookInfo getSelectedBookInfo() {
        return selectedBookInfo;
    }

    public Book getSelectedBook() {
        return selectedBook;
    }

    public String selectBook(BookInfo selectedBook) {
        try {
            this.selectedBook = bookstore.findBook(selectedBook.getIsbn());
            this.selectedBookInfo = selectedBook;
            return BOOK_DETAILS;
        } catch (BookstoreException ex) {
            MessageFactory.error(ex, selectedBook.getIsbn());
        }
        return null;
    }

    public String findBook() {
        books.clear();
        if (!keywords.isEmpty()) {
            books.addAll(bookstore.searchBooks(keywords));
        }
        if (books.isEmpty()) {
            MessageFactory.error("noBokFound", keywords);
        }
        return null;
    }
    public void autoComplete(AjaxBehaviorEvent abe) {
        
        findBook();
    }

}
