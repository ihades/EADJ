/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.books.data.Book;

/**
 *
 * @author cb
 */
public class ShoppingBasket {

    private final Map<Book, Integer> basket = new HashMap<>();

    public int increment(Book buch) {
        if (!basket.containsKey(buch)) {
            basket.put(buch, 1);
        } else {
            basket.put(buch, basket.get(buch) + 1);
        }
        return basket.get(buch);
    }

    public int decrement(Book buch) {
        if (basket.containsKey(buch)) {
            basket.put(buch, basket.get(buch) - 1);
            if (basket.get(buch) == 0) {
                basket.remove(buch);
                return 0;
            } else {
                return basket.get(buch);
            }
        } else {
            return 0;
        }
    }

    public int getCount(Book buch) {
        if (basket.containsKey(buch)) {
            return basket.get(buch);
        } else {
            return 0;
        }
    }

    public void remove(Book buch) {
        if (basket.containsKey(buch)) {
            basket.remove(buch);
        }
    }

    public List<BookEntry> getBooks() {
        List<BookEntry> result = new ArrayList<>();
        for (Entry<Book, Integer> entry : basket.entrySet()) {
            result.add(new BookEntry(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    boolean isEmpty() {
        return basket.isEmpty();
    }

    public static class BookEntry {

        Book book;
        Integer count;

        private BookEntry(Book book, Integer count) {
            this.book = book;
            this.count = count;
        }

        public Book getBook() {
            return book;
        }

        public Integer getCount() {
            return count;
        }

    }

}
