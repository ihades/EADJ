package org.books.persistence.entity;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "ORDERITEM")
public class OrderItem extends IdentifiableObject {

    @ManyToOne
    @JoinColumn(name = "bookId",
            nullable = false)
    private Book book;

    @Column(nullable = false,
            precision = 7,
            scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer quantity;

    public OrderItem() {
    }

    public OrderItem(Book book, Integer quantity) {
        this.book = book;
        this.quantity = quantity;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "LineItem{" + "book=" + book + ", quantity=" + quantity + '}';
    }
}
