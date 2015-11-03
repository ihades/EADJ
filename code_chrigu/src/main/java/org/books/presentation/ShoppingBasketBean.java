/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import org.books.presentation.util.ShoppingBasket;
import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import org.books.data.dto.BookInfo;
import org.books.data.dto.OrderItemDTO;

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

    public void addBookToBasket(BookInfo book) {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().getSessionMap().containsKey("shoppingBasket")) {
            context.getExternalContext().getSessionMap().put("shoppingBasket", new ShoppingBasket());
        }
        ShoppingBasket basket = (ShoppingBasket) context.getExternalContext().getSessionMap().get("shoppingBasket");

        basket.increment(book);
    }

    public String removeBookFromBasket(OrderItemDTO book) {
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

    public List<OrderItemDTO> getBooks() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!context.getExternalContext().getSessionMap().containsKey("shoppingBasket")) {
            context.getExternalContext().getSessionMap().put("shoppingBasket", new ShoppingBasket());
        }
        return ((ShoppingBasket) context.getExternalContext().getSessionMap().get("shoppingBasket")).getBooks();
    }

}
