/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.books.presentation;

import java.util.ArrayList;
import java.util.List;
import org.books.data.Book;

/**
 *
 * @author cb
 */
public class ShoppingBasket {

    private final List<BookEntry> basket = new ArrayList<>();

    public int increment(Book buch) {
        for (BookEntry bookEntry : basket) {
            if (bookEntry.getBook().equals(buch)) {
                bookEntry.increment();
                return bookEntry.getCount();
            }
        }
        basket.add(new BookEntry(buch, 1));
        return 1;
    }

    public int decrement(Book buch) {
        BookEntry foundBook = null;
        for (BookEntry bookEntry : basket) {
            if (bookEntry.getBook().equals(buch)) {
                foundBook = bookEntry;
            }
        }
        if ((foundBook != null) && (foundBook.getCount() > 1)) {
            foundBook.decrement();
            return foundBook.getCount();
        } else {
            basket.remove(foundBook);
            return 0;
        }
    }

    public void remove(BookEntry buch) {
        basket.remove(buch);
    }

    public List<BookEntry> getBooks() {
        return basket;
    }

    boolean isEmpty() {
        return basket.isEmpty();
    }

    public static class BookEntry {

        private Book book;
        private Integer count;

        private BookEntry(Book book, Integer count) {
            this.book = book;
            this.count = count;
        }

        private void increment() {
            count++;
        }

        private void decrement() {
            count--;
        }

        public Book getBook() {
            return book;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

    }

}
