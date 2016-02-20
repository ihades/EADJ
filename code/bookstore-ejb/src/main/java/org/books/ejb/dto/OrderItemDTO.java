package org.books.ejb.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.books.persistence.dto.BookInfo;

@XmlRootElement(name = "OrderItem")
@XmlType(propOrder = {"book", "quantity"})
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

    @XmlElement(name = "bookInfo")
    public BookInfo getBook() {
        return book;
    }

    public void setBook(BookInfo book) {
        this.book = book;
    }

    @XmlTransient
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
