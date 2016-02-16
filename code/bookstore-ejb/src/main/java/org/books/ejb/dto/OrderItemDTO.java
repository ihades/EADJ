package org.books.ejb.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlRootElement;
import org.books.persistence.dto.BookInfo;

@XmlRootElement
public class OrderItemDTO implements Serializable {

    private BookInfo book;
    private BigDecimal price;
    private Integer quantity;

    public OrderItemDTO() {
    }

    public OrderItemDTO(BookInfo book, BigDecimal price, Integer quantity) {
        this.book = book;
        this.price = price;
        this.quantity = quantity;
    }

    public BookInfo getBook() {
        return book;
    }

    public void setBook(BookInfo book) {
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
