package org.books.persistence.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.LockModeType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@NamedQueries({
    @NamedQuery(name = "Book.findByIsbn", query = "SELECT b "
            + "FROM Book b "
            + "WHERE UPPER(b.isbn) = UPPER(:isbn)"),
    @NamedQuery(name = "Book.findByIsbnPessimisticLock", query = "SELECT b "
            + "FROM Book b "
            + "WHERE UPPER(b.isbn) = UPPER(:isbn)", lockMode = LockModeType.PESSIMISTIC_WRITE)
})
@Entity
public class Book extends IDObject {

    public transient static final String BOOK_FIND_BY_ISBN_PARAM = "isbn";

    public enum Binding {

        Hardcover, Paperback, Unknown
    }

    @Column(unique = true,
            nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String authors;

    @Column(nullable = false)
    private String publisher;

    @Column(nullable = false)
    private Integer publicationYear;

    @Enumerated(EnumType.STRING)
    private Binding binding;

    @Column(nullable = false)
    private Integer numberOfPages;

    @Column(nullable = false,
            precision = 7,
            scale = 2)
    private BigDecimal price;

    public Book() {
    }

    public Book(String isbn, String title, String authors, String publisher,
            Integer publicationYear, Binding binding, Integer numberOfPages, BigDecimal price) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.binding = binding;
        this.numberOfPages = numberOfPages;
        this.price = price;
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

    public void setPrice(BigDecimal price) {
        this.price = price.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.isbn);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Book other = (Book) obj;
        if (!Objects.equals(this.isbn, other.isbn)) {
            return false;
        }
        return true;
    }

    public boolean isComplete() {
        return this.getIsbn() != null
                && this.getBinding() != null
                && this.getNumberOfPages() != null
                && this.getPrice() != null
                && this.getTitle() != null
                && this.getPublicationYear() != null
                && this.getAuthors() != null
                && this.getAuthors().length() > 0
                && this.getPublisher() != null;
    }
}
