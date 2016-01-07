package org.books.persistence.ejb;

import java.util.Properties;

public class Util {

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
}
