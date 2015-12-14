package org.books.persistence.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.books.persistence.entity.Address;
import org.books.persistence.entity.CreditCard;

@Generated(value="EclipseLink-2.6.1.v20150916-rNA", date="2015-12-14T21:03:26")
@StaticMetamodel(Customer.class)
public class Customer_ extends IdentifiableObject_ {

    public static volatile SingularAttribute<Customer, String> firstName;
    public static volatile SingularAttribute<Customer, String> lastName;
    public static volatile SingularAttribute<Customer, Address> address;
    public static volatile SingularAttribute<Customer, CreditCard> creditCard;
    public static volatile SingularAttribute<Customer, String> email;

}