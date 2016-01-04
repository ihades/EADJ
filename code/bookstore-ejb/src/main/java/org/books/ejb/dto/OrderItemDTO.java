package org.books.ejb.dto;

import java.math.BigDecimal;

public class OrderItemDTO {

    private BookDTO book;
    private BigDecimal price;
    private Integer quantity;

    public OrderItemDTO() {
    }

    public OrderItemDTO(BookDTO book, BigDecimal price, Integer quantity) {
        this.book = book;
        this.price = price;
        this.quantity = quantity;
    }

    public BookDTO getBook() {
        return book;
    }

    public void setBook(BookDTO book) {
        this.book = book;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
