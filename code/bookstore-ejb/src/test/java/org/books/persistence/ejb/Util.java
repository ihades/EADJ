package org.books.persistence.ejb;

import java.util.Properties;

public class Util {

    //<services>
//    public static final String CATALOG_SERVICE_NAME = "java:global/bookstore-app/bookstore-ejb/CatalogService!org.books.ejb.CatalogServiceRemote";
//    public static final String ORDER_SERVICE_NAME = "java:global/bookstore-app/bookstore-ejb/OrderService!org.books.ejb.OrderServiceRemote";
//    public static final String CUSTOMER_SERVICE_NAME = "java:global/bookstore-app/bookstore-ejb/CustomerService!org.books.ejb.CustomerServiceRemote";
//    public static final String AMAZON_CATALOG_SERVICE_NAME = "java:global/bookstore-app/bookstore-ejb/AmazonCatalogService!org.books.ejb.AmazonCatalogServiceRemote";
    
    
    public static final String CATALOG_SERVICE_NAME = "java:global/bookstore-app-1.0.0/bookstore-ejb/CatalogService!org.books.ejb.CatalogServiceRemote";
    public static final String ORDER_SERVICE_NAME = "java:global/bookstore-app-1.0.0/bookstore-ejb/OrderService!org.books.ejb.OrderServiceRemote";
    public static final String CUSTOMER_SERVICE_NAME = "java:global/bookstore-app-1.0.0/bookstore-ejb/CustomerService!org.books.ejb.CustomerServiceRemote";
    public static final String AMAZON_CATALOG_SERVICE_NAME = "java:global/bookstore-app-1.0.0/bookstore-ejb/AmazonCatalogService!org.books.ejb.AmazonCatalogServiceRemote";
    //</services>
    public static Properties getInitProperties() {
        Properties result = new Properties();
        result.setProperty("java.naming.factory.initial",
                "com.sun.enterprise.naming.SerialInitContextFactory");
        result.setProperty("java.naming.factory.url.pkgs",
                "com.sun.enterprise.naming");
        result.setProperty("java.naming.factory.state",
                "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
        result.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");
        result.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
        return result;
    }

    public static String numbGen() {
        while (true) {
            long numb = (long) (Math.random() * 100000000 * 1000000); // had to use this as int's are to small for a 13 digit number.
            if (String.valueOf(numb).length() == 13) {
                return String.valueOf(numb);
            }
        }
    }

    public static String invalidISBNGenerator() {
        return numbGen().substring(1);
    }
}
