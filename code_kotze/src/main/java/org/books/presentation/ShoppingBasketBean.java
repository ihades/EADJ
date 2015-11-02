/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.books.data.Book;

/**
 *
 * @author edm
 */
@Named("shoppingBasketBean")
@SessionScoped
public class ShoppingBasketBean implements Serializable {
    
    private String basketName = "shoppingBasket";
    //private List<Book> basket = new ArrayList<>();
    
    private int quantity = 0;
    private Book currentBook;

    public String getBasketName() {
        return basketName;
    }

    public void setBasketName(String basketName) {
        this.basketName = basketName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Book getCurrentBook() {
        return currentBook;
    }

    public void setCurrentBook(Book currentBook) {
        this.currentBook = currentBook;
    }
    
    
    
    public void setQuantityOfBooks() {
        getShoppingBasket().setQuantity(currentBook, quantity);
    }
    
    private ShoppingBasket getShoppingBasket() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().getSessionMap().containsKey(basketName)) {
            context.getExternalContext().getSessionMap().put(basketName, new ShoppingBasket());
        }
        return (ShoppingBasket) context.getExternalContext().getSessionMap().get(basketName);
    }
    
    public void addBookToBasket(Book b) {
        getShoppingBasket().increment(b);
    }
    
    public String removeBookFromBasket(Book b) {
        getShoppingBasket().remove(b);
        
        if (getShoppingBasket().isEmpty()) {
            return "catalogSearch";
        }
        return null;
    }
    
    public List<ShoppingBasket.BookEntry> getBooks() {
        return getShoppingBasket().getBooks();
    }
    
}
