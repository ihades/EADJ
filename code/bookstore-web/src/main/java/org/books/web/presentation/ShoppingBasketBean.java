/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.web.presentation;

import org.books.web.presentation.util.ShoppingBasket;
import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import org.books.web.data.dto.BookInfo;
import org.books.web.data.dto.OrderItemDTO;
import org.books.web.presentation.util.MaxOrderException;
import org.books.web.presentation.util.MessageFactory;

/**
 *
 * @author cb
 */
@Named(value = "shoppingBasketBean")
@SessionScoped
public class ShoppingBasketBean implements Serializable {

    private static final int MAX_ORDER_PER_BOOK = 100;

    private final ShoppingBasket basket = new ShoppingBasket(MAX_ORDER_PER_BOOK);

    /**
     * Creates a new instance of ShoppingBasketBean
     */
    public ShoppingBasketBean() {
    }

    public void addBookToBasket(BookInfo book) {
        try {
            basket.increment(book);
        } catch (MaxOrderException e) {
            MessageFactory.error(e, MAX_ORDER_PER_BOOK);
        }
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
    
    public String getTotalBookCount() {
        int total = 0;
        for (OrderItemDTO bookDto : basket.getBooks()) {
            total+=bookDto.getQuantity(); 
        }
        return Integer.toString(total);
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

    public String getTotalPrice() {
        return basket.getTotalPrice().toPlainString();
    }

    public String getMaxOrderPerBook() {
        return String.valueOf(MAX_ORDER_PER_BOOK);
    }

}
