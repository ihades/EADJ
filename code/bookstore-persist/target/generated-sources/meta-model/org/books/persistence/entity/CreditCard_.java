package org.books.persistence.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import org.books.persistence.entity.CreditCard.Type;

@Generated(value="EclipseLink-2.6.1.v20150916-rNA", date="2015-12-14T21:03:26")
@StaticMetamodel(CreditCard.class)
public class CreditCard_ { 

    public static volatile SingularAttribute<CreditCard, Integer> expirationYear;
    public static volatile SingularAttribute<CreditCard, String> number;
    public static volatile SingularAttribute<CreditCard, Integer> expirationMonth;
    public static volatile SingularAttribute<CreditCard, Type> type;

}