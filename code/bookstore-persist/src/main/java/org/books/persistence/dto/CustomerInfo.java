package org.books.persistence.dto;

import java.io.Serializable;

public class CustomerInfo implements Serializable {

    public static final String CUSTOMER_FIND_BY_NAME_PART_QUERY = "org.books.persistence.CustomerByNamePart";
    public transient static final String CUSTOMER_FIND_BY_NAME_PART_PARAM = "name";

    private Long id;

    private String firstName;
    private String lastName;
    private String email;

    public CustomerInfo(Long id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Long getId() {
        return id;
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
