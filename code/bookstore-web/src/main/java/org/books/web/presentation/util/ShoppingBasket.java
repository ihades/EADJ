/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.web.presentation.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.books.web.data.dto.BookInfo;
import org.books.web.data.dto.OrderItemDTO;

/**
 *
 * @author cb
 */
public class ShoppingBasket implements Serializable {

    private final List<OrderItemDTO> basket = new ArrayList<>();
    private final int maxOrderPerBook;

    public ShoppingBasket(int maxOrderPerBook) {
        this.maxOrderPerBook = maxOrderPerBook;
    }

    public int increment(BookInfo buch) throws MaxOrderException {
        for (OrderItemDTO bookEntry : basket) {
            if (bookEntry.getBook().equals(buch)) {
                if (bookEntry.getQuantity() >= maxOrderPerBook) {
                    throw new MaxOrderException();
                }
                bookEntry.setQuantity(bookEntry.getQuantity() + 1);
                return bookEntry.getQuantity();
            }
        }
        basket.add(new OrderItemDTO(buch, 1));
        return 1;
    }

    public int decrement(BookInfo buch) {
        OrderItemDTO foundBook = null;
        for (OrderItemDTO bookEntry : basket) {
            if (bookEntry.getBook().equals(buch)) {
                foundBook = bookEntry;
            }
        }
        if ((foundBook != null) && (foundBook.getQuantity() > 1)) {
            foundBook.setQuantity(foundBook.getQuantity() - 1);
            return foundBook.getQuantity();
        } else {
            basket.remove(foundBook);
            return 0;
        }
    }

    public void remove(OrderItemDTO buch) {
        basket.remove(buch);
    }

    public List<OrderItemDTO> getBooks() {
        return basket;
    }

    public boolean isEmpty() {
        return basket.isEmpty();
    }

    public BigDecimal getTotalPrice() {
        BigDecimal price = BigDecimal.ZERO;
        for (OrderItemDTO book : basket) {
            price = price
                    .add(BigDecimal.valueOf(book.getQuantity())
                            .multiply(book.getBook().getPrice()));
        }
        return price;
    }
}
