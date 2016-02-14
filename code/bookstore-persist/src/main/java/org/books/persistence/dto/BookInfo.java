package org.books.persistence.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class BookInfo implements Serializable {

    public static final String QUERY_BOOKS_BY_YEAR = "org.books.persistence.dto.BookInfo.findByPublicationYear";

    private Long id;
    private String title;
    private String isbn;
    private BigDecimal price;

    /**
     * For Modelmapper Only!
     */
    protected BookInfo() {
    }

    public BookInfo(Long id, String title, String isbn, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
