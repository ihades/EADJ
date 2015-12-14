package org.books.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.books.persistence.entity.Address;
import org.books.persistence.entity.CreditCard;
import org.books.persistence.entity.Customer;
import org.books.persistence.entity.Order.Status;
import org.books.persistence.entity.OrderItem;

@Generated(value="EclipseLink-2.6.1.v20150916-rNA", date="2015-12-14T21:03:26")
@StaticMetamodel(Order.class)
public class Order_ extends IdentifiableObject_ {

    public static volatile SingularAttribute<Order, Date> date;
    public static volatile SingularAttribute<Order, String> number;
    public static volatile SingularAttribute<Order, BigDecimal> amount;
    public static volatile SingularAttribute<Order, Address> address;
    public static volatile SingularAttribute<Order, CreditCard> creditCard;
    public static volatile ListAttribute<Order, OrderItem> items;
    public static volatile SingularAttribute<Order, Status> status;
    public static volatile SingularAttribute<Order, Customer> customer;

}