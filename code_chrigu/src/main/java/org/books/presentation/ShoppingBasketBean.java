/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import org.books.data.Book;

/**
 *
 * @author cb
 */
@Named(value = "shoppingBasketBean")
@SessionScoped
public class ShoppingBasketBean implements Serializable {

    /**
     * Creates a new instance of ShoppingBasketBean
     */
    public ShoppingBasketBean() {
    }

    public void addBookToBasket(Book book) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().getSessionMap().containsKey("shoppingBasket")) {
            context.getExternalContext().getSessionMap().put("shoppingBasket", new ShoppingBasket());
        }
        ShoppingBasket basket = (ShoppingBasket) context.getExternalContext().getSessionMap().get("shoppingBasket");

        basket.increment(book);
    }

    public String removeBookFromBasket(ShoppingBasket.BookEntry book) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().getSessionMap().containsKey("shoppingBasket")) {
            context.getExternalContext().getSessionMap().put("shoppingBasket", new ShoppingBasket());
        }
        ShoppingBasket basket = (ShoppingBasket) context.getExternalContext().getSessionMap().get("shoppingBasket");

        basket.remove(book);

        if (basket.isEmpty()) {
            return "catalogSearch";
        } else {
            return null;
        }
    }

    public List<ShoppingBasket.BookEntry> getBooks() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().getSessionMap().containsKey("shoppingBasket")) {
            context.getExternalContext().getSessionMap().put("shoppingBasket", new ShoppingBasket());
        }
        return ((ShoppingBasket) context.getExternalContext().getSessionMap().get("shoppingBasket")).getBooks();
    }

}
