package org.books.persistence.entity;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.books.persistence.entity.Book;

@Generated(value="EclipseLink-2.6.1.v20150916-rNA", date="2015-12-14T21:03:26")
@StaticMetamodel(OrderItem.class)
public class OrderItem_ extends IdentifiableObject_ {

    public static volatile SingularAttribute<OrderItem, BigDecimal> amount;
    public static volatile SingularAttribute<OrderItem, Integer> quantity;
    public static volatile SingularAttribute<OrderItem, Book> book;

}