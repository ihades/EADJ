package org.books.persistence.entity;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.books.persistence.entity.Book.Binding;

@Generated(value="EclipseLink-2.6.1.v20150916-rNA", date="2015-12-14T21:03:26")
@StaticMetamodel(Book.class)
public class Book_ extends IdentifiableObject_ {

    public static volatile SingularAttribute<Book, Integer> numberOfPages;
    public static volatile SingularAttribute<Book, BigDecimal> price;
    public static volatile SingularAttribute<Book, String> isbn;
    public static volatile SingularAttribute<Book, String> publisher;
    public static volatile SingularAttribute<Book, Integer> publicationYear;
    public static volatile SingularAttribute<Book, Binding> binding;
    public static volatile SingularAttribute<Book, String> title;
    public static volatile SingularAttribute<Book, String> authors;

}