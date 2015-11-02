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
import javax.inject.Inject;
import org.books.application.BookNotFoundException;
import org.books.application.Bookstore;
import org.books.data.Book;

/**
 *
 * @author cb
 */
@Named(value = "catalogBean")
@SessionScoped
public class CatalogBean implements Serializable {

    private static final String NOT_FOUND_EXCEPTION = "No Books found matching your criteria!";
    private static final String BOOK_DETAILS = "bookDetails";

    @Inject
    private Bookstore bookstore;

    private Book selectedBook;
    private final List<Book> books = new ArrayList<>();
    private String isbnString = "", keywords = "", message = "";

    public void setIsbn(String isbn) {
        this.isbnString = isbn;
    }

    public String getIsbn() {
        return isbnString;
    }

    public void setKeywords(String book) {
        this.keywords = book;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getMessage() {
        return message;
    }

    public List<Book> getBooks() {
        return books;
    }

    public Book getSelectedBook() {
        return selectedBook;
    }

    public String selectBook(Book selectedBook) {
        this.selectedBook = selectedBook;
        return BOOK_DETAILS;
    }

    public String findBook() {
        books.clear();
        try {
            if (!isbnString.isEmpty()) {
                books.add(bookstore.findBook(isbnString));
            }
        } catch (BookNotFoundException e) {
        }
        if (!keywords.isEmpty()) {
            books.addAll(bookstore.searchBooks(keywords));
        }
        if (books.isEmpty()) {
            message = NOT_FOUND_EXCEPTION;
            return null;
        } else {
            message = "";
            return null;
        }
    }

}
