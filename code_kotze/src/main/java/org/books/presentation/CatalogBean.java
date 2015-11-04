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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.books.application.BookNotFoundException;
import org.books.application.Bookstore;
import org.books.application.BookstoreException;
import org.books.data.dto.BookInfo;
import org.books.data.entity.Book;
import org.books.presentation.util.MessageFactory;

/**
 *
 * @author edm
 */
@Named("catalogBean")
@SessionScoped
public class CatalogBean implements Serializable {
    
    private String isbn, search;
    
    private Book book = new Book();
    private List<BookInfo> books;
    
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
    
    public List<BookInfo> getBooks(){
        return books;
    }
    
    public String findBook () {
        
        try {
            this.book = bookstore.findBook(isbn);
        } catch (BookstoreException ex) {
            Logger.getLogger(CatalogBean.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        return "bookdetails";
    }
    
    public String searchBook() {
        if (isbn != null) {
            return findBook();
        }
        return findWildcardBook();
    }
    
    
    public String findWildcardBook () {
        books = bookstore.searchBooks(search);
        
        //FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Bla", null);
        //FacesContext context = FacesContext.getCurrentInstance();
        //context.addMessage(null, fm);
        MessageFactory.error("javax.faces.converter.IntegerConverter.INTEGER", search,1);
        
        return "catalogSearch";
    }
    public String setSelectedBook(Book b) {
        this.book = b;
        return "bookdetails";
    }
    
    
    
    
}
