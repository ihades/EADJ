/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.books.application.BookNotFoundException;
import org.books.application.Bookstore;
import org.books.data.Book;

/**
 *
 * @author edm
 */
@Named("catalogBean")
@SessionScoped
public class CatalogBean implements Serializable {
    
    private String isbn, search;
    
    private Book book = new Book();
    private List<Book> books;
    
    @Inject
    private Bookstore bookstore;
    
    
    public String getIsbn(){
        if (isbn != null && !isbn.equals("")) {
            return isbn;
        }
        return "";
    }
    
    public void setIsbn(String isbn){
        this.isbn = isbn;
    }
    
    public String getSearch(){
        return search;
    }
    
    public void setSearch(String s){
        this.search = s;
    }
    
    
    public Book getBook() {
       return book;
    }
    
    public List<Book> getBooks(){
        return books;
    }
    
    public String findBook () {
        try {
            this.book = bookstore.findBook(isbn);
        } catch (BookNotFoundException ex) {
            this.book = new Book();
            Logger.getLogger(CatalogBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "bookdetails";
    }
    
    public String findWildcardBook () {
        books = bookstore.searchBooks(search);
        return "catalogSearch";
    }
    public String setSelectedBook(Book b) {
        this.book = b;
        return "bookdetails";
    }
    
    
    
    
}
