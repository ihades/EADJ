package org.books.persistence.dto;

import java.io.Serializable;

public class CustomerInfo implements Serializable {

    public static final String CUSTOMER_FIND_BY_NAME_PART_QUERY = "org.books.persistence.dto.CustomerInfo.findByNamePart";
    public transient static final String CUSTOMER_FIND_BY_NAME_PART_PARAM = "name";

    private final String number;

    private String firstName;
    private String lastName;
    private String email;

    public CustomerInfo(String number, String firstName, String lastName, String email) {
        this.number = number;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        firstName = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        lastName = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String mail) {
        email = mail;
    }

}
