package org.books.persistence.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.6.1.v20150916-rNA", date="2015-12-14T21:03:26")
@StaticMetamodel(Login.class)
public class Login_ extends IdentifiableObject_ {

    public static volatile SingularAttribute<Login, String> password;
    public static volatile SingularAttribute<Login, String> userName;
    public static volatile SingularAttribute<Login, String> groupname;

}