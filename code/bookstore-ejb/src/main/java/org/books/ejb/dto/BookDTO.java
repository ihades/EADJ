package org.books.ejb.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "book")
@XmlType(propOrder = {"isbn", "title", "authors", "publisher", "publicationYear", "binding", "numberOfPages", "price"})
public class BookDTO implements Serializable {

    @XmlType(name = "BookBinding", propOrder = {"Paperback", "Hardcover", "Ebook", "Unknown"})
    @XmlEnum
    public enum Binding {

        Hardcover, Paperback, Ebook, Unknown
    }
    
    private String isbn;
    private String title;
    private String authors;
    private String publisher;
    private Integer publicationYear;
    private Binding binding;
    private Integer numberOfPages;
    private BigDecimal price;

    public BookDTO() {
    }

    public BookDTO(String isbn, String title, String authors, String publisher,
            Integer publicationYear, Binding binding, Integer numberOfPages, BigDecimal price) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.binding = binding;
        this.numberOfPages = numberOfPages;
        this.setPrice(price);
    }
    
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public final void setPrice(BigDecimal price) {
        this.price = price.setScale(2, RoundingMode.HALF_UP);
    }
}
