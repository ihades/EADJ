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
import org.books.data.dto.BookInfo;
import org.books.data.dto.OrderItemDTO;

/**
 *
 * @author cb
 */
@Named(value = "shoppingBasketBean")
@SessionScoped
public class ShoppingBasketBean implements Serializable {

    private ShoppingBasket basket = new ShoppingBasket();

    /**
     * Creates a new instance of ShoppingBasketBean
     */
    public ShoppingBasketBean() {
    }

    public void addBookToBasket(BookInfo book) {
        basket.increment(book);
    }

    public String removeBookFromBasket(OrderItemDTO book) {
        basket.remove(book);

        if (basket.isEmpty()) {
            return "catalogSearch";
        } else {
            return null;
        }
    }

    public List<OrderItemDTO> getBooks() {
        return basket.getBooks();
    }
    
    /**
     * Return a css class to control a link's behaviour as to
     * whether it is active or not depending on the basket's emptiness.
     * 
     * @return String {active | not-active}
     */
    public String getStyle () {
        if (getBooks().isEmpty()) {
            return "link-not-active";
        }
        return "link-active";
    }

}
